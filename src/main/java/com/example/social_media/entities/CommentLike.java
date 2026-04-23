package com.example.social_media.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
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
