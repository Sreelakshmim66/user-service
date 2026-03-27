package com.internalproject.user_service.dto;

public class LoginResponse {
    private String message;
    private String token;

    public LoginResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }
}
