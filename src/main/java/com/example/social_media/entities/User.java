package com.example.social_media.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDate dateCreated;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "banner_colour")
    private String bannerColour;

    @Column(name = "bio")
    private String bio;

    @Column(name = "followers_count", nullable = false)
    private Long followersCount = 0L;

    @Column(name = "following_count", nullable = false)
    private Long followingCount = 0L;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "following", orphanRemoval = true)
    private List<Follow> followers;

    @OneToMany(mappedBy = "follower", orphanRemoval = true)
    private List<Follow> following;

    public void addPost(Post post){
        posts.add(post);
        post.setUser(this);
    }

    public void removePost(Post post){
        posts.remove(post);
        post.setUser(null);
    }

    public void addComment(Comment comment){
        comments.add(comment);
        comment.setUser(this);
    }

    public void removeComment(Comment comment){
        comments.remove(comment);
        comment.setUser(null);
    }

    public Follow follow(User userToFollow){
        Follow follow = new Follow(this, userToFollow);
        followers.add(follow);
        this.followingCount++;
        userToFollow.followersCount++;
        userToFollow.following.add(follow);
        return follow;
    }

    public void unfollow(User userToUnfollow){
        this.followingCount--;
        userToUnfollow.followersCount--;
    }
}
