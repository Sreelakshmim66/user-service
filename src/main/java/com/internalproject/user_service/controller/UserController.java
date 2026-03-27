package com.internalproject.user_service.controller;

import com.internalproject.user_service.dto.LoginRequest;
import com.internalproject.user_service.model.User;
import com.internalproject.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is running");
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest.getUserId(), loginRequest.getPassword());
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> newUserAdded(@RequestBody User user) {
         return userService.createUser(user);
    }

    @GetMapping("/fetchProfile/{userId}")
    public ResponseEntity<User> fetchProfile(@PathVariable String userId) {
        return userService.fetchProfile(userId);
    }
}
