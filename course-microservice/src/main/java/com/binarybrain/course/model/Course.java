package com.binarybrain.course.model;

import com.binarybrain.course.dto.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "Long")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String code;

    private String description;

    @Schema(example = "[\"OPEN\", \"CLOSED\"]")
    private CourseStatus status;

    @Column(nullable = false)
    @Schema(example = "Long")
    private Long createdBy;

    @ElementCollection
    @CollectionTable(name = "course_tasks", joinColumns = @JoinColumn(name = "course_id"))
    @Schema(example = "[\"Long\"]")
    private Set<Long> taskIds = new HashSet<>();
}
