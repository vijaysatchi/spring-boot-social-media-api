package com.example.social_media.controllers;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.CreateCommentRequest;
import com.example.social_media.dtos.EditCommentRequest;
import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.entities.User;
import com.example.social_media.services.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page
    ) {
        Long viewer = user == null ? null : user.getId();
        var comments = commentService.getCommentsByPostId(id, viewer, page);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/post/{id}/comment")
    public ResponseEntity<CommentDto> createComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("id") Long postId,
            @RequestBody @Valid CreateCommentRequest request,
            UriComponentsBuilder uriBuilder) {
        var commentDto = commentService.createComment(postId, user.getId(), request);
        var uri =  uriBuilder.path("/api/comment/{id}").buildAndExpand(commentDto.getId()).toUri();
        return ResponseEntity.created(uri).body(commentDto);
    }

    @PostMapping("/comment/{id}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable("id") Long comment_id,
            @AuthenticationPrincipal CustomUserDetails user){
        commentService.toggleLike(user.getId(), comment_id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/comment/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable("id") Long commentId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid EditCommentRequest request){
        var updatedComment = commentService.updateComment(commentId, user.getId(), request);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("id") Long commentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        commentService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }
}
