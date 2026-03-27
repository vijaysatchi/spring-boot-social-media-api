package com.example.social_media.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    @NotBlank(message = "User id is required")
    private Long userId; //temporary

    @Size(max = 255, message="Name must be at most 255 characters long.")
    private String caption; //kind of also a temporary requirement
}
