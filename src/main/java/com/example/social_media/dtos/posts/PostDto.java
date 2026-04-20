package com.example.social_media.dtos.posts;

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
    private LocalDateTime updatedAt;
    private Boolean liked;

    private Long userId;
    private String userName;
    private String profilePictureUrl;

    public PostDto(Long id,
                   String caption,
                   LocalDateTime timeCreated,
                   Long likeCount,
                   LocalDateTime updatedAt,
                   Boolean liked,
                   Long userId,
                   String userName,
                   String profilePictureUrl) {
        this.id = id;
        this.caption = caption;
        this.timeCreated = timeCreated;
        this.likeCount = likeCount;
        this.updatedAt = updatedAt;
        this.liked = liked;
        this.userId = userId;
        this.userName = userName;
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public String toString() {
        return "PostDto(" +
                "id=" + id +
                ", caption='" + caption + '\'' +
                ", timeCreated=" + timeCreated +
                ", likeCount=" + likeCount +
                ", updatedAt=" + updatedAt +
                ", liked=" + liked +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ')';
    }
}
