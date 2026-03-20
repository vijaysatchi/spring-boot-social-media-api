package com.example.social_media.mappers;

import com.example.social_media.dtos.CreatePostRequest;
import com.example.social_media.dtos.PostDto;
import com.example.social_media.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    PostDto toDto(Post post);

    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "timeCreated", expression = "java(java.time.LocalDateTime.now())")
    Post toEntity(CreatePostRequest createPostRequest);
}
