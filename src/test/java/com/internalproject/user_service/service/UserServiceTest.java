package com.internalproject.user_service.service;

import com.internalproject.user_service.dto.LoginRequest;
import com.internalproject.user_service.dto.LoginResponse;
import com.internalproject.user_service.dto.RegisterRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("test-user-id");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setMobileNumber("1234567890");
        testUser.setPassword("hashed-password");
    }

    @Test
    void login_withValidCredentials_returnsOk() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("test-user-id")).thenReturn("jwt-token");

        ResponseEntity<LoginResponse> response = userService.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals("test-user-id", response.getBody().getUserId());
    }

    @Test
    void login_withInvalidPassword_returnsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("wrong-password");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        ResponseEntity<LoginResponse> response = userService.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_withUnknownEmail_returnsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseEntity<LoginResponse> response = userService.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void createUser_withNewEmail_returnsCreated() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane.doe@example.com");
        request.setMobileNumber("0987654321");
        request.setPassword("password");

        when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<String> response = userService.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody());
    }

    @Test
    void createUser_withExistingEmail_returnsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        ResponseEntity<String> response = userService.createUser(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void fetchProfile_withValidUserId_returnsProfile() {
        when(userRepository.findById("test-user-id")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userService.fetchProfile("test-user-id");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void fetchProfile_withUnknownUserId_returnsNotFound() {
        when(userRepository.findById("unknown-id")).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.fetchProfile("unknown-id");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
