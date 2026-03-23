package com.example.social_media.controllers;

import com.example.social_media.config.JwtConfig;
import com.example.social_media.dtos.LoginRequestDto;
import com.example.social_media.repositories.UserRepository;
import com.example.social_media.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response){
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var email =  auth.getName();
        var accessToken = jwtService.generateAccessToken(email, null).toString();

        var refreshToken = jwtService.generateRefreshToken(email, null).toString();
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
        System.out.println(refreshToken);
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = jwt.getSubject();
        return ResponseEntity.ok(jwtService.generateAccessToken(email, null).toString());
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(
            @Valid @RequestBody LoginRequestDto request,
            @RequestHeader("Authorization") String authorization
    ){
        var token = authorization.substring(7);
        var jwt = jwtService.parseToken(token);
        if(jwt == null || !jwt.getSubject().equals(request.getEmail())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me")
    public ResponseEntity<?> me(){
        SecurityContext context = SecurityContextHolder.getContext();
        var auth = context.getAuthentication();
        var user = userRepository.findByEmail(auth.getPrincipal().toString()).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok("Hello " + auth.getPrincipal());
    }
}
