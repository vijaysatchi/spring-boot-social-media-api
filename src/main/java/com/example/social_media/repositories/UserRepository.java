package com.example.social_media.repositories;

import com.example.social_media.dtos.users.UserFollowDto;
import com.example.social_media.dtos.users.UserProfileDto;
import com.example.social_media.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Query("Select u.id from User u where u.email = :email")
    Optional<String> findEmailById(long id);

    @Query("""
    select new com.example.social_media.dtos.users.UserProfileDto(
        u.id,
        u.name,
        u.dateCreated,
        u.profilePictureUrl,
        u.bannerColour,
        u.bio,
        u.followersCount,
        u.followingCount,
        (
            select count(f1)
            from Follow f1
            join Follow f2
                on f1.follower.id = f2.follower.id
            where f1.following.id = :userId
                and f2.following.id = :viewerId
        ),
        case when f.id is not null then true else false end
    )
    from User u
    left join Follow f
        on f.following.id = :userId
        and f.follower.id = :viewerId
    where u.id = :userId
    """)
    Optional<UserProfileDto> findUserDtoByIdFromViewer(@Param("userId") Long userId, @Param("viewerId") Long viewerId);

    @Query("""
    select new com.example.social_media.dtos.users.UserFollowDto(
        u.id,
        u.name,
        u.profilePictureUrl
    )
    from Follow f
        join f.following u
    where f.follower.id = :userId
    """)
    Page<UserFollowDto> findUserFollowings(@Param("userId") Long userId, Pageable pageable);

    @Query("""
    select new com.example.social_media.dtos.users.UserFollowDto(
        u.id,
        u.name,
        u.profilePictureUrl
    )
    from Follow f
        join f.follower u
    where f.following.id = :userId
    """)
    Page<UserFollowDto> findUserFollowers(@Param("userId") Long userId, Pageable pageable);

    @Query("""
    select new com.example.social_media.dtos.users.UserFollowDto(
        u.id,
        u.name,
        u.profilePictureUrl
    )
    from Follow f1
    join f1.follower u
    where exists(
        select 1 from Follow f2
            where f1.follower.id = f2.follower.id
                and f2.following.id = :viewerId
        )
        and f1.following.id = :userId
    """)
    Page<UserFollowDto> findUserMutuals(@Param("userId") Long userId, @Param("viewerId") Long viewerId, Pageable pageable);

    boolean existsByName(String name);
}
