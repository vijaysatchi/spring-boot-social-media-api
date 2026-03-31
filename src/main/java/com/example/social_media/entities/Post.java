package com.example.social_media.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "caption")
    private String caption;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime timeCreated;

    @Column(name = "like_count")
    private Long likeCount;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Comment> comments;

    public void addComment(Comment comment){
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment){
        comments.remove(comment);
        comment.setPost(null);
    }
}
