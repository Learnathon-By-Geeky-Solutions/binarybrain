package com.binarybrain.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="UserImage64")
public class UserImage {
    @Id
    private Long id;
    @Column(unique = true, nullable = false)
    String username;
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageBase64;
}
