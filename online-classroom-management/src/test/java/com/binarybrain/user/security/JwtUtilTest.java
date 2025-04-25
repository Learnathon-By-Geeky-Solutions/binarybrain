package com.binarybrain.user.security;

import com.binarybrain.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {
    @Autowired
    private JwtUtil jwtUtil;

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
    void generateToken() {
        String token = jwtUtil.generateToken(getUser());
        assertNotNull(token);
    }

    @Test
    void createToken() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        map.put("password","sample");
        String token = jwtUtil.createToken(map,"sample",1000L);
        assertNotNull(token);
    }

    @Test
    void extractUsername() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",1000L);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
    }

    @Test
    void extractExpiration() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",1000L);
        Date date = jwtUtil.extractExpiration(token);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
        assertNotNull(date);
    }

    @Test
    void extractClaim() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",1000L);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
    }

    @Test
    void extractAllClaims() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",1000L);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
    }

    @Test
    void validateToken() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",10000000000L);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
        jwtUtil.validateToken(token,getUser());
    }

    @Test
    void validateToken_When_Username_Malformed() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample-dummy");
        String token = jwtUtil.createToken(map,"sample-dummy",100000000000L);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
        jwtUtil.validateToken(token,getUser());
    }

    @Test
    void isTokenExpired() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",1000000000000L);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
        Boolean ok = jwtUtil.isTokenExpired(token);
        assertNotNull(ok);
    }
    @Test
    void isTokenExpired_When_Expired() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",-10);
        assertTrue(jwtUtil.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_When_TokenIsWrong() {
        Map<String,Object> map=new HashMap<>();
        map.put("username","sample");
        String token = jwtUtil.createToken(map,"sample",1000000000L);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
        assertThrows(InvalidTokenException.class,()-> jwtUtil.isTokenExpired("token"));
    }

    @Test
    void validateToken_InvalidToken() {
        assertFalse(jwtUtil.validateToken("invalidToken",getUser()));
    }
}