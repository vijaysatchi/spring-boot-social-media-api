package com.example.social_media.repositories;

import com.example.social_media.dtos.CommentDto;
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

    @Query("""
    select new com.example.social_media.dtos.CommentDto(
        c.id,
        c.content,
        c.dateCreated,
        c.likeCount,
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

    @Query(value = "select exists(select 1 from comment_likes where user_id = :userId and comment_id = :commentId)" , nativeQuery = true)
    Long isLikedByUser(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Modifying
    @Query(value = "insert ignore into comment_likes(user_id, comment_id) values(:userId, :commentId)", nativeQuery = true)
    void addLike(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Modifying
    @Query(value = "delete from comment_likes where user_id = :userId and comment_id = :commentId", nativeQuery = true)
    void removeLike(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
