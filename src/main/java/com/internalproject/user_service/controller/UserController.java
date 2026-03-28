package com.internalproject.user_service.controller;

import com.internalproject.user_service.dto.*;
import com.internalproject.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is running");
    }

    // POST /api/auth/login  (public - no JWT needed)
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    // POST /api/auth/register  (public - no JWT needed)
    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        return userService.createUser(request);
    }

    // GET /api/users/{userId}  (protected - requires JWT)
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserProfileResponse> fetchProfile(@PathVariable String userId) {
        return userService.fetchProfile(userId);
    }
}
