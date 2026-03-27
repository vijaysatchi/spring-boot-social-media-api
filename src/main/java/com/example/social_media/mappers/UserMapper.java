package com.example.social_media.mappers;

import com.example.social_media.dtos.RegisterUserRequest;
import com.example.social_media.dtos.UpdateUserRequest;
import com.example.social_media.dtos.UserDto;
import com.example.social_media.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterUserRequest request);
    User updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
