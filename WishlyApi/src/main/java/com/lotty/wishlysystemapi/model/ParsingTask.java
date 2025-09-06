package com.lotty.wishlysystemapi.model;

import com.lotty.wishlysystemapi.status.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "parsing_tasks")
public class ParsingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer wishlistId;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String errorMessage;
    private Integer createdItemId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = TaskStatus.PROCESSING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}