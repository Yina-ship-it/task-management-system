package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Yina-ship-it
 * @since 10.12.2023
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void getAllTasks_AuthorisedRequest_ShouldReturnListOfCountries() throws Exception {
        // Arrange
        String password = passwordEncoder.encode("Password");
        User user1 = userRepository.save(
                User.builder().name("maksim1").email("maksim1@mail.test").password(password).build());
        User user2 = userRepository.save(
                User.builder().name("maksim2").email("maksim2@mail.test").password(password).build());
        User user3 = userRepository.save(
                User.builder().name("maksim3").email("maksim3@mail.test").password(password).build());

        List<Task> tasks = taskRepository.saveAll(List.of(
                Task.builder()
                        .title("TestTask1")
                        .description("task 1")
                        .priority(TaskPriority.MEDIUM)
                        .status(TaskStatus.IN_PROGRESS)
                        .author(user1)
                        .assignees(Set.of(user2, user3))
                        .build(),
                Task.builder()
                        .title("TestTask2")
                        .description("task 2")
                        .priority(TaskPriority.LOW)
                        .status(TaskStatus.COMPLETED)
                        .author(user2)
                        .assignees(Set.of(user1, user3))
                        .build()
        ));
        List<TaskResponse> taskResponses = tasks.stream()
                .map(taskDtoConverter::convertEntityToDto)
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();

        String token = jwtProvider.generateToken(user1.getEmail());

        // Act
        mockMvc.perform(get("/api/tasks/")
                .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(taskResponses)));

    }

    @Test
    void getAllTasks_UnauthorisedRequest_ShouldReturnForribenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/"))
                // Assert
                .andExpect(status().isForbidden());

    }
}