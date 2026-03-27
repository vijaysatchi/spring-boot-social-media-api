package com.example.social_media.repositories;

import com.example.social_media.entities.Follow;
import com.example.social_media.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User user, User targetUser);
}
