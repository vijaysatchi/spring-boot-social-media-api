package com.example.social_media.mappers;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.CreateCommentRequest;
import com.example.social_media.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    CommentDto toDto(Comment comment);

    Comment toEntity(CreateCommentRequest request);
}
