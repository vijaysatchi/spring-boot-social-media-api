package com.example.social_media.mappers;

import com.example.social_media.dtos.comments.CommentDto;
import com.example.social_media.dtos.comments.CreateCommentRequest;
import com.example.social_media.dtos.comments.EditCommentRequest;
import com.example.social_media.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    Comment toEntity(CreateCommentRequest request);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "content", source = "content")
    void update(EditCommentRequest request, @MappingTarget Comment comment);
}
