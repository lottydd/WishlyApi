package com.lotty.wishlysystemapi.service;

import com.example.common.dto.ParseRequestDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskResponseDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskStatusResponseDTO;
import com.lotty.wishlysystemapi.model.ParsingTask;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ParsingTaskDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import com.lotty.wishlysystemapi.status.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ParsingTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingTaskService.class);

    private final ParsingTaskDAO parsingTaskDAO;
    private final KafkaProducerService kafkaProducerService;
    private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;

    public ParsingTaskService( ParsingTaskDAO parsingTaskDAO, KafkaProducerService kafkaProducerService, UserDAO userDAO, WishlistDAO wishlistDAO) {
        this.parsingTaskDAO = parsingTaskDAO;
        this.kafkaProducerService = kafkaProducerService;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
    }

    @Transactional
    public TaskResponseDTO createAndSendTask(ParseRequestDTO request) {
        logger.info("Попытка парса ссылки");
        logger.info("Получен request: url={}, wishlistId={}, taskId={}",
                request.getUrl(), request.getWishlistId(), request.getTaskId());

        User currentUser = getAuthenticatedUser();
        Wishlist wishlist = validateAndGetWishlist(request.getWishlistId(), currentUser);
        ParsingTask task = createParsingTask(request, currentUser, wishlist);
        sendTaskToKafka(request, task.getTaskId());

        logger.info("Завершение обработки ParseRequest");
        return new TaskResponseDTO(task.getTaskId(), "Задача создана");
    }
    @Transactional
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            logger.error("Authentication в SecurityContext пустой!");
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }

        String username = auth.getName();
        logger.info("Пользователь из JWT: {}", username);

        return userDAO.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден в базе", username);
                    return new EntityNotFoundException("Пользователь не найден");
                });
    }

    @Transactional
    private Wishlist validateAndGetWishlist(Integer wishlistId, User currentUser) {
        Wishlist wishlist = wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> {
                    logger.error("Wishlist с id {} не найден", wishlistId);
                    return new EntityNotFoundException("Wishlist не найден");
                });

        logger.info("wishlist.id = {}, wishlist.user.id = {}", wishlist.getWishlistId(),
                wishlist.getUser().getUserId());

        if (!wishlist.getUser().getUserId().equals(currentUser.getUserId())) {
            logger.error("Нельзя парсить ссылку для чужого вишлиста. JWT userId={} != wishlist.userId={}",
                    currentUser.getUserId(), wishlist.getUser().getUserId());
            throw new AccessDeniedException("Нельзя парсить ссылку для чужого вишлиста");
        }
        return wishlist;
    }
    @Transactional
    private ParsingTask createParsingTask(ParseRequestDTO request, User currentUser, Wishlist wishlist) {
        ParsingTask task = new ParsingTask();
        //Need mapper
        task.setUrl(request.getUrl());
        task.setUser(currentUser);
        task.setWishlistId(wishlist.getWishlistId());
        task.setUpdatedAt(LocalDateTime.now());
        task = parsingTaskDAO.save(task);
        logger.info("ParsingTask создан. taskId = {}", task.getTaskId());
        return task;
    }

    private void sendTaskToKafka(ParseRequestDTO request, Integer taskId) {
        ParseRequestDTO kafkaDTO = createKafkaRequestDTO(
                request.getUrl(),
                request.getWishlistId(),
                taskId
        );
        kafkaProducerService.sendParseRequest(kafkaDTO);
    }

    @Transactional
    private ParseRequestDTO createKafkaRequestDTO(String url, Integer wishlistId, Integer taskId) {
        ParseRequestDTO kafkaDTO = new ParseRequestDTO();
        kafkaDTO.setUrl(url);
        kafkaDTO.setWishlistId(wishlistId);
        kafkaDTO.setTaskId(taskId);
        return kafkaDTO;
    }

    @Transactional
    public void updateTaskStatusToFailed(Integer taskId, TaskStatus status, String errorMessage) {
        ParsingTask task = parsingTaskDAO.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setStatus(status);
        task.setErrorMessage(errorMessage);
        parsingTaskDAO.save(task);
    }

    @Transactional
    public void markAsCompleted(Integer taskId, Integer itemId) {
        ParsingTask task = parsingTaskDAO.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setStatus(TaskStatus.COMPLETED);
        task.setCreatedItemId(itemId);
        parsingTaskDAO.save(task);
    }

    @Transactional
    public TaskStatusResponseDTO getTaskStatus(Integer taskId) {
        ParsingTask task = parsingTaskDAO.findById(taskId).orElseThrow(() -> {
            logger.error("Task не найден.  TaskId: {}", taskId);
            return new EntityNotFoundException("Task not found with id: " + taskId);
        });
        return new TaskStatusResponseDTO(task.getTaskId(),
                task.getErrorMessage(),
                task.getCreatedItemId(),
                task.getStatus()
        );
    }

    @Transactional
    public ParsingTask getTaskById(Integer taskId) {
        return parsingTaskDAO.findById(taskId).orElseThrow(() -> {
            logger.error("Task не найден. TaskId: {}", taskId);
            return new EntityNotFoundException("Task not found with id: " + taskId);
        });
    }
}