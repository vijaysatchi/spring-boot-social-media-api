package com.example.social_media.controllers;

import com.example.social_media.dtos.CreatePostRequest;
import com.example.social_media.dtos.EditPostRequest;
import com.example.social_media.dtos.PostDto;
import com.example.social_media.entities.User;
import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.services.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @GetMapping("/post/{id}")
    public ResponseEntity<PostDto> getPostDto(
            @PathVariable("id") Long postId,
            @AuthenticationPrincipal CustomUserDetails user){
        Long viewerId = user == null ? null : user.getId();
        var postDto = postService.getPostDtoById(postId, viewerId);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/post/feed/following")
    public ResponseEntity<List<PostDto>> getPostsFromFollowing(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page
    ){
        List<PostDto> posts = postService.getFollowingFeedByPage(user.getId(), page);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/post/feed/global")
    public ResponseEntity<List<PostDto>> getPostsFromGlobal(
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long viewerId = user == null ? null : user.getId();
        List<PostDto> posts = postService.getGlobalFeedByPage(viewerId, page);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{id}/post/{page}")
    public ResponseEntity<List<PostDto>> getPostsFromUser(
            @PathVariable(name="id") Long id,
            @RequestParam(required = false, defaultValue = "0", name="page") Integer page,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long viewerId = user == null ? null : user.getId();
        var postsList = postService.getUsersPosts(id, page, viewerId);
        return ResponseEntity.ok(postsList);
    }

    @GetMapping("/post/{id}/isliked/{user_id}")
    public ResponseEntity<Boolean> isPostLikedByUser(
            @PathVariable("id") Long post_id,
            @PathVariable("user_id") Long user_id
    ){
        var isLiked = postService.isLikedByUser(user_id, post_id);
        return ResponseEntity.ok(isLiked);
    }

    @PostMapping("/post")
    public ResponseEntity<PostDto> createPost(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid CreatePostRequest request,
            UriComponentsBuilder uriComponentsBuilder
            ){
        var postDto = postService.createPost(request, user.getId());
        var uri = uriComponentsBuilder.path("/api/post/{id}").buildAndExpand(postDto.getId()).toUri();
        return ResponseEntity.created(uri).body(postDto);
    }

    @PostMapping("/post/{id}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user){
        postService.togglePostLike(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/post/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable("id") Long postId,
            @RequestBody @Valid EditPostRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ){

        var postDto = postService.update(postId, user.getId(), request);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("id") Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        postService.deleteById(postId, user.getId());
        return ResponseEntity.ok().build();
    }
}
