package com.example.social_media.services;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.dtos.CreateCommentRequest;
import com.example.social_media.dtos.EditCommentRequest;
import com.example.social_media.entities.Comment;
import com.example.social_media.entities.User;
import com.example.social_media.exceptions.BadRequestException;
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
        var comment = commentMapper.toEntity(request);
        var user = userService.findById(userId);

        post.addComment(comment);
        user.addComment(comment);

        var newComment = commentRepository.save(comment);
        return commentMapper.toDto(newComment);
    }

    public List<CommentDto> getCommentsByPostId(Long postId, Long viewerId, Integer page) {
        postService.findById(postId);
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dateCreated"));
        if(viewerId == null) return commentRepository.findAllByPostId(postId, pageRequest)
                .stream()
                .map(commentMapper::toDto)
                .toList();
        return commentRepository.findAllByPostIdWithIsLiked(postId, viewerId, pageRequest)
                .toList();
    }

    @Transactional
    public CommentDto updateComment(Long commentId, Long userId, EditCommentRequest request) {
        var comment = findById(commentId);
        if(!comment.getUser().getId().equals(userId))
            throw new BadRequestException("You cannot edit this comment!");
        commentMapper.update(request, comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public void deleteComment(Long id, Long userId) {
        var comment = findById(id);
        var user = comment.getUser();
        if(!user.getId().equals(userId))
            throw new BadRequestException("You cannot delete this comment!");
        user.removeComment(comment);
        var post = comment.getPost();
        post.removeComment(comment);
        commentRepository.deleteById(id);
    }

    public boolean isLikedByUser(Long userId, Long commentId) {
        return 1L == commentRepository.isLikedByUser(userId, commentId);
    }

    @Transactional
    public void toggleLike(Long user_id, Long comment_id) {
        var comment = commentRepository.findById(comment_id).orElseThrow(() ->
                new BadRequestException("Comment #" + comment_id + " not found."));
        if(isLikedByUser(user_id, comment_id)) {
            commentRepository.removeLike(user_id, comment_id);
            comment.setLikeCount(comment.getLikeCount() - 1);
        }else{
            commentRepository.addLike(user_id, comment_id);
            comment.setLikeCount(comment.getLikeCount() + 1);
        }
        commentRepository.save(comment);
    }
}
