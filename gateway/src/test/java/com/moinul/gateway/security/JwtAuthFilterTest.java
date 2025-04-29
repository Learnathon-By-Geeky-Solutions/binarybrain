package com.moinul.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;




@SpringBootTest
class JwtAuthFilterTest {

    @MockitoBean
    ServerWebExchange exchange;
    @MockitoBean
    GatewayFilterChain chain;

    @Autowired
    JwtAuthFilter jwtAuthFilter;


    @Test
    void apply(){
        //make extractToken private method mock
        when(exchange.getRequest()).thenReturn(MockServerHttpRequest.get("/").build());
        jwtAuthFilter.apply(new JwtAuthFilter.Config());
    }

    @Test
    void shouldAllowPath_When_True() throws Exception {
        Method shouldAllowPathMethod = JwtAuthFilter.class.getDeclaredMethod("shouldAllowPath", String.class);
        shouldAllowPathMethod.setAccessible(true);

        String path = "/api/user/login";
        boolean result = (boolean) shouldAllowPathMethod.invoke(jwtAuthFilter, path);
        assertTrue(result);

        path = "/api/user/register";
        result = (boolean) shouldAllowPathMethod.invoke(jwtAuthFilter, path);
        assertTrue(result);

        path = "/api/user/refresh";
        result = (boolean) shouldAllowPathMethod.invoke(jwtAuthFilter, path);
        assertTrue(result);

        path = "/v3/api-docs";
        result = (boolean) shouldAllowPathMethod.invoke(jwtAuthFilter, path);
        assertTrue(result);
    }

    @Test
    void shouldAllowPath_When_False() throws Exception {
        Method shouldAllowPathMethod = JwtAuthFilter.class.getDeclaredMethod("shouldAllowPath", String.class);
        shouldAllowPathMethod.setAccessible(true);

        String path = "/api/user/other";
        boolean result = (boolean) shouldAllowPathMethod.invoke(jwtAuthFilter, path);
        assertFalse(result);

        path = "/api/user/login/other";
        result = (boolean) shouldAllowPathMethod.invoke(jwtAuthFilter, path);
        assertFalse(result);

        path = "/api/user/register/other";
        result = (boolean) shouldAllowPathMethod.invoke(jwtAuthFilter, path);
        assertFalse(result);

    }

    @Test
    void extractToken() throws Exception {
        Method extractTokenMethod = JwtAuthFilter.class.getDeclaredMethod("extractToken", String.class);
        extractTokenMethod.setAccessible(true);
        String authHeader = "Bearer token";
        String token = (String) extractTokenMethod.invoke(jwtAuthFilter, authHeader);
        assertEquals("token", token);
    }

    @Test
    void extractToken_When_Null() throws Exception {
        Method extractTokenMethod = JwtAuthFilter.class.getDeclaredMethod("extractToken", String.class);
        extractTokenMethod.setAccessible(true);
        String authHeader = null;
        String token = (String) extractTokenMethod.invoke(jwtAuthFilter, authHeader);
        assertNull(token);

        authHeader = "Not Bearer token";
        token = (String) extractTokenMethod.invoke(jwtAuthFilter, authHeader);
        assertNull(token);
    }

}