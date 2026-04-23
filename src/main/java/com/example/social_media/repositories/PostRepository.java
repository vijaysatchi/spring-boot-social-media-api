package com.example.social_media.repositories;

import com.example.social_media.dtos.posts.PostDto;
import com.example.social_media.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    @Modifying
    void deleteByUserId(Long userId);

    /*update posts p
    join post_likes pl on p.id = pl.post_id
        set p.like_count = p.like_count - 1
    where pl.user_id = :userId*/
    @Modifying
    @Query(value = """
    update posts p
    set like_count = p.like_count - 1
    from post_likes pl
    where pl.user_id = :userId
        and pl.post_id = p.id
    """, nativeQuery = true)
    void removeDeletedUsersLikedPostsFromCount(@Param("userId") Long userId);

    @Query("""
    select new com.example.social_media.dtos.posts.PostDto(
        p.id,
        p.caption,
        p.dateCreated,
        p.likeCount,
        p.updatedAt,
        case when pl.userId is not null then true else false end,
        u.id,
        u.name,
        u.profilePictureUrl
    )
    from Post p
    left join p.user u
    left join PostLike pl
        on pl.postId = p.id and pl.userId = :followerId
    join Follow f on p.user = f.following
    where f.follower.id = :followerId
    """)
    Page<PostDto> findAllByFollowerIdWithIsLiked(@Param("followerId") Long followerId, Pageable pageable);

    @Query(value = """
    select new com.example.social_media.dtos.posts.PostDto(
        p.id,
        p.caption,
        p.dateCreated,
        p.likeCount,
        p.updatedAt,
        case when pl.userId is not null then true else false end,
        u.id,
        u.name,
        u.profilePictureUrl
    )
    from Post p
    left join p.user u
    left join PostLike pl
        on pl.postId = p.id and pl.userId = :viewerId
    where u.id = :userId
    order by p.dateCreated desc
    """)
    Page<PostDto> findAllByUserIdWithIsLiked(@Param("userId") long userId, @Param("viewerId") long viewerId, Pageable pageRequest);

    @Query("""
    select new com.example.social_media.dtos.posts.PostDto(
        p.id,
        p.caption,
        p.dateCreated,
        p.likeCount,
        p.updatedAt,
        case when pl.userId is not null then true else false end,
        u.id,
        u.name,
        u.profilePictureUrl
    )
    from Post p
    left join p.user u
    left join PostLike pl
        on pl.postId = p.id and pl.userId = :viewerId
    order by p.dateCreated desc
    """)
    Page<PostDto> findAllWithIsLiked(@Param("viewerId") Long viewerId, Pageable pageRequest);

    @Query("""
    select new com.example.social_media.dtos.posts.PostDto(
        p.id,
        p.caption,
        p.dateCreated,
        p.likeCount,
        p.updatedAt,
        case when pl.userId is not null then true else false end,
        u.id,
        u.name,
        u.profilePictureUrl
    )
    from Post p
        left join p.user u
        left join PostLike pl
            on pl.postId = p.id and pl.userId = :viewerId
        where p.id = :postId
    """)
    PostDto findByIdWithIsLiked(@Param("postId") Long postId, @Param("viewerId") Long viewerId);

//    @Query(value = "select exists(select 1 from post_likes where user_id = :user_id and post_id = :post_id)", nativeQuery = true)
//    Long isLikedByUser(@Param("post_id") Long post_id, @Param("user_id") Long user_id);

//    @Modifying
//    @Query(value = "insert ignore into post_likes(user_id, post_id) values(:user_id, :post_id)", nativeQuery = true)
//    void addLike(@Param("user_id") Long user_id, @Param("post_id") Long post_id);

//    @Modifying
//    @Query(value = "delete from post_likes where user_id = :user_id and post_id = :post_id", nativeQuery = true)
//    void removeLike(@Param("user_id") Long user_id, @Param("post_id") Long post_id);
}
