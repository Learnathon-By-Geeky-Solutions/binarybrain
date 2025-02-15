package com.binaryBrain.classroom_microservice.dto;

import java.time.LocalDate;
import java.util.List;
public class ClassroomDto {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate expireDate;
    private Long courseId;
    private List<Long> studentIds;
    private List<ResourceDto> resources;
}
