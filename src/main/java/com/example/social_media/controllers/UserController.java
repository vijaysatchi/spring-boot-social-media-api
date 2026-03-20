package com.example.social_media.controllers;

import com.example.social_media.dtos.PostDto;
import com.example.social_media.dtos.RegisterUserRequest;
import com.example.social_media.dtos.UserDto;
import com.example.social_media.mappers.PostMapper;
import com.example.social_media.mappers.UserMapper;
import com.example.social_media.repositories.PostRepository;
import com.example.social_media.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable(name="id") Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name="id") Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable(name="id") Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        var postsList = user.getPosts()
                .stream()
                .map(postMapper::toDto)
                .toList();
        return ResponseEntity.ok(postsList);
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriComponentsBuilder
            ){
        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().build();
        }
        var user = userMapper.toEntity(request);
        userRepository.save(user);
        var uri = uriComponentsBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toDto(user));
    }
}
