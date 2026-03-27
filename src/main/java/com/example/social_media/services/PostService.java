package com.example.social_media.services;

import com.example.social_media.entities.Post;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.repositories.CommentRepository;
import com.example.social_media.repositories.PostRepository;
import com.example.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostService {
    private PostRepository postRepository;

    public Post findById(Long id){
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post #" + id + " was not found."));
    }
}
