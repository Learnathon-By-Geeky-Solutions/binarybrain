package com.onlineclassroom.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The {@code SecurityConfig} class Configures the security setting of the application to handle HTTP request security, session managment and password encoding.
 *
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 */
@Configuration
public class SecurityConfig{

    /**
     * Configures the {@link HttpSecurity} to define security settings for HTTP requests.
     * *
     * @param http the {@link HttpSecurity} object to configure web security.
     * @return a configured {@link SecurityFilterChain} that defines security rules.
     * @throws Exception if an error occurs while configuring security settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
    /**
     * Provides a {@link PasswordEncoder} bean for encoding passwords.
     * @return an instance of {@link PasswordEncoder} that encodes passwords using BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
