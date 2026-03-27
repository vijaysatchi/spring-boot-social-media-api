package com.example.social_media.services;

import com.example.social_media.entities.User;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User #" + id + " was not found."));
    }
}
