package com.binaryBrain.course.model;

import com.binaryBrain.course.dto.CourseStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

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

    @ElementCollection
    @CollectionTable(name = "course_tasks", joinColumns = @JoinColumn(name = "course_id"))
    private Set<Long> taskIds = new HashSet<>();
}
