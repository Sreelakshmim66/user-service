package com.internalproject.user_service.service;

import com.internalproject.user_service.model.User;
import com.internalproject.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<String> login(String userId, String password) {
        try {
            User user = userRepository.findById(String.valueOf(userId)).orElse(null);
            if (user != null && user.getPassword().equals(password)) {
                return new ResponseEntity<>("Login Successful", HttpStatus.OK);
            }
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error during login: " + e.getMessage());
            return new ResponseEntity<>("An error occurred during login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> createUser(User user) {
        try {
            String userId = user.getUserId();
            if (userRepository.existsById(String.valueOf(userId))) {
                return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
            } else {
                userRepository.save(user);
                return new ResponseEntity<>("success", HttpStatus.CREATED);
            }
        } catch (Exception e) {
            logger.error("Error during user creation: " + e.getMessage());
            return new ResponseEntity<>("An error occurred during user creation", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<User> fetchProfile(String userId) {
        try {
            User user = userRepository.findById(String.valueOf(userId)).orElse(null);
            if (user != null) {
                return new ResponseEntity<>(userRepository.findById(String.valueOf(userId)).orElse(null), HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error during user creation: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
