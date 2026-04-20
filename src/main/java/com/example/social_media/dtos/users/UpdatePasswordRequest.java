package com.example.social_media.dtos.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters long.")
    private String newPassword;

    @NotBlank(message = "Confirming password is required")
    private String confirmPassword;
}
