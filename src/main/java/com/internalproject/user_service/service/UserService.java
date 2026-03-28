package com.internalproject.user_service.service;

import com.internalproject.user_service.dto.*;
import com.internalproject.user_service.model.User;
import com.internalproject.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        try {
            User user = userRepository.findByEmailId(request.getEmail()).orElse(null);
            if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getUserId());
                return ResponseEntity.ok(new LoginResponse("Login successful", token, user.getUserId()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Invalid credentials", null, null));
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("An error occurred during login", null, null));
        }
    }

    public ResponseEntity<String> createUser(RegisterRequest request) {
        try {
            if (userRepository.existsByEmailId(request.getEmailId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
            }
            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmailId(request.getEmailId());
            user.setMobileNumber(request.getMobileNumber());
            // Hash the password before saving
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            logger.error("Error during user creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during user creation");
        }
    }

    public ResponseEntity<UserProfileResponse> fetchProfile(String userId) {
        try {
            return userRepository.findById(userId)
                    .map(user -> ResponseEntity.ok(
                            new UserProfileResponse(
                                    user.getUserId(),
                                    user.getFirstName(),
                                    user.getLastName(),
                                    user.getEmailId(),
                                    user.getMobileNumber()
                            )
                    ))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            logger.error("Error fetching profile for userId {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
