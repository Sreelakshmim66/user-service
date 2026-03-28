package com.internalproject.user_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "email_id", unique = true, nullable = false)
    private String emailId;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;
}
