package com.example.social_media.services;

import com.example.social_media.dtos.posts.CreatePostRequest;
import com.example.social_media.dtos.posts.EditPostRequest;
import com.example.social_media.dtos.posts.PostDto;
import com.example.social_media.entities.Post;
import com.example.social_media.entities.PostLike;
import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.mappers.PostMapper;
import com.example.social_media.repositories.PostLikeRepository;
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
    private UserService userService;
    private final PostLikeRepository postLikeRepository;

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
        PageRequest pageRequest = PageRequest.of(pageNum, 7, Sort.by(Sort.Direction.DESC, "dateCreated"));
        if(viewerId == null) return postRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(postMapper::toDto)
                .toList();

        return postRepository.findAllWithIsLiked(viewerId, pageRequest)
                .toList();
    }

    public List<PostDto> getFollowingFeedByPage(long userId, int pages) {
        PageRequest pageRequest = PageRequest.of(pages, 7, Sort.by(Sort.Direction.DESC, "dateCreated"));
        return postRepository.findAllByFollowerIdWithIsLiked(userId, pageRequest)
                .toList();
    }

    public List<PostDto> getUsersPosts(long userId, int pages, Long viewerId) {
        PageRequest pageRequest = PageRequest.of(pages, 7, Sort.by(Sort.Direction.DESC, "dateCreated"));
        if(viewerId == null) return postRepository.findAllByUserId(userId, pageRequest)
                .getContent()
                .stream()
                .map(postMapper::toDto)
                .toList();
        return postRepository.findAllByUserIdWithIsLiked(userId, viewerId, pageRequest)
                .toList();
    }

    public PostDto createPost(CreatePostRequest request, Long userId) {
        var post = postMapper.toEntity(request);
        var user = userService.findById(userId);
        user.addPost(post);
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto update(Long postId, Long userId, EditPostRequest request) {
        var post = findById(postId);
        if(!post.getUser().getId().equals(userId)){
            throw new BadRequestException("You cannot edit this post!");
        }
        postMapper.update(request, post);
        return postMapper.toDto(post);
    }

    @Transactional
    public void deleteById(Long id, Long userId) {
        var post = findById(id);
        var user = post.getUser();
        if(!user.getId().equals(userId)){
            throw new BadRequestException("You cannot delete this post!");
        }
        user.removePost(post);
        postRepository.deleteById(id);
    }

    @Transactional
    public void toggleLike(Long postId, Long userId) {
        var post = findById(postId);
        if(postLikeRepository.existsByUserIdAndPostId(userId, postId)){
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            post.setLikeCount(post.getLikeCount() - 1);
        }else{
            postLikeRepository.save(new PostLike(postId, userId));
            post.setLikeCount(post.getLikeCount() + 1);
        }
        postRepository.save(post);
    }
}
