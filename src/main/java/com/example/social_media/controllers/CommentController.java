package com.example.social_media.controllers;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.CreateCommentRequest;
import com.example.social_media.dtos.EditCommentRequest;
import com.example.social_media.entities.Comment;
import com.example.social_media.mappers.CommentMapper;
import com.example.social_media.repositories.CommentRepository;
import com.example.social_media.repositories.PostRepository;
import com.example.social_media.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentMapper.toDto(comment));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsOfPost(@PathVariable Long id) {
        var post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        var comments = commentRepository.findByPostId(id)
                .stream()
                .map(commentMapper::toDto)
                .toList();
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long id,
            @RequestBody CreateCommentRequest request,
            UriComponentsBuilder uriBuilder) {
        var user = userRepository.findById(request.getUserId()).orElse(null);
        if(user == null){
            return ResponseEntity.badRequest().build();
        }
        var post = postRepository.findById(id).orElse(null);
        if(post == null){
            return ResponseEntity.notFound().build();
        }
        var comment = commentMapper.toEntity(request);
        user.addComment(comment);
        post.addComment(comment);
        commentRepository.save(comment);

        var commentDto = commentMapper.toDto(comment);
        var uri =  uriBuilder.path("/api/comment/{id}").buildAndExpand(comment.getId()).toUri();
        return ResponseEntity.created(uri).body(commentDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody @Valid EditCommentRequest request){
        var comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        commentMapper.update(request, comment);
        var commentDto = commentMapper.toDto(commentRepository.save(comment));
        return ResponseEntity.ok(commentDto);
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        var comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        comment.getPost().removeComment(comment);
        return ResponseEntity.ok().build();
    }
}
