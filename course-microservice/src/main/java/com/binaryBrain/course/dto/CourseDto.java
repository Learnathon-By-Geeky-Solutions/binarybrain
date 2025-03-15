package com.binaryBrain.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class CourseDto {
    private Long id;
    @NotBlank(message = "Title is required!")
    private String title;
    @NotBlank(message = "Course code is required!")
    private String code;
    private String description;
    private CourseStatus status;
    private Long createdBy;
    private Set<Long> taskIds;
}

