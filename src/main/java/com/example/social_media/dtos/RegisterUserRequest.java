package com.example.social_media.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message="Name must be between 3 and 50 characters long.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Must be a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters long.")
    private String password;
}
