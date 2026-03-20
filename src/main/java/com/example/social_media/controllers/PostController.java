package com.example.social_media.controllers;

import com.example.social_media.dtos.CreatePostRequest;
import com.example.social_media.dtos.EditPostRequest;
import com.example.social_media.dtos.PostDto;
import com.example.social_media.mappers.PostMapper;
import com.example.social_media.repositories.PostRepository;
import com.example.social_media.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id){
        var post = postRepository.findById(id).orElse(null);
        if(post == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postMapper.toDto(post));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @RequestBody CreatePostRequest request,
            UriComponentsBuilder uriComponentsBuilder
            ){

        var user = userRepository.findById(request.getUserId()).orElse(null);
        if(user == null){
            return ResponseEntity.badRequest().build();
        }

        var post = postMapper.toEntity(request);
        user.addPost(post);
        postRepository.save(post);

        var postDto = postMapper.toDto(post);
        var uri = uriComponentsBuilder.path("/api/post/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(uri).body(postDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid EditPostRequest request
    ){
        var post = postRepository.findById(id).orElse(null);
        if(post == null){
            return ResponseEntity.notFound().build();
        }
        postMapper.update(request, post);
        var postDto = postMapper.toDto(postRepository.save(post));
        return ResponseEntity.ok(postDto);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        var post = postRepository.findById(id).orElse(null);
        if(post == null){
            return ResponseEntity.notFound().build();
        }
        post.getUser().removePost(post);
        return ResponseEntity.ok().build();
    }
}
