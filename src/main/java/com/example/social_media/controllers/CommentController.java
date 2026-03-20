package com.example.social_media.controllers;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.CreateCommentRequest;
import com.example.social_media.mappers.CommentMapper;
import com.example.social_media.repositories.CommentRepository;
import com.example.social_media.repositories.PostRepository;
import com.example.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

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
}
