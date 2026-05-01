package com.example.social_media.dtos.users;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserProfileDto {
    private Long id;
    private String name;
    private LocalDateTime dateCreated;
    private String profilePictureUrl;
    private String bannerColour;
    private String bio;
    private Long followersCount;
    private Long followingCount;
    private Long mutualFollowersCount;
    private Boolean currentlyFollowing = null;

    public UserProfileDto(Long id,
                          String name,
                          LocalDateTime dateCreated,
                          String profilePictureUrl,
                          String bannerColour,
                          String bio,
                          Long followersCount,
                          Long followingCount,
                          Long mutualFollowersCount) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.profilePictureUrl = profilePictureUrl;
        this.bannerColour = bannerColour;
        this.bio = bio;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.mutualFollowersCount = mutualFollowersCount;
    }
    public UserProfileDto(Long id,
                          String name,
                          LocalDateTime dateCreated,
                          String profilePictureUrl,
                          String bannerColour,
                          String bio,
                          Long followersCount,
                          Long followingCount,
                          Long mutualFollowersCount,
                          Boolean currentlyFollowing) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.profilePictureUrl = profilePictureUrl;
        this.bannerColour = bannerColour;
        this.bio = bio;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.mutualFollowersCount = mutualFollowersCount;
        this.currentlyFollowing = currentlyFollowing;
    }
}
