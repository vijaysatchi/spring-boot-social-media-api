package com.example.social_media.controllers;

import com.example.social_media.dtos.LoginRequestDto;
import com.example.social_media.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var accessToken = jwtService.generateToken(request.getEmail(), null);

        return ResponseEntity.ok(accessToken.toString());
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
        return ResponseEntity.ok("Hello " + auth.getPrincipal());
    }
}
