package com.example.social_media.dtos;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String text;
    private Long userId;
}
