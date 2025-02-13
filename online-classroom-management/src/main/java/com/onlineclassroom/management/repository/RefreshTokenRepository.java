package com.onlineclassroom.management.repository;

import com.onlineclassroom.management.model.RefreshToken;
import com.onlineclassroom.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
