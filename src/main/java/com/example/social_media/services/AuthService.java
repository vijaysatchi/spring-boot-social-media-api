package com.example.social_media.services;

import com.example.social_media.dtos.LoginRequestDto;
import com.example.social_media.dtos.RegisterUserRequest;
import com.example.social_media.dtos.UserDto;
import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.entities.User;
import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.mappers.UserMapper;
import com.example.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    public UserDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("Invalid email or password"));
        return userMapper.toDto(user);
    }

    public String getAccessToken(long id, String email) {
        return jwtService.generateAccessToken(id, Map.of("email", email)).toString();
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

    public UserDto registerUser(RegisterUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new BadCredentialsException("Email already exists.");
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var newUser = userRepository.save(user);
        return userMapper.toDto(newUser);
    }

    public UserDto getAuthenticatedUser(CustomUserDetails user) {
        if(user == null) throw new BadRequestException("No authenticated user found.");
        return userMapper.toDto(userRepository.findById(user.getId()).orElseThrow(() ->
                new BadRequestException("No authenticated user found.")));
    }
}
