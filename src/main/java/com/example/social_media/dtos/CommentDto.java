package com.example.social_media.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private LocalDateTime dateCreated;

    private Long userId;
    private String userName;
}
