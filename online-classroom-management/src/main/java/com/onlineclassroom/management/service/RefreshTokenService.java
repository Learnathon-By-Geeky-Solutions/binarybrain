package com.onlineclassroom.management.service;

import com.onlineclassroom.management.model.RefreshToken;
import com.onlineclassroom.management.model.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
}
