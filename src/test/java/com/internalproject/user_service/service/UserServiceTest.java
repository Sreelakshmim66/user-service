package com.internalproject.user_service.service;

import com.internalproject.user_service.model.User;
import com.internalproject.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("user123");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmailId("john.doe@example.com");
        testUser.setMobileNumber("1234567890");
    }

    @Test
    void loginWithValidCredentialsShouldReturnSuccess() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        ResponseEntity<String> response = userService.login("user123", "password123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login Successful", response.getBody());
        verify(userRepository, times(1)).findById("user123");
    }

    @Test
    void loginWithInvalidPasswordShouldReturnBadRequest() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        ResponseEntity<String> response = userService.login("user123", "wrongPassword");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Credentials", response.getBody());
        verify(userRepository, times(1)).findById("user123");
    }

    @Test
    void loginWithNonExistentUserShouldReturnBadRequest() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseEntity<String> response = userService.login("nonexistent", "password123");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Credentials", response.getBody());
        verify(userRepository, times(1)).findById("nonexistent");
    }

    @Test
    void loginWithNullUserIdShouldHandleGracefully() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = userService.login(null, "password123");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void loginWhenRepositoryThrowsExceptionShouldReturnInternalServerError() {
        when(userRepository.findById(anyString())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<String> response = userService.login("user123", "password123");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred during login", response.getBody());
    }

    @Test
    void createUserWithNewUserShouldReturnCreated() {
        when(userRepository.existsById("user123")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<String> response = userService.createUser(testUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody());
        verify(userRepository, times(1)).existsById("user123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void createUserWithExistingUserIdShouldReturnConflict() {
        when(userRepository.existsById("user123")).thenReturn(true);

        ResponseEntity<String> response = userService.createUser(testUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
        verify(userRepository, times(1)).existsById("user123");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserWhenRepositoryThrowsExceptionShouldReturnInternalServerError() {
        when(userRepository.existsById(anyString())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<String> response = userService.createUser(testUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred during user creation", response.getBody());
    }

    @Test
    void createUserWithNullUserIdShouldHandleGracefully() {
        User userWithNullId = new User();
        userWithNullId.setUserId(null);
        userWithNullId.setPassword("password123");

        when(userRepository.existsById(anyString())).thenThrow(new IllegalArgumentException("ID cannot be null"));

        ResponseEntity<String> response = userService.createUser(userWithNullId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void fetchProfileWithExistingUserShouldReturnUser() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userService.fetchProfile("user123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user123", response.getBody().getUserId());
        assertEquals("John", response.getBody().getFirstName());
        verify(userRepository, times(2)).findById("user123");
    }

    @Test
    void fetchProfileWithNonExistentUserShouldReturnNotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userService.fetchProfile("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, times(1)).findById("nonexistent");
    }

    @Test
    void fetchProfileWhenRepositoryThrowsExceptionShouldReturnInternalServerError() {
        when(userRepository.findById(anyString())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<User> response = userService.fetchProfile("user123");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void fetchProfileWithNullUserIdShouldHandleGracefully() {
        when(userRepository.findById(anyString())).thenThrow(new IllegalArgumentException("ID cannot be null"));

        ResponseEntity<User> response = userService.fetchProfile(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void loginWithEmptyPasswordShouldReturnBadRequest() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        ResponseEntity<String> response = userService.login("user123", "");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Credentials", response.getBody());
    }

    @Test
    void createUserWithEmptyPasswordShouldStillCreateUser() {
        User userWithEmptyPassword = new User();
        userWithEmptyPassword.setUserId("user456");
        userWithEmptyPassword.setPassword("");

        when(userRepository.existsById("user456")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(userWithEmptyPassword);

        ResponseEntity<String> response = userService.createUser(userWithEmptyPassword);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository, times(1)).save(userWithEmptyPassword);
    }

    @Test
    void createUserWithAllFieldsPopulatedShouldSucceed() {
        when(userRepository.existsById("user123")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<String> response = userService.createUser(testUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody());
    }

    @Test
    void fetchProfileShouldCallRepositoryCorrectNumberOfTimes() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        userService.fetchProfile("user123");

        verify(userRepository, times(2)).findById("user123");
    }
}

