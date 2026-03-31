package com.example.social_media.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String caption;
    private LocalDateTime timeCreated;
    private long likeCount;

    private Long userId;
    private String userName;
}
