package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.models.User;

import java.util.List;

/**
 * @author Yina-ship-it
 * @since 08.12.2023
 */
public interface UserService {
    List<User> findAllUsers();
    User findByEmail(String email);
    User findById(Long id);
    User findByEmailAndPassword(String email, String password);
    void saveUser(User user);

    User updateUserNameByEmail(String email, String name);
    User updateUserEmailByEmail(String email, String newEmail);
    User updateUserPasswordByEmail(String email, String password);
}
