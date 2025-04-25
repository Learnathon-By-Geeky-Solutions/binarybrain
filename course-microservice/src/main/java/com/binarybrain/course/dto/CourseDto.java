package com.binarybrain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class CourseDto {
    @Schema(hidden = true)
    private Long id;
    @Schema(example = "String")
    @NotBlank(message = "Title is required!")
    private String title;
    @Schema(example = "String")
    @NotBlank(message = "Course code is required!")
    private String code;
    @Schema(example = "String")
    private String description;
    @Schema(example = "OPEN")
    private CourseStatus status;
    @Schema(hidden = true)
    private Long createdBy;
    @Schema(hidden = true)
    private Set<Long> taskIds;
}

