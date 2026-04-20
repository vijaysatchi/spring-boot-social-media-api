package com.example.social_media.dtos.users;

import lombok.Data;

@Data
public class UserFollowDto {
    private Long id;
    private String name;
    private String profilePictureUrl;

    public UserFollowDto(Long id,
                         String name,
                         String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }
}
