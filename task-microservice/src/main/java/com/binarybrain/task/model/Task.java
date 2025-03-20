package com.binarybrain.task.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private Long teacherId;
    private String attachmentUrl;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}