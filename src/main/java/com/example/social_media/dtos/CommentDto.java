package com.example.social_media.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime dateCreated;
    private long likeCount;

    private Long userId;
    private String userName;
}
