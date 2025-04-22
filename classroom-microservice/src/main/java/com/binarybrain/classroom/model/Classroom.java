package com.binarybrain.classroom.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Schema(hidden = true)
    @Column(nullable = false)
    private LocalDate startDate;

    @Schema(hidden = true)
    @Column(nullable = false)
    private Long teacherId;

    @Schema(hidden = true)
    @ElementCollection
    @CollectionTable(name = "classroom_students", joinColumns = @JoinColumn(name = "classroom_id"))
    private Set<Long> studentIds = new HashSet<>();

    @Schema(hidden = true)
    @ElementCollection
    @CollectionTable(name = "classroom_courses", joinColumns = @JoinColumn(name = "classroom_id"))
    private Set<Long> courseIds = new HashSet<>();

    @Schema(hidden = true)
    @ElementCollection
    @CollectionTable(name = "classroom_resources", joinColumns = @JoinColumn(name = "classroom_id"))
    private Set<Long> resourceIds = new HashSet<>();
}
