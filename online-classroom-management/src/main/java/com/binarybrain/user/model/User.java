package com.binarybrain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

/**
 * The {@code User} class represents a user in the Online Classroom Management system.
 *
 * <p> This entity is typically mapped to a {@code users} table in the database,
 * with columns corresponding to the fields in this class. The user can have
 * multiple roles(ADMIN, TEACHER, STUDENT, and the relationships are managed via the {@code user_roles} join table.
 * </p>
 */
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "Long")
    private Long id;

    @Column(nullable = false)
    private  String firstName;

    @Column(nullable = false)
    private String lastName;

    private String currentInstitute;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String gender;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    private String profilePicture;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

}
