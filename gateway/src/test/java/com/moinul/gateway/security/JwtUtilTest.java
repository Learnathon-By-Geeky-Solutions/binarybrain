package com.moinul.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${jwt.secret}")
    private String secretKey;
    public String generateToken(UserDetails userDetails,Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return createToken(claims, userDetails.getUsername(), expiration);
    }

    public String createToken(Map<String, Object> claims, String subject, long expire){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private User getUser() {
        return new User(
                "sample",
                "sample",
                new HashSet<>()
        );
    }

    @Test
    void init() {
        assertNotNull(jwtUtil);
        assertDoesNotThrow(() -> jwtUtil.init());
    }

    @Test
    void extractUsername() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "sample");
        String token=createToken(claims, getUser().getUsername(), 10000000000L);
        String username = jwtUtil.extractUsername(token);
        assertEquals(getUser().getUsername(), username);
    }

    @Test
    void extractExpiration() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "sample");
        String token=createToken(claims, getUser().getUsername(), 10000000000L);
        Date date = jwtUtil.extractExpiration(token);
        assertEquals(getUser().getUsername(), jwtUtil.extractUsername(token));
        assertNotNull(date);
    }

    @Test
    void extractClaim() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "sample");
        String token=createToken(claims, getUser().getUsername(), 10000000000L);
        String claim = jwtUtil.extractClaim(token, Claims::getSubject);
        assertEquals(getUser().getUsername(), claim);
    }

    @Test
    void extractAllClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "sample");
        String token=createToken(claims, getUser().getUsername(), 10000000000L);
        Claims claims1 = jwtUtil.extractAllClaims(token);
        assertEquals(getUser().getUsername(), claims1.getSubject());
    }

    @Test
    void validateToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "sample");
        String token=createToken(claims, getUser().getUsername(), 10000000000L);
        boolean valid = jwtUtil.validateToken(token);
        assertTrue(valid);
    }

    @Test
    void isTokenExpired() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "sample");
        String token=createToken(claims, getUser().getUsername(), -10000L);
        assertThrows(ExpiredJwtException.class,()->jwtUtil.isTokenExpired(token));
    }

    @Test
    void validateToken_InvalidToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "sample");
        String token=createToken(claims, getUser().getUsername(), -10000L);
        assertThrows(JwtException.class,()->jwtUtil.validateToken("Invalid "+token));
    }

}