package com.example.social_media.services;

import com.example.social_media.dtos.LoginRequestDto;
import com.example.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private UserRepository userRepository;

    public long login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        return userRepository.findIdByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("Invalid email or password"));
    }

    public String getAccessToken(long id) {
        return jwtService.generateAccessToken(id, null).toString();
    }

    public String getRefreshToken(long id) {
        return jwtService.generateRefreshToken(id, null).toString();
    }

    public String refresh(String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || jwt.isExpired()){
            throw new BadCredentialsException("You must log in; invalid refresh token.");
        }
        return jwtService.generateAccessToken(jwt.getUserId(), null).toString();
    }

    public void validate(String authorization) {
        var token = authorization.substring(7);
        var jwt = jwtService.parseToken(token);
        if(jwt == null){
            throw new BadCredentialsException("Invalid token; invalid email or password.");
        }
    }
}
