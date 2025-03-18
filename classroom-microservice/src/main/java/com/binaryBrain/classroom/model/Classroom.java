package com.binaryBrain.classroom.model;

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
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDate startDate;

    @Column(nullable = false)
    private Long teacherId;

    @ElementCollection
    @CollectionTable(name = "classroom_students", joinColumns = @JoinColumn(name = "classroom_id"))
    private Set<Long> studentIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "classroom_courses", joinColumns = @JoinColumn(name = "classroom_id"))
    private Set<Long> courseIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "classroom_resources", joinColumns = @JoinColumn(name = "classroom_id"))
    private Set<Long> resourceIds = new HashSet<>();
}
