package com.example.social_media.services;

import com.example.social_media.dtos.CreatePostRequest;
import com.example.social_media.dtos.EditPostRequest;
import com.example.social_media.dtos.PostDto;
import com.example.social_media.entities.Post;
import com.example.social_media.entities.User;
import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.mappers.PostMapper;
import com.example.social_media.repositories.PostRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public Post findById(Long id){
        return postRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Post #" + id + " was not found.")
        );
    }

    public PostDto getPostDtoById(Long postId, Long viewerId){
        if(viewerId != null) return postRepository.findByIdWithIsLiked(postId, viewerId);
        return postMapper.toDto(findById(postId));
    }

    public List<PostDto> getGlobalFeedByPage(Long viewerId, int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum, 7, Sort.by(Sort.Direction.DESC, "timeCreated"));
        if(viewerId == null) return postRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(postMapper::toDto)
                .toList();

        return postRepository.findAllWithIsLiked(viewerId, pageRequest)
                .toList();
    }

    public List<PostDto> getFollowingFeedByPage(long userId, int pages) {
        PageRequest pageRequest = PageRequest.of(pages, 7, Sort.by(Sort.Direction.DESC, "timeCreated"));
        return postRepository.findAllByFollowerIdWithIsLiked(userId, pageRequest)
                .toList();
    }

    public List<PostDto> getUsersPosts(long userId, int pages, Long viewerId) {
        PageRequest pageRequest = PageRequest.of(pages, 7, Sort.by(Sort.Direction.DESC, "timeCreated"));
        if(viewerId == null) return postRepository.findAllByUserId(userId, pageRequest)
                .getContent()
                .stream()
                .map(postMapper::toDto)
                .toList();
        return postRepository.findAllByUserIdWithIsLiked(userId, viewerId, pageRequest)
                .toList();
    }

    public PostDto createPost(CreatePostRequest request, User user) {
        var post = postMapper.toEntity(request);
        user.addPost(post);
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto update(Long id, EditPostRequest request) {
        var post = findById(id);
        postMapper.update(request, post);
        return postMapper.toDto(post);
    }

    @Transactional
    public void deleteById(Long id) {
        var post = findById(id);
        var user = post.getUser();
        user.removePost(post);
        postRepository.deleteById(id);
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        return 1L == postRepository.isLikedByUser(postId, userId);
    }

    @Transactional
    public void togglePostLike(Long postId, Long userId) {
        var post = postRepository.findById(postId).orElseThrow(() ->
                new BadRequestException("Post #" + postId + " not found."));
        if(isLikedByUser(postId, userId)){
            postRepository.removeLike(userId, postId);
            post.setLikeCount(post.getLikeCount() - 1);
        }else{
            postRepository.addLike(userId, postId);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        postRepository.save(post);
    }
}
