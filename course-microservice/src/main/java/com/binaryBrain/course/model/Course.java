package com.binaryBrain.course.model;

import com.binaryBrain.course.dto.CourseStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String code;

    private String description;

    private CourseStatus status;

    @Column(nullable = false)
    private Long createdBy;
}
