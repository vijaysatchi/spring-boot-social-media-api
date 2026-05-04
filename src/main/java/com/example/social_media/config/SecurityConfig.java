package com.example.social_media.config;

import com.example.social_media.filters.JwtAuthFilter;
import com.example.social_media.services.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(c -> c
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(c -> c
                                                        // basic visitor privileges
                .requestMatchers(HttpMethod.GET,"/api/post/{id}").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/post/{id}/comments/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/post/feed/global").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/user/{id}/post/{page}").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/comment/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/user/{id}").permitAll()
                                                        // auth
                .requestMatchers(HttpMethod.POST,"/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/auth/logout").permitAll()
                                                        // pages
                .requestMatchers(HttpMethod.GET,"/").permitAll()
                .requestMatchers(HttpMethod.GET,"/feed/global").permitAll()
                .requestMatchers(HttpMethod.GET,"/feed/following").permitAll()
                .requestMatchers(HttpMethod.GET,"/login").permitAll()
                .requestMatchers(HttpMethod.GET,"/register").permitAll()
                .requestMatchers(HttpMethod.GET,"/post/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/profile/**").permitAll()
                                                        // static assets
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                                                        // api docs
                .requestMatchers(HttpMethod.GET,"/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c ->
                        c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
