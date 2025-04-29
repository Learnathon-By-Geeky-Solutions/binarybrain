package com.moinul.gateway.security;
import com.binarybrain.exception.global.GlobalExceptionHandler;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            if(shouldAllowPath(path)){
                return chain.filter(exchange);
            }

            String token = extractToken(exchange.getRequest());

            try {
                Optional.ofNullable(token).orElseThrow(
                        () -> new JwtException("Missing token!")
                );

                GlobalExceptionHandler.Thrower.throwIf(!jwtUtil.validateToken(token), new JwtException("Invalid token!"));

                String username = jwtUtil.extractUsername(token);
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Username", username)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (JwtException | IllegalArgumentException e){
                return handleUnauthorizedResponse(exchange);
            }
        };
    }

    private boolean shouldAllowPath(String path) {
        return path.equals("/api/user/login") ||
                path.equals("/api/user/register") ||
                path.equals("/api/user/refresh") ||
                path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui");
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        return extractToken(authHeader);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> handleUnauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorMessage = "{\"error\": \"" + "Token expired/ invalid!" + "\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * This class is required by AbstractGatewayFilterFactory,
     * even if it does not contain any configuration fields.
     */
    public static class Config {
        public Config() {
            /*
             * This file is required for JWT  Auth filter to work
             */
        }
    }
}