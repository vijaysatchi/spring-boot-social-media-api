package com.example.social_media.services;

import com.example.social_media.dtos.RegisterUserRequest;
import com.example.social_media.dtos.UpdateUserRequest;
import com.example.social_media.dtos.UserDto;
import com.example.social_media.entities.Follow;
import com.example.social_media.entities.User;
import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.mappers.UserMapper;
import com.example.social_media.repositories.FollowRepository;
import com.example.social_media.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.nio.cs.ext.SimpleEUCEncoder;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User #" + id + " was not found."));
    }

    public UserDto getUserDtoById(Long id){
        return userMapper.toDto(findById(id));
    }

    @Transactional
    public void delete(Long id) {
        findById(id); // to return 404 if user doesn't exist
        userRepository.deleteById(id);
    }

    public UserDto registerUser(RegisterUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already exists.");
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var newUser = userRepository.save(user);
        return userMapper.toDto(newUser);
    }

    public void follow(long id, long targetId) {
        if(id == targetId)
            throw new BadRequestException("You cannot follow yourself.");

        var user = findById(id);
        var targetUser = findById(targetId);
        if(followRepository.existsByFollowerAndFollowing(user, targetUser))
            throw new BadRequestException("You are already following this user.");
        Follow follow = user.follow(targetUser);
        followRepository.save(follow);
    }

    public void unfollow(Long id, Long targetId) {
        var user = findById(id);
        var targetUser = findById(targetId);
        followRepository.findByFollowerAndFollowing(user, targetUser).orElseThrow(() ->
                new BadRequestException("You are not currently following this user.")
        );
        followRepository.deleteByFollowerAndFollowing(user, targetUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        var user = findById(id);
        userMapper.updateEntity(request, user);
        return userMapper.toDto(user);
    }
}
