package com.example.social_media.dtos.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountRequest {
    @NotBlank(message = "Your password is required to delete account")
    private String password;
}
