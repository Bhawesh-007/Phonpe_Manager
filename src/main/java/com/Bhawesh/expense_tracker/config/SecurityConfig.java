package com.Bhawesh.expense_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF so Postman can send POST requests without tokens
                .csrf(csrf -> csrf.disable())

                // 2. Tell the authorization manager to permit EVERY request
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}