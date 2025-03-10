package com.binaryBrain.task_microservice.dto;
import com.binaryBrain.task_microservice.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    private String attachmentUrl;
    private Long teacherId;
    private TaskStatus status;

}
