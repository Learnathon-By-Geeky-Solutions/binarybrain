package com.moinul.gateway.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moinul.gateway.security.JwtAuthFilter;
import com.moinul.gateway.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/user/**",
                                "/api/v1/private/course/**",
                                "/api/v1/private/classroom/**",
                                "/api/v1/private/task/**",
                                "/api/v1/public/submission/**")
                        .permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }

}
