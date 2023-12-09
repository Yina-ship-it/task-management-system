package com.example.taskmanagementsystem.services.impl;

import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Yina-ship-it
 * @since 08.12.2023
 */
@Service
@Primary
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email=" + email + "not found!"));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id=" + id + "not found!"));
    }

    @Override
    public User findByEmailAndPassword(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()){
            if (passwordEncoder.matches(password, user.get().getPassword())){
                return user.get();
            }
        }
        return null;
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
