package com.binarybrain.task.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "Long")
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Schema(example = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(example = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    @Schema(example = "Long")
    private Long teacherId;

    @Schema(example = "www.example.com")
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Schema(example = "[\"OPEN\", \"CLOSED\", \"DONE\"]")
    private TaskStatus status;
}