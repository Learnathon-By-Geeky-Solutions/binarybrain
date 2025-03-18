package com.binarybrain.user.service;

import com.binarybrain.user.model.RefreshToken;
import com.binarybrain.user.model.User;
import com.binarybrain.user.repository.RefreshTokenRepository;
import com.binarybrain.user.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private RefreshToken refreshToken;
    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600)); // 1 hour from now
        refreshToken.setUser(user);
    }

    @Test
    void createRefreshToken_Success() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken(user);

        assertNotNull(createdToken);
        assertEquals(refreshToken.getToken(), createdToken.getToken());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void findByToken_Success() {
        when(refreshTokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken(refreshToken.getToken());

        assertTrue(foundToken.isPresent());
        assertEquals(refreshToken.getToken(), foundToken.get().getToken());
        verify(refreshTokenRepository, times(1)).findByToken(refreshToken.getToken());
    }


    @Test
    void verifyExpiration_NotExpired() {
        refreshTokenService.verifyExpiration(refreshToken);

    }

    @Test
    void verifyExpiration_Expired() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(3600)); // 1 hour ago

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }
}
