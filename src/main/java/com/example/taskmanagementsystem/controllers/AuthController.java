package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.security.JwtProvider;
import com.example.taskmanagementsystem.security.dto.AuthRequest;
import com.example.taskmanagementsystem.security.dto.AuthResponse;
import com.example.taskmanagementsystem.security.dto.RegistrationRequest;
import com.example.taskmanagementsystem.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yina-ship-it
 * @since 08.12.2023
 */
@RestController
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest){
        return null;
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid AuthRequest authRequest) {
        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUser() {
        return null;
    }
}
