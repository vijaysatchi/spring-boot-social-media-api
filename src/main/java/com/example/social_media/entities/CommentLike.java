package com.example.social_media.entities;

import jakarta.persistence.*;

@Entity
@IdClass(CommentLikeId.class)
@Table(name = "comment_likes")
public class CommentLike {
    @Id
    @Column(name = "comment_id")
    private Long commentId;

    @Id
    @Column(name = "user_id")
    private Long userId;
}
