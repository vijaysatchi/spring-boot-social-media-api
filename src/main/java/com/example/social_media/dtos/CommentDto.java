package com.example.social_media.dtos;

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
    private Boolean liked;

    private Long userId;
    private String userName;

    private Long postId;

    public CommentDto(Long id,
                      String content,
                      LocalDateTime dateCreated,
                      Long likeCount,
                      Boolean liked,
                      Long userId,
                      String userName,
                      Long postId) {
        this.id = id;
        this.content = content;
        this.dateCreated = dateCreated;
        this.likeCount = likeCount;
        this.liked = liked;
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
    }
}
