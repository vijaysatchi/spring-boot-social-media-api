package com.example.social_media.controllers;

import com.example.social_media.dtos.users.*;
import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserDto(
            @PathVariable(name="id") Long userId,
            @AuthenticationPrincipal CustomUserDetails user) {
        var userDto = userService.getUserDtoById(userId, user);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{id}/following/{page}")
    public ResponseEntity<List<UserFollowDto>> getUsersFollowings(
            @PathVariable("id") Long userId,
            @PathVariable("page") Integer page
    ){
        var users = userService.getUsersFollowings(userId, page);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/followers/{page}")
    public ResponseEntity<List<UserFollowDto>> getUsersFollowers(
            @PathVariable("id") Long userId,
            @PathVariable("page") Integer page
    ){
        var users = userService.getUsersFollowers(userId, page);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/mutuals/{page}")
    public ResponseEntity<List<UserFollowDto>> getUsersMutuals(
            @PathVariable("id") Long userId,
            @PathVariable("page") Integer page,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        if(user == null)
            throw new BadRequestException("You must be signed in to perform this action.");
        var users = userService.getUsersMutuals(userId, user.getId(), page);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable(name="id") Long targetId){
        userService.follow(user.getId(), targetId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid UpdateUserProfileRequest request
    ){
        var userDto = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid UpdatePasswordRequest request
    ){
        userService.updatePassword(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollowUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable(name="id") Long targetId){
        userService.unfollow(user.getId(), targetId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid DeleteAccountRequest request,
            HttpServletResponse response){
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setPath("/api/auth/refresh");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);
        userService.delete(user.getId(), request);
        return ResponseEntity.ok().build();
    }
}
