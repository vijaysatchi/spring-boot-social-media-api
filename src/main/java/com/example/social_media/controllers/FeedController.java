package com.example.social_media.controllers;

import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class FeedController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/feed/global";
    }

    @GetMapping("/feed/global")
    public String globalFeed(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("feedType", "global");
        var userDto = user == null ? null : userService.getUserDtoById(user.getId(), null);
        model.addAttribute("currentUser", userDto);
        return "feed";
    }

    @GetMapping("/feed/following")
    public String followingFeed(Model model,
                                @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("feedType", "following");
        var userDto = user == null ? null : userService.getUserDtoById(user.getId(), null);
        model.addAttribute("currentUser", userDto);
        if(userDto == null) return "redirect:/feed/global";
        return "feed";
    }

    @GetMapping("/post/{postId}")
    public String viewPost(@PathVariable Long postId,
                           @AuthenticationPrincipal CustomUserDetails user,
                           Model model) {
        model.addAttribute("postId", postId);
        var userDto = user == null ? null : userService.getUserDtoById(user.getId(), null);
        model.addAttribute("currentUser", userDto);
        return "post";
    }

    @GetMapping("/profile/{userId}")
    public String viewProfile(@PathVariable Long userId,
                              @AuthenticationPrincipal CustomUserDetails user,
                              Model model) {
        model.addAttribute("userId", userId);
        var userDto = user == null ? null : userService.getUserDtoById(user.getId(), null);
        model.addAttribute("currentUser", userDto);
        return "profile";
    }
}