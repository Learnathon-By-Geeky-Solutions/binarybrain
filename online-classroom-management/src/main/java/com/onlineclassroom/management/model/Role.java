package com.onlineclassroom.management.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * The {@code Role} entity represents a user role within the system. It is used to
 * store role information such as the role's unique identifier and its name.
 *
 * <p> This entity is typically mapped to a {@code roles} table in the database,
 * and each role is associated with one or more users. Roles define the level of accessa user has within the application.</p>
 *
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 */
@Entity
@Data
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
