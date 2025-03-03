package com.binaryBrain.classroom_microservice.dto;

import lombok.Data;

@Data
public class CourseDto {
    private Long id;
    private String title;
    private String code;
    private String description;
    private CourseStatus status;
    private Long createdBy;
}
