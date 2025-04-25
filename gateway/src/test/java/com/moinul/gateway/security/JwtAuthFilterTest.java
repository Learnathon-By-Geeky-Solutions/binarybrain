package com.moinul.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ServerWebExchange;
import static org.mockito.Mockito.when;



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

}