package com.example.social_media.mappers;

import com.example.social_media.dtos.posts.CreatePostRequest;
import com.example.social_media.dtos.posts.EditPostRequest;
import com.example.social_media.dtos.posts.PostDto;
import com.example.social_media.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.profilePictureUrl", target = "profilePictureUrl")
    PostDto toDto(Post post);

    @Mapping(target = "user", ignore = true)
    Post toEntity(CreatePostRequest createPostRequest);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void update(EditPostRequest request, @MappingTarget Post post);
}
