package com.example.social_media.dtos.comments;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime dateCreated;
    private Long likeCount;
    private LocalDateTime updatedAt;
    private Boolean liked;

    private Long userId;
    private String userName;

    private Long postId;

    public CommentDto(Long id,
                      String content,
                      LocalDateTime dateCreated,
                      Long likeCount,
                      LocalDateTime updatedAt,
                      Boolean liked,
                      Long userId,
                      String userName,
                      Long postId) {
        this.id = id;
        this.content = content;
        this.dateCreated = dateCreated;
        this.likeCount = likeCount;
        this.updatedAt = updatedAt;
        this.liked = liked;
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "PostDto(" +
                "id=" + id +
                ", caption='" + content + '\'' +
                ", timeCreated=" + dateCreated +
                ", likeCount=" + likeCount +
                ", updatedAt=" + updatedAt +
                ", liked=" + liked +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ')';
    }
}
