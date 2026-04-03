package com.example.social_media.services;

import com.example.social_media.entities.CustomUserDetails;
import com.example.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException(username + " was not found."));
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), null);
    }

    public UserDetails getCustomUserDetails(long id, String email) throws UsernameNotFoundException {
        return new CustomUserDetails(id, email, null, null);
    }
}
