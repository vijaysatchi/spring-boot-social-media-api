package com.example.social_media.services;

import com.example.social_media.dtos.CreatePostRequest;
import com.example.social_media.dtos.EditPostRequest;
import com.example.social_media.dtos.PostDto;
import com.example.social_media.entities.Post;
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
    private UserService userService;

    public Post findById(Long id){
        return postRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Post #" + id + " was not found.")
        );
    }

    public PostDto getPostDtoById(Long id){
        return postMapper.toDto(findById(id));
    }

    public List<PostDto> getGlobalFeedByPage(int pages) {
        PageRequest pageRequest = PageRequest.of(pages, 7, Sort.by(Sort.Direction.DESC, "timeCreated"));
        return postRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getFollowingFeedByPage(long userId, int pages) {
        PageRequest pageRequest = PageRequest.of(pages, 7, Sort.by(Sort.Direction.DESC, "timeCreated"));
        return postRepository.getFeed(userId, pageRequest)
                .getContent()
                .stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getUsersPosts(long userId, int pages) {
        PageRequest pageRequest = PageRequest.of(pages, 7, Sort.by(Sort.Direction.DESC, "timeCreated"));
        return postRepository.findAllByUserId(userId, pageRequest)
                .getContent()
                .stream()
                .map(postMapper::toDto)
                .filter(post -> post.getUserId() == userId)
                .toList();
    }

    public PostDto createPost(CreatePostRequest request) {
        var user = userService.findById(request.getUserId());
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
}
