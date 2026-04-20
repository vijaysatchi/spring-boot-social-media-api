package com.example.social_media.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("currentUser", null);
        return "auth";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("currentUser", null);
        return "auth";
    }
}
