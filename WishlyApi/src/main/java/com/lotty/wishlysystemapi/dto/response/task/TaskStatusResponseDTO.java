package com.lotty.wishlysystemapi.dto.response.task;

import com.lotty.wishlysystemapi.status.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskStatusResponseDTO {
    private Integer taskId;
    private TaskStatus status;
    private String errorMessage;
    private Integer createdItemId;
    private LocalDateTime createdAt;

    public TaskStatusResponseDTO(Integer taskId,  String errorMessage,Integer createdItemId, TaskStatus status) {
        this.taskId = taskId;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdItemId = createdItemId;
    }
}