package com.lotty.wishlysystemapi.service;

import com.example.common.dto.ParseRequestDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskResponseDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskStatusResponseDTO;
import com.lotty.wishlysystemapi.model.ParsingTask;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ParsingTaskDAO;
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
@Transactional
public class ParsingTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingTaskService.class);

    private final UserService userService;
    private final ParsingTaskDAO parsingTaskDAO;
    private final KafkaProducerService kafkaProducerService;
    private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;

    public ParsingTaskService(UserService userService, ParsingTaskDAO parsingTaskDAO, KafkaProducerService kafkaProducerService, UserDAO userDAO, WishlistDAO wishlistDAO) {
        this.userService = userService;
        this.parsingTaskDAO = parsingTaskDAO;
        this.kafkaProducerService = kafkaProducerService;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
    }
    @Transactional
    public TaskResponseDTO createAndSendTask(ParseRequestDTO request) {
        logger.info("=== Начало обработки ParseRequest ===");
        logger.info("Получен request: url={}, wishlistId={}, taskId={}",
                request.getUrl(), request.getWishlistId(), request.getTaskId());

        // Берём пользователя из токена
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            logger.error("Authentication в SecurityContext пустой!");
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }

        String username = auth.getName();
        logger.info("Пользователь из JWT: {}", username);

        User currentUser = userDAO.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден в базе", username);
                    return new EntityNotFoundException("Пользователь не найден");
                });

        logger.info("currentUser.id = {}", currentUser.getUserId());

        // Проверяем вишлист
        Wishlist wishlist = wishlistDAO.findById(request.getWishlistId())
                .orElseThrow(() -> {
                    logger.error("Wishlist с id {} не найден", request.getWishlistId());
                    return new EntityNotFoundException("Wishlist не найден");
                });

        logger.info("wishlist.id = {}, wishlist.user.id = {}", wishlist.getWishlistId(),
                wishlist.getUser().getUserId());

        if (!wishlist.getUser().getUserId().equals(currentUser.getUserId())) {
            logger.error("Нельзя парсить чужой вишлист. JWT userId={} != wishlist.userId={}",
                    currentUser.getUserId(), wishlist.getUser().getUserId());
            throw new AccessDeniedException("Нельзя парсить чужой вишлист");
        }

        // Создаём задачу
        ParsingTask task = new ParsingTask();
        task.setUrl(request.getUrl());
        task.setUser(currentUser);
        task.setWishlistId(wishlist.getWishlistId());
        task.setUpdatedAt(LocalDateTime.now());
        task = parsingTaskDAO.save(task);

        logger.info("ParsingTask создан. taskId = {}", task.getTaskId());

        // Отправляем в Kafka
        ParseRequestDTO kafkaDTO = new ParseRequestDTO();
        kafkaDTO.setUrl(request.getUrl());
        kafkaDTO.setWishlistId(request.getWishlistId());
        kafkaDTO.setTaskId(task.getTaskId());
        kafkaProducerService.sendParseRequest(kafkaDTO);

        logger.info("=== Завершение обработки ParseRequest ===");

        return new TaskResponseDTO(task.getTaskId(), "Задача создана");
    }





    public ParsingTask createTask(ParseRequestDTO parseRequestDTO) {
        // Берём пользователя из токена
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // sub из JWT
        User user = userService.findByUsername(username);

        // Создаём задачу
        ParsingTask task = new ParsingTask();
        task.setUrl(parseRequestDTO.getUrl());
        task.setUser(user);
        task.setWishlistId(parseRequestDTO.getWishlistId());

        return parsingTaskDAO.save(task);
    }

    public ParsingTask updateTaskStatus(Integer taskId, TaskStatus status, String errorMessage) {
        ParsingTask task = parsingTaskDAO.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setStatus(status);
        task.setErrorMessage(errorMessage);
        return parsingTaskDAO.save(task);
    }

    public ParsingTask markAsCompleted(Integer taskId, Integer itemId) {
        ParsingTask task = parsingTaskDAO.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setStatus(TaskStatus.COMPLETED);
        task.setCreatedItemId(itemId);
        return parsingTaskDAO.save(task);
    }

    public ParsingTask getTaskById(Integer taskId) {
        return parsingTaskDAO.findById(taskId).orElseThrow(() -> {
            logger.error("Task не найден. TaskId: {}", taskId);
            return new EntityNotFoundException("Task not found with id: " + taskId);
        });
    }

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
}