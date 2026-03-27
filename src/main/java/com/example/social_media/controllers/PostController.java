package com.example.social_media.controllers;

import com.example.social_media.dtos.CreatePostRequest;
import com.example.social_media.dtos.EditPostRequest;
import com.example.social_media.dtos.PostDto;
import com.example.social_media.services.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @GetMapping("/post/{id}")
    public ResponseEntity<PostDto> getPostDto(@PathVariable Long id){
        var postDto = postService.getPostDtoById(id);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/post/feed/following/{id}")
    public ResponseEntity<List<PostDto>> getAllPosts(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page
    ){
        List<PostDto> posts = postService.getFollowingFeedByPage(id, page);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/post/feed/global")
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page
    ){
        List<PostDto> posts = postService.getGlobalFeedByPage(page);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{id}/post/{page}")
    public ResponseEntity<List<PostDto>> getPosts(
            @PathVariable(name="id") Long id,
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page
    ){
        var postsList = postService.getUsersPosts(id, page);
        return ResponseEntity.ok(postsList);
    }

    @PostMapping("/post")
    public ResponseEntity<PostDto> createPost(
            @RequestBody @Valid CreatePostRequest request,
            UriComponentsBuilder uriComponentsBuilder
            ){
        var postDto = postService.createPost(request);
        var uri = uriComponentsBuilder.path("/api/post/{id}").buildAndExpand(postDto.getId()).toUri();
        return ResponseEntity.created(uri).body(postDto);
    }

    @PatchMapping("/post/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid EditPostRequest request
    ){

        var postDto = postService.update(id, request);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
