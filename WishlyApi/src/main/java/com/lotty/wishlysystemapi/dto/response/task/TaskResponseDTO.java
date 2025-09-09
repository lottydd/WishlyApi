package com.lotty.wishlysystemapi.dto.response.task;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Integer taskId;
    private String message;
    private LocalDateTime createdAt;
    private String statusUrl;

    public TaskResponseDTO(Integer taskId, String message) {
        this.taskId = taskId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.statusUrl = "/api/tasks/" + taskId;
    }
}