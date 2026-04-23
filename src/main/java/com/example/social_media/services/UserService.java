package com.example.social_media.services;

import com.example.social_media.dtos.users.*;
import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.entities.Follow;
import com.example.social_media.entities.User;
import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.exceptions.ResourceNotFoundException;
import com.example.social_media.mappers.UserMapper;
import com.example.social_media.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private CommentLikeRepository commentLikeRepository;
    private PostLikeRepository postLikeRepository;

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User #" + id + " was not found."));
    }

    public boolean doesPasswordMatch(String currentPassword, User user){
        if(!passwordEncoder.matches(
                currentPassword,
                user.getPassword()))
            throw new BadRequestException("Incorrect password.");
        return true;
    }

    public UserProfileDto getUserDtoById(Long userId, CustomUserDetails viewer){
        Long viewerId = viewer == null || viewer.getId().equals(userId) ? null : viewer.getId();
        if(viewerId == null) return userMapper.toProfileDto(findById(userId));
        return userRepository.findUserDtoByIdFromViewer(userId, viewerId).
                        orElseThrow(() ->
                                new ResourceNotFoundException("User #" + userId + " was not found."));
    }

    @Transactional
    public void follow(long id, long targetId) {
        if(id == targetId)
            throw new BadRequestException("You cannot follow yourself.");

        var user = findById(id);
        var targetUser = findById(targetId);
        if(followRepository.existsByFollowerAndFollowing(user, targetUser))
            throw new BadRequestException("You are already following this user.");
        Follow follow = user.follow(targetUser);
        followRepository.save(follow);
    }

    public List<UserFollowDto> getUsersFollowings(Long userId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return userRepository.findUserFollowings(userId, pageRequest)
                .toList();
    }

    public List<UserFollowDto> getUsersFollowers(Long userId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return userRepository.findUserFollowers(userId, pageRequest)
                .toList();
    }

    public List<UserFollowDto> getUsersMutuals(Long userId, Long viewerId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return userRepository.findUserMutuals(userId, viewerId, pageRequest)
                .toList();
    }

    @Transactional
    public UserProfileDto updateUser(Long id, UpdateUserProfileRequest request) {
        var user = findById(id);
        if(!request.getName().equals(user.getName()) && userRepository.existsByName(request.getName())){
            throw new BadRequestException("Username already exists.");
        }
        userMapper.updateEntity(request, user);
        return userMapper.toProfileDto(user);
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new BadRequestException("The passwords do not match.");
        }
        var user = findById(userId);
        doesPasswordMatch(request.getCurrentPassword(), user);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void unfollow(Long id, Long targetId) {
        var user = findById(id);
        var targetUser = findById(targetId);
        followRepository.findByFollowerAndFollowing(user, targetUser).orElseThrow(() ->
                new BadRequestException("You are not currently following this user.")
        );
        user.unfollow(targetUser);
        followRepository.deleteByFollowerAndFollowing(user, targetUser);
    }

    @Transactional
    public void delete(Long userId, DeleteAccountRequest request) {
        var user = findById(userId);
        doesPasswordMatch(request.getPassword(), user);
        commentRepository.removeDeletedUsersLikedCommentsFromCount(userId);
        postRepository.removeDeletedUsersLikedPostsFromCount(userId);
        commentLikeRepository.deleteByUserId(userId);
        postLikeRepository.deleteByUserId(userId);
        commentRepository.deleteByUserId(userId);
        postRepository.deleteByUserId(userId);
        followRepository.removeDeletedUsersFollowers(userId);
        followRepository.removeDeletedUsersFollowings(userId);
        userRepository.deleteById(userId);
    }
}
