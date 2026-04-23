package com.example.social_media.repositories;

import com.example.social_media.entities.CommentLike;
import com.example.social_media.entities.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    @Modifying
    void deleteByUserId(Long userId);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    @Modifying
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}
