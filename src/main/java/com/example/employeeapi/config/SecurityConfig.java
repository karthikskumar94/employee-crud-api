package com.example.employeeapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@EnableWebSecurity
@EnableAspectJAutoProxy
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // Allow H2 console access
                        .anyRequest().permitAll() // Allow all requests, AOP will handle role-based auth
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                )
                .headers(headers -> headers.frameOptions().disable()); // Allow H2 console frames
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Uses the secret from application.yml via @Value injection
        return NimbusJwtDecoder.withSecretKey(
                new javax.crypto.spec.SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")
        ).build();
    }
}