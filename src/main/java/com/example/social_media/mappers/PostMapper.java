package com.example.social_media.mappers;

import com.example.social_media.dtos.CreatePostRequest;
import com.example.social_media.dtos.EditPostRequest;
import com.example.social_media.dtos.PostDto;
import com.example.social_media.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    PostDto toDto(Post post);

    @Mapping(target = "user", ignore = true)
    Post toEntity(CreatePostRequest createPostRequest);

    void update(EditPostRequest request, @MappingTarget Post post);
}
