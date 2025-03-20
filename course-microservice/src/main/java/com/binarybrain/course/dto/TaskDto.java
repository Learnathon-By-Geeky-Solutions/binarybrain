package com.binarybrain.course.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private String attachmentUrl;
    private Long teacherId;
    private TaskStatus status;
}
