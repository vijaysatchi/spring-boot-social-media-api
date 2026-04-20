package com.example.social_media.dtos.users;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdateUserProfileRequest {
    @Size(min = 3, max = 50, message="Name must be between 3 and 50 characters long.")
    private String name;

    @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}){1,2}$", message = "Invalid hex color format")
    private String bannerColour;

    @Pattern(regexp = "^(null|avatar1|avatar2|avatar3|avatar4|avatar5)?$", message = "Invalid avatar selection")
    private String profilePictureUrl;

    @Size(max = 255, message = "Bio must be less than 256 characters long.")
    private String bio;
}
