package com.example.social_media.services;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.CreateCommentRequest;
import com.example.social_media.dtos.EditCommentRequest;
import com.example.social_media.entities.Comment;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.mappers.CommentMapper;
import com.example.social_media.repositories.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final UserService userService;

    public Comment findById(Long id){
        return commentRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Comment #" + id + " not found."));
    }

    public CommentDto getCommentById(Long id){
        var comment = findById(id);
        return commentMapper.toDto(comment);
    }

    public CommentDto createComment(Long postId, Long userId, CreateCommentRequest request){
        var post = postService.findById(postId);
        var user = userService.findById(userId);
        var comment = commentMapper.toEntity(request);

        post.addComment(comment);
        user.addComment(comment);

        var newComment = commentRepository.save(comment);
        return commentMapper.toDto(newComment);
    }

    public List<CommentDto> getCommentsByPostId(Long id, Integer page) {
        postService.findById(id);
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "timeCreated"));

        return commentRepository.findByPostId(id, pageRequest)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto updateComment(Long id, EditCommentRequest request) {
        var comment = findById(id);
        commentMapper.update(request, comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        var comment = findById(id);
        var user = comment.getUser();
        user.removeComment(comment);
        var post = comment.getPost();
        post.removeComment(comment);
        commentRepository.deleteById(id);
    }
}
