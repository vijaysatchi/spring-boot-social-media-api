package com.example.social_media.dtos;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String profilePictureUrl;
    private String bio;
    private int followersCount;
    private int followingCount;
}
