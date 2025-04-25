package com.binarybrain.classroom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassroomDto {
    @NotBlank(message = "Title is required!")
    private String title;
    private String description;
}
