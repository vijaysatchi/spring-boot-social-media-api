package com.example.social_media.controllers;

import com.example.social_media.dtos.RegisterUserRequest;
import com.example.social_media.dtos.UpdateUserRequest;
import com.example.social_media.dtos.UserDto;
import com.example.social_media.mappers.UserMapper;
import com.example.social_media.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDto(@PathVariable(name="id") Long id){
        var userDto = userService.getUserDtoById(id);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid UpdateUserRequest request
    ){
        var userDto = userService.updateUser(id, request);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name="id") Long id){
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/follow/{targetId}")
    public ResponseEntity<Void> followUser(@PathVariable(name="id") Long id, @PathVariable(name="targetId") Long targetId){
        userService.follow(id, targetId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/unfollow/{targetId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable(name="id") Long id, @PathVariable(name="targetId") Long targetId){
        userService.unfollow(id, targetId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid RegisterUserRequest request,
            UriComponentsBuilder uriComponentsBuilder
            ){
        var userDto = userService.registerUser(request);
        var uri = uriComponentsBuilder.path("/api/user/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }
}
