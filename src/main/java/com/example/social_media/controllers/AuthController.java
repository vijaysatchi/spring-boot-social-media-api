package com.example.social_media.controllers;

import com.example.social_media.config.JwtConfig;
import com.example.social_media.dtos.LoginRequestDto;
import com.example.social_media.entities.User;
import com.example.social_media.repositories.UserRepository;
import com.example.social_media.services.AuthService;
import com.example.social_media.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response){
        var userId = authService.login(request);
        var accessToken = authService.getAccessToken(userId);
        var refreshToken = authService.getRefreshToken(userId);

        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken") String refreshToken){
        var jwt = authService.refresh(refreshToken);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(
            @Valid @RequestBody LoginRequestDto request,
            @RequestHeader("Authorization") String authorization
    ){
        authService.validate(authorization);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal User user){
        return ResponseEntity.ok("Hello " + user.getUsername() + "! :D");
    }
}
