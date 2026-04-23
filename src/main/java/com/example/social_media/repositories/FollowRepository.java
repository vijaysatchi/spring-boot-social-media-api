package com.example.social_media.repositories;

import com.example.social_media.entities.Follow;
import com.example.social_media.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);

    @Modifying
    void deleteByFollowerAndFollowing(User follower, User following);

    // Subtract 1 from 'followingCount' for everyone who follows this user
    @Modifying
    @Query(value = """
    update users u
    set following_count = u.following_count - 1
    from follows f
    where f.follower_id = u.id
    and f.following_id = :followingId
    """, nativeQuery = true)
    void removeDeletedUsersFollowers(@Param("followingId") Long followingId);

    // Subtract 1 from 'followersCount' for everyone who this user follows
    @Modifying
    @Query(value = """
    update users u
    set followers_count = u.followers_count - 1
    from follows f
    where u.id = f.following_id
    and f.follower_id = :followersId
    """, nativeQuery = true)
    void removeDeletedUsersFollowings(@Param("followersId") Long followersId);
    Optional<Follow> findByFollowerAndFollowing(User user, User targetUser);
}
