package com.example.social_media.repositories;

import com.example.social_media.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUserId(Long userId, Pageable pageable);
    @Query("""
        SELECT p 
        FROM POST p
        JOIN Follow f ON p.user = f.following
        WHERE f.follower.id = :followerId
    """)
    Page<Post> getFeed(@Param("followerId") Long followerId, Pageable pageable);
}
