package com.example.social_media.entities;

import jakarta.persistence.*;

@Entity
@IdClass(PostLikeId.class)
@Table(name = "post_likes")
public class PostLike {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "post_id")
    private Long postId;
}
