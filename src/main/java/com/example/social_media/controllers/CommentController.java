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
        var comments = commentService.getCommentsByPostId(id, page);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long id,
            @RequestBody @Valid CreateCommentRequest request,
            UriComponentsBuilder uriBuilder) {
        var commentDto = commentService.createComment(id, request.getUserId(), request);
        var uri =  uriBuilder.path("/api/comment/{id}").buildAndExpand(commentDto.getId()).toUri();
        return ResponseEntity.created(uri).body(commentDto);
    }

    @PatchMapping("/comment/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody @Valid EditCommentRequest request){
        var updatedComment = commentService.updateComment(id, request);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
