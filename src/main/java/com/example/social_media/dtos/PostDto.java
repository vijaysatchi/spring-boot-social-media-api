package com.example.social_media.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String caption;
    private LocalDateTime timeCreated;
    private Long likeCount;
    private Boolean liked;

    private Long userId;
    private String userName;

    public PostDto(Long id,
                   String caption,
                   LocalDateTime timeCreated,
                   Long likeCount,
                   Boolean liked,
                   Long userId,
                   String userName) {
        this.id = id;
        this.caption = caption;
        this.timeCreated = timeCreated;
        this.likeCount = likeCount;
        this.liked = liked;
        this.userId = userId;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "PostDto(" +
                "id=" + id +
                ", caption='" + caption + '\'' +
                ", timeCreated=" + timeCreated +
                ", likeCount=" + likeCount +
                ", liked=" + liked +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ')';
    }
}
