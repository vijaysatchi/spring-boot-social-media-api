package com.example.social_media.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditPostRequest {
    @NotNull(message = "Caption cannot be blank.")
    @Size(min = 1, max = 255, message = "Caption length must be between 1 and 255 characters long.")
    private String caption;
}
