package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.profile.UserResponse;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskRequest;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Yina-ship-it
 * @since 10.12.2023
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskFieldControllerIntegrationTest {

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

    private String token;
    private List<User> users;
    private List<Task> tasks;

    @BeforeEach
    void setUp() {
        this.users = createUsers();
        this.token = jwtProvider.generateToken(users.get(0).getEmail());
        this.tasks = createTasks();
    }

    private List<User> createUsers(){
        String password = passwordEncoder.encode("Password");
        return userRepository.saveAll(List.of(
                User.builder().name("maksim1").email("maksim1@mail.test").password(password).build(),
                User.builder().name("maksim2").email("maksim2@mail.test").password(password).build(),
                User.builder().name("maksim3").email("maksim3@mail.test").password(password).build()
        ));
    }

    private List<Task> createTasks(){
        return taskRepository.saveAll(List.of(
                Task.builder()
                        .title("TestTask1")
                        .description("task 1")
                        .priority(TaskPriority.MEDIUM)
                        .status(TaskStatus.IN_PROGRESS)
                        .author(users.get(0))
                        .assignees(Set.of(users.get(1), users.get(2)))
                        .build(),
                Task.builder()
                        .title("TestTask2")
                        .description("task 2")
                        .priority(TaskPriority.LOW)
                        .status(TaskStatus.COMPLETED)
                        .author(users.get(1))
                        .assignees(Set.of(users.get(0), users.get(2)))
                        .build()
        ));
    }

    @Test
    void getTitle_ShouldReturnOkStatusAndTaskTitle() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("title", tasks.get(1).getTitle());

        // Act
        mockMvc.perform(get("/api/tasks/{id}/title", tasks.get(1).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getTitle_WhenTaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/title", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getTitle_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/title", tasks.get(1).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getDescription_ShouldReturnOkStatusAndTaskDescription() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("description", tasks.get(1).getDescription());

        // Act
        mockMvc.perform(get("/api/tasks/{id}/description", tasks.get(1).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getDescription_WhenTaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/description", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getDescription_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/description", tasks.get(1).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getStatus_ShouldReturnOkStatusAndTaskStatus() throws Exception {
        // Arrange
        Map<String, TaskStatus> response = new HashMap<>();
        response.put("status", tasks.get(1).getStatus());

        // Act
        mockMvc.perform(get("/api/tasks/{id}/status", tasks.get(1).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getStatus_WhenTaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/status", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getStatus_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/status", tasks.get(1).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getPriority_ShouldReturnOkStatusAndTaskPriority() throws Exception {
        // Arrange
        Map<String, TaskPriority> response = new HashMap<>();
        response.put("priority", tasks.get(1).getPriority());

        // Act
        mockMvc.perform(get("/api/tasks/{id}/priority", tasks.get(1).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getPriority_WhenTaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/priority", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getPriority_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/priority", tasks.get(1).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getAuthor_ShouldReturnOkStatusAndTaskAuthor() throws Exception {
        // Arrange
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskDtoConverter.convertEntityToDto(tasks.get(1))
        );
        Map<String, UserResponse> response = new HashMap<>();
        response.put("author", task.getAuthor());

        // Act
        mockMvc.perform(get("/api/tasks/{id}/author", tasks.get(1).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAuthor_WhenTaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/author", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getAuthor_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/author", tasks.get(1).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getAssignees_ShouldReturnOkStatusAndTaskAssignees() throws Exception {
        // Arrange
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskDtoConverter.convertEntityToDto(tasks.get(1))
        );
        Map<String, List<UserResponse>> response = new HashMap<>();
        response.put("assignees", task.getAssignees());

        // Act
        mockMvc.perform(get("/api/tasks/{id}/assignees", tasks.get(1).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAssignees_WhenTaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/assignees", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getAssignees_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/{id}/assignees`", tasks.get(1).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }
}