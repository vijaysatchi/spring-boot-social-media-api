package com.example.social_media.services;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.EditCommentRequest;
import com.example.social_media.entities.Comment;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.mappers.CommentMapper;
import com.example.social_media.repositories.CommentRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final UserService userService;

    public CommentDto getCommentById(Long id){
        var comment = commentRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Comment #" + id + " not found."));
        return commentMapper.toDto(comment);
    }

    public Comment createComment(Long postId, Long userId, Comment comment){
        var post = postService.findById(postId);
        var user = userService.findById(userId);
        post.addComment(comment);
        user.addComment(comment);

        return commentRepository.save(comment);
    }

    public Page<Comment> getCommentsByPostId(Long id, Integer page) {
        postService.findById(id);
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "timeCreated"));
        return commentRepository.findByPostId(id, pageRequest);
    }

    @Transactional
    public Comment updateComment(Long id, EditCommentRequest request) {
        var comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment #" + id + " not found."));
        commentMapper.update(request, comment);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        var comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment #" + id + " not found."));
        var user = comment.getUser();
        user.removeComment(comment);
        var post = comment.getPost();
        post.removeComment(comment);
        commentRepository.deleteById(id);
    }
}
