 package com.internalproject.user_service.repository;

import com.internalproject.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmailId(String emailId);
    boolean existsByEmailId(String emailId);
}
