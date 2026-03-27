package com.example.social_media.repositories;

import com.example.social_media.dtos.CommentDto;
import com.example.social_media.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long id);
    Page<Comment> findByPostId(Long id, PageRequest pageRequest);
}
