package com.lotty.wishlysystemapi.service;

import com.example.common.dto.ParseRequestDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskStatusResponseDTO;
import com.lotty.wishlysystemapi.model.ParsingTask;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.PasringTaskDAO;
import com.lotty.wishlysystemapi.status.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParsingTaskService {


    private static final Logger logger = LoggerFactory.getLogger(ParsingTaskService.class);

    private final UserService userService;
    private final PasringTaskDAO pasringTaskDAO;

    public ParsingTaskService(UserService userService, PasringTaskDAO pasringTaskDAO) {
        this.userService = userService;
        this.pasringTaskDAO = pasringTaskDAO;
    }

    public ParsingTask createTask(
            ParseRequestDTO parseRequestDTO
    ) {
        User user = userService.findUserByIdForTask(parseRequestDTO.getUserId()); // Получаем текущего пользователя
        ParsingTask task = new ParsingTask();
        task.setUrl(parseRequestDTO.getUrl());
        task.setUser(user);
        task.setWishlistId(parseRequestDTO.getWishlistId());
        return pasringTaskDAO.save(task);
    }

    public ParsingTask updateTaskStatus(Integer taskId, TaskStatus status, String errorMessage) {
        ParsingTask task = pasringTaskDAO.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setStatus(status);
        task.setErrorMessage(errorMessage);
        return pasringTaskDAO.save(task);
    }

    public ParsingTask markAsCompleted(Integer taskId, Integer itemId) {
        ParsingTask task = pasringTaskDAO.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setStatus(TaskStatus.COMPLETED);
        task.setCreatedItemId(itemId);
        return pasringTaskDAO.save(task);
    }

    public ParsingTask getTaskById(Integer taskId) {
        return pasringTaskDAO.findById(taskId).orElseThrow(() -> {
            logger.error("Task не найден. TaskId: {}", taskId);
            return new EntityNotFoundException("Task not found with id: " + taskId);
        });
    }

    public TaskStatusResponseDTO getTaskStatus(Integer taskId) {
        ParsingTask task = pasringTaskDAO.findById(taskId).orElseThrow(() -> {
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