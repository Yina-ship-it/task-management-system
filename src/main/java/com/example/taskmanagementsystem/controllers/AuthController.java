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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yina-ship-it
 * @since 08.12.2023
 */
@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest){
        try {
            User user = User.builder()
                    .email(registrationRequest.getEmail())
                    .password(registrationRequest.getPassword())
                    .name(registrationRequest.getName())
                    .build();
            userService.saveUser(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid AuthRequest authRequest) {
        try {
            User user = userService.findByEmailAndPassword(authRequest.getEmail(), authRequest.getPassword());
            String token = jwtProvider.generateToken(user.getEmail());
            AuthResponse response = AuthResponse.builder().token(token).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
