package com.example.taskmanagementsystem.repositories;

import com.example.taskmanagementsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Yina-ship-it
 * @since 08.12.2023
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
