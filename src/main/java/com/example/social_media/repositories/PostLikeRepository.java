package com.example.social_media.repositories;

import com.example.social_media.entities.PostLike;
import com.example.social_media.entities.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    @Modifying
    void deleteByUserId(Long userId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Modifying
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
