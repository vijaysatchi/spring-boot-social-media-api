package com.example.social_media.controllers;

import com.example.social_media.config.JwtConfig;
import com.example.social_media.dtos.users.LoginRequestDto;
import com.example.social_media.dtos.users.RegisterUserRequest;
import com.example.social_media.dtos.users.UserDto;
import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.services.AuthService;
import com.example.social_media.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private JwtConfig jwtConfig;
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        var userDto = authService.login(request);
        var accessToken = authService.getAccessToken(userDto.getId(), userDto.getEmail());
        var refreshToken = authService.getRefreshToken(userDto.getId());

        response.addHeader(
                "Set-Cookie",
                "accessToken=" + accessToken +
                        "; HttpOnly; Secure; Path=/; Max-Age=" + jwtConfig.getAccessTokenExpiration() +
                        "; SameSite=Strict"
        );

        response.addHeader(
                "Set-Cookie",
                "refreshToken=" + refreshToken +
                        "; HttpOnly; Secure; Path=/; Max-Age=" + jwtConfig.getRefreshTokenExpiration() +
                        "; SameSite=Strict"
        );

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        response.addHeader(
                "Set-Cookie",
                "accessToken=; HttpOnly; Secure; Path=/; Max-Age=0; SameSite=Strict"
        );

        response.addHeader(
                "Set-Cookie",
                "refreshToken=; HttpOnly; Secure; Path=/; Max-Age=0; SameSite=Strict"
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refreshToken") String refreshToken,
            HttpServletResponse response){

        var accessToken = jwtService.refresh(refreshToken);
        response.addHeader(
                "Set-Cookie",
                "accessToken=" + accessToken +
                        "; HttpOnly; Secure; Path=/; Max-Age=" + jwtConfig.getAccessTokenExpiration() +
                        "; SameSite=Strict"
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(
            @Valid @RequestBody LoginRequestDto request,
            @RequestHeader("Authorization") String authorization
    ){
        authService.validate(authorization);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal CustomUserDetails user){
        var userDto = authService.getAuthenticatedUser(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid RegisterUserRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ){
        var userDto = authService.registerUser(request);
        var uri = uriComponentsBuilder.path("/api/user/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }
}
