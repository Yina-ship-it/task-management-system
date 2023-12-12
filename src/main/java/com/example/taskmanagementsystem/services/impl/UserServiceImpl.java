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

import java.util.List;
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
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

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
        validateEmail(user.getEmail());
        validateName(user.getName());
        validatePassword(user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User updateUserNameByEmail(String email, String name) {
        validateName(name);
        User user = findByEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    @Override
    public User updateUserEmailByEmail(String email, String newEmail) {
        validateEmail(newEmail);

        Optional<User> userWithNewEmail = userRepository.findByEmail(newEmail);

        if(userWithNewEmail.isPresent())
            throw new IllegalArgumentException("Email " + newEmail + " is taken by another user");

        User user = findByEmail(email);
        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    @Override
    public User updateUserPasswordByEmail(String email, String password) {
        validatePassword(password);
        User user = findByEmail(email);
        user.setPassword(password);
        saveUser(user);
        return user;
    }

    private void validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format for " + email);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("invalid user password");
    }

    private void validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("invalid user name");
    }
}
