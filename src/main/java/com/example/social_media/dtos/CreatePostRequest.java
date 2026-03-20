package com.example.social_media.dtos;

import lombok.Data;

@Data
public class CreatePostRequest {
    private Long userId;
    private String caption;
}
