package com.internalproject.user_service.service;

import com.internalproject.user_service.model.User;
import com.internalproject.user_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUserId())  // treat email as username
                .password(user.getPassword())
                .roles("USER") // or from DB if you add role field
                .build();
    }
}
