package com.internalproject.user_service.controller;

import com.internalproject.user_service.model.User;
import com.internalproject.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class UserController {

    @Autowired
    UserService userService;

//    @GetMapping("/listAllUsers")
//    public List<User> listAllUsers() {
//        return userService.listAllUsers();
//    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestParam String userId, @RequestParam String password) {
        return userService.login(userId, password);
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
