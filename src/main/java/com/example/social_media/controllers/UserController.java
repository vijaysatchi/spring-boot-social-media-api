package com.example.social_media.controllers;

import com.example.social_media.dtos.RegisterUserRequest;
import com.example.social_media.dtos.UserDto;
import com.example.social_media.mappers.UserMapper;
import com.example.social_media.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable(name="id") Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody RegisterUserRequest request
            ){
        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().build();
        }
        var user = userMapper.toEntity(request);
        user.setDateCreated(java.time.LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validationHandler(MethodArgumentNotValidException ex){
        var errors = new HashMap<String, String>();

        ex.getBindingResult().getFieldErrors().forEach((error) -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }
}
