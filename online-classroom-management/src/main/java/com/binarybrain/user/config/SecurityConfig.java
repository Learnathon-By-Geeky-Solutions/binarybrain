package com.binarybrain.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * The {@code SecurityConfig} class Configures the security setting of the application to handle HTTP request security, session managment and password encoding.
 *
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 * lastModified 2025-08-02
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
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs/swagger-config")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .httpBasic(Customizer.withDefaults());
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:5000"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
            config.setAllowedHeaders(List.of("*"));
            return config;
        };
    }
}