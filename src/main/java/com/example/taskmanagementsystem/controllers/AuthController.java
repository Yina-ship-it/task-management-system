package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.security.JwtProvider;
import com.example.taskmanagementsystem.security.dto.AuthRequest;
import com.example.taskmanagementsystem.security.dto.AuthResponse;
import com.example.taskmanagementsystem.security.dto.RegistrationRequest;
import com.example.taskmanagementsystem.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirements
@Tag(name = "User authorisation")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @Operation(summary = "User registration.", description = "Allows to register a user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid Input Data", content = @Content)
    })
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

    @Operation(summary = "User authorisation.", description = "Allows to get a JWT authorisation token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authorisation successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid Input Data", content = @Content)
    })
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
