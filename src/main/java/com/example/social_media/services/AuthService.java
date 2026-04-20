package com.example.social_media.services;

import com.example.social_media.dtos.users.LoginRequestDto;
import com.example.social_media.dtos.users.RegisterUserRequest;
import com.example.social_media.dtos.users.UserDto;
import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.mappers.UserMapper;
import com.example.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                new BadRequestException("Invalid email or password"));
        return userMapper.toDto(user);
    }

    public String getAccessToken(long id, String email) {
        return jwtService.generateAccessToken(id, Map.of("email", email)).toString();
    }

    public String getRefreshToken(long id) {
        return jwtService.generateRefreshToken(id, null).toString();
    }

    public UserDto registerUser(RegisterUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already exists.");
        if(userRepository.existsByName(request.getName()))
            throw new BadRequestException("Name already exists.");
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var newUser = userRepository.save(user);
        return userMapper.toDto(newUser);
    }

    public UserDto getAuthenticatedUser(CustomUserDetails user) {
        if(user == null) throw new BadRequestException("No authenticated user found.");
        return userMapper.toDto(userRepository.findById(user.getId()).orElseThrow(() ->
                new BadRequestException("No authenticated user found.")));
    }
}
