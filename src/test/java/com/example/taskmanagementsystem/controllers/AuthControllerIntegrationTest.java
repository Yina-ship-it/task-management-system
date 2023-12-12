package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.profile.UserResponse;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.security.JwtProvider;
import com.example.taskmanagementsystem.security.dto.AuthRequest;
import com.example.taskmanagementsystem.security.dto.AuthResponse;
import com.example.taskmanagementsystem.security.dto.RegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_WhenValidRegistrationRequest_ShouldReturnOkStatus() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("test@email.test", "TestPassword", "maksim");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        Optional<User> registeredUser = userRepository.findByEmail(registrationRequest.getEmail());
        assertTrue(registeredUser.isPresent());
        assertTrue(passwordEncoder.matches(registrationRequest.getPassword(),
                registeredUser.get().getPassword()));
    }

    @Test
    void registerUser_WhenRegistrationRequestWithDuplicateEmail_ShouldReturnBadRequestStatus() throws Exception {
        User user = userRepository.save(
                User.builder()
                        .email("test@email.test")
                        .password("TestPassword1")
                        .name("maksim")
                        .build()
        );

        RegistrationRequest registrationRequest = new RegistrationRequest(user.getEmail(), "TestPassword2", "maksim");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WhenRegistrationRequestWithoutEmail_ShouldReturnBadRequestStatus() throws Exception {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .password("TestPassword")
                .name("maksim")
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WhenRegistrationRequestWithoutPassword_ShouldReturnBadRequestStatus() throws Exception {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .email("test@email.test")
                .name("maksim")
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WhenRegistrationRequestWithoutName_ShouldReturnBadRequestStatus() throws Exception {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .email("test@email.test")
                .password("TestPassword")
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WhenRegistrationRequestWithInvalidEmail_ShouldReturnBadRequestStatus() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("TestInvalidEmail", "TestPassword", "maksim");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void auth_WhenValidAuthRequest_ShouldReturnAuthResponse() throws Exception {
        AuthRequest authRequest = new AuthRequest("test@email.test", "TestPassword");
        userRepository.save(
                User.builder()
                        .email(authRequest.getEmail())
                        .password(passwordEncoder.encode(authRequest.getPassword()))
                        .name("maksim")
                        .build()
        );

        String responseContent = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthResponse response = objectMapper.readValue(responseContent, AuthResponse.class);

        assertEquals(authRequest.getEmail(), jwtProvider.getEmailFromToken(response.getToken()));
    }

    @Test
    void auth_WhenAuthRequestWithUnsuitablePassword_ShouldReturnBadRequestStatus() throws Exception {
        AuthRequest authRequest = new AuthRequest("test@email.test", "TestPassword2");
        userRepository.save(
                User.builder()
                        .email(authRequest.getEmail())
                        .password(passwordEncoder.encode("TestPassword1"))
                        .name("maksim")
                        .build()
        );

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void auth_WhenAuthRequestWithNonExistentEmail_ShouldReturnBadRequestStatus() throws Exception {
        AuthRequest authRequest = new AuthRequest("test@email.test", "TestPassword");

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void auth_WhenAuthRequestWithoutEmail_ShouldReturnBadRequestStatus() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .password("TestPassword")
                .build();

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void auth_WhenAuthRequestWithoutPassword_ShouldReturnBadRequestStatus() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .email("test@email.test")
                .build();

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_WhenAuthenticatedWithValidJwtToken_ShouldReturnUser() throws Exception {
        User user = userRepository.save(
                User.builder()
                        .email("test@email.test")
                        .password(passwordEncoder.encode("PasswordTest"))
                        .name("maksim")
                        .build()
        );
        String jwtToken = jwtProvider.generateToken(user.getEmail());

        String responseContent = mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse foundUser = objectMapper.readValue(responseContent, UserResponse.class);

        assertNotNull(foundUser);
        assertEquals(foundUser.getEmail(), user.getEmail());
        assertEquals(foundUser.getName(), user.getName());
    }

    @Test
    void getUser_WhenAuthenticatedWithInvalidJwtToken_ShouldReturnUser() throws Exception {
        userRepository.save(
                User.builder()
                        .email("test1@email.test")
                        .password(passwordEncoder.encode("PasswordTest"))
                        .name("maksim")
                        .build()
        );
        String jwtToken = jwtProvider.generateToken("test2@email.test");

        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_WhenAuthenticatedWithExpiredJwtToken_ShouldReturnForbiddenStatus() throws Exception {
        User user = userRepository.save(
                User.builder()
                        .email("test@email.test")
                        .password(passwordEncoder.encode("PasswordTest"))
                        .name("maksim")
                        .build()
        );
        Date date = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        String jwtToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }
}