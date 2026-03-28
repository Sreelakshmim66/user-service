package com.internalproject.user_service.service;

import com.internalproject.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security calls this with userId (the JWT subject)
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUserId())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
