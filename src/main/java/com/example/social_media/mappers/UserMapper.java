package com.example.social_media.mappers;

import com.example.social_media.dtos.RegisterUserRequest;
import com.example.social_media.dtos.UserDto;
import com.example.social_media.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
}
