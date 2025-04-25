package com.binarybrain.task.dto;
import com.binarybrain.task.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    @Schema(hidden = true)
    private Long id;
    @Schema(example = "String")
    private String title;
    @Schema(example = "String")
    private String description;
    @Schema(hidden = true)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(example = "2025-12-30T23:59:59")
    private LocalDateTime deadline;

    @Schema(example = "www.example.com")
    private String attachmentUrl;

    @Schema(hidden = true)
    private Long teacherId;
    @Schema(example = "OPEN")
    private TaskStatus status;

}
