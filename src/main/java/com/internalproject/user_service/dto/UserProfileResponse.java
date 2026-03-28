package com.internalproject.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String emailId;
    private String mobileNumber;
}
