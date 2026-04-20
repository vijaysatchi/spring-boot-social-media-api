package com.example.social_media.mappers;

import com.example.social_media.dtos.users.*;
import com.example.social_media.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    UserProfileDto toProfileDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "followersCount", constant = "0L")
    @Mapping(target = "followingCount", constant = "0L")
    User toEntity(RegisterUserRequest request);
    User updateEntity(UpdateUserProfileRequest request, @MappingTarget User user);
}
