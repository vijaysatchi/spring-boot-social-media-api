package com.example.social_media.controllers;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.CreateCommentRequest;
import com.example.social_media.dtos.EditCommentRequest;
import com.example.social_media.entities.Comment;
import com.example.social_media.mappers.CommentMapper;
import com.example.social_media.repositories.CommentRepository;
import com.example.social_media.repositories.PostRepository;
import com.example.social_media.repositories.UserRepository;
import com.example.social_media.services.CommentService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentService commentService;


    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long id) {
        var commentDto = commentService.getCommentById(id);
        return ResponseEntity.ok(commentDto);
    }

    @GetMapping("/post/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsOfPost(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page
    ) {
        var commentsPage = commentService.getCommentsByPostId(id, page);
        var comments = commentsPage
                .stream()
                .map(commentMapper::toDto)
                .toList();
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long id,
            @RequestBody @Valid CreateCommentRequest request,
            UriComponentsBuilder uriBuilder) {
        var comment = commentMapper.toEntity(request);
        var newComment = commentService.createComment(id, request.getUserId(), comment);
        var uri =  uriBuilder.path("/api/comment/{id}").buildAndExpand(newComment.getId()).toUri();
        var commentDto = commentMapper.toDto(newComment);
        return ResponseEntity.created(uri).body(commentDto);
    }

    @PatchMapping("/comment/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody @Valid EditCommentRequest request){
        var updatedComment = commentService.updateComment(id, request);
        var commentDto = commentMapper.toDto(updatedComment);
        return ResponseEntity.ok(commentDto);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
