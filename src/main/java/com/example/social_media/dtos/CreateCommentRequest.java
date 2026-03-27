package com.example.social_media.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "Text is required.")
    @Size(min = 1, max = 255, message="Text must be between 1 and 255 characters long.")
    private String text;
    @NotBlank(message = "User id is required") // this is temporary until auth is completed
    private Long userId;
}
