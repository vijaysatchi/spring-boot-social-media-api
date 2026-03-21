package com.example.social_media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(c -> c
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(c -> c
                .requestMatchers(HttpMethod.GET,"/api/post/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/comment/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/user").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/auth/login/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

}
