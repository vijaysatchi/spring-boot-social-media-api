package com.example.social_media.dtos.posts;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    @Size(max = 255, message="Name must be at most 255 characters long.")
    private String caption;
}
