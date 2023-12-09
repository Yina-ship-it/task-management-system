package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.models.User;

/**
 * @author Yina-ship-it
 * @since 08.12.2023
 */
public interface UserService {
    User findByEmail(String email);
    User findById(Long id);
    User findByEmailAndPassword(String email, String password);
    void saveUser(User user);
}
