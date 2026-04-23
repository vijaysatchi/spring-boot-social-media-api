package com.example.social_media.repositories;

import com.example.social_media.dtos.comments.CommentDto;
import com.example.social_media.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPostId(Long id, Pageable pageable);

    @Modifying
    void deleteByUserId(Long userId);

    @Modifying
    @Query(value = """
    update comments c
    set like_count = c.like_count - 1
    from comment_likes cl
    where cl.comment_id = c.id
        and cl.user_id = :userId
    """, nativeQuery = true)
    void removeDeletedUsersLikedCommentsFromCount(@Param("userId") Long userId);

    @Query("""
    select new com.example.social_media.dtos.comments.CommentDto(
        c.id,
        c.content,
        c.dateCreated,
        c.likeCount,
        c.updatedAt,
        case when cl.userId is not null then true else false end,
        u.id,
        u.name,
        c.post.id
    )
    from Comment c
        left join c.user u
        left join CommentLike cl
            on cl.commentId = c.id and cl.userId = :viewerId
        where c.post.id = :postId
    """)
    Page<CommentDto> findAllByPostIdWithIsLiked(@Param("postId") Long postId, @Param("viewerId") Long viewerId, Pageable pageable);
}
