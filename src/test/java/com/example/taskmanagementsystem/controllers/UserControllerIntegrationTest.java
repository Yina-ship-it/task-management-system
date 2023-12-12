package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.dto.user.UserResponseConverter;
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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Yina-ship-it
 * @since 13.12.2023
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserResponseConverter userResponseConverter;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

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
                User.builder().name("maksim3").email("maksim3@mail.test").password(password).build(),
                User.builder().name("maksim4").email("maksim4@mail.test").password(password).build()
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
                        .assignees(new ArrayList<>(List.of(users.get(1), users.get(2))))
                        .comments(new ArrayList<>())
                        .build(),
                Task.builder()
                        .title("TestTask2")
                        .description("task 2")
                        .priority(TaskPriority.HIGH)
                        .status(TaskStatus.IN_PROGRESS)
                        .author(users.get(0))
                        .assignees(new ArrayList<>(List.of(users.get(2))))
                        .comments(new ArrayList<>())
                        .build(),
                Task.builder()
                        .title("TestTask3")
                        .description("task 3")
                        .priority(TaskPriority.LOW)
                        .status(TaskStatus.PENDING)
                        .author(users.get(0))
                        .assignees(new ArrayList<>(List.of(users.get(0))))
                        .comments(new ArrayList<>())
                        .build(),
                Task.builder()
                        .title("TestTask4")
                        .description("task 4")
                        .priority(TaskPriority.MEDIUM)
                        .status(TaskStatus.IN_PROGRESS)
                        .author(users.get(2))
                        .assignees(new ArrayList<>(List.of(users.get(1), users.get(2))))
                        .comments(new ArrayList<>())
                        .build(),
                Task.builder()
                        .title("TestTask5")
                        .description("task 5")
                        .priority(TaskPriority.LOW)
                        .status(TaskStatus.IN_PROGRESS)
                        .author(users.get(2))
                        .assignees(new ArrayList<>(List.of(users.get(1), users.get(0))))
                        .comments(new ArrayList<>())
                        .build(),
                Task.builder()
                        .title("TestTask6")
                        .description("task 6")
                        .priority(TaskPriority.MEDIUM)
                        .status(TaskStatus.PENDING)
                        .author(users.get(2))
                        .assignees(new ArrayList<>(List.of(users.get(0))))
                        .comments(new ArrayList<>())
                        .build(),
                Task.builder()
                        .title("TestTask7")
                        .description("task 7")
                        .priority(TaskPriority.LOW)
                        .status(TaskStatus.COMPLETED)
                        .author(users.get(2))
                        .assignees(new ArrayList<>(List.of(users.get(2))))
                        .comments(new ArrayList<>())
                        .build()
        ));
    }

    @Test
    void getUsers_ShouldReturnOkStatusAndListOfUserResponse() throws Exception {
        // Arrange
        List<UserResponse> response = users.stream().map(userResponseConverter::convertUserToResponse).toList();

        // Act
        mockMvc.perform(get("/api/users/")
                .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getUsers_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/"))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_WhenUserExist_ShouldReturnOkStatusAndUserResponse() throws Exception {
        // Arrange
        UserResponse response = userResponseConverter.convertUserToResponse(users.get(2));

        // Act
        mockMvc.perform(get("/api/users/{id}", users.get(2).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getUserById_WhenUserNonExist_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}", users.get(2).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getMe_WhenAuthenticatedWithValidJwtToken_ShouldReturnOkStatusAndUserResponse() throws Exception {
        // Arrange
        UserResponse response = userResponseConverter.convertUserToResponse(users.get(0));

        // Act
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getMe_WhenAuthenticatedWithInvalidJwtToken_ShouldReturnNotFoundStatus() throws Exception {
        // Arrange
        String jwtToken = jwtProvider.generateToken("test@email.test");

        // Act
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getMe_WhenAuthenticatedWithExpiredJwtToken_ShouldReturnForbiddenStatus() throws Exception {
        // Arrange
        Date date = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        String jwtToken = Jwts.builder()
                .setSubject(users.get(0).getEmail())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        // Act
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getMe_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/me/"))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasksCreatedByUser_WhenUserExist_ShouldReturnOkStatusAndListOfTaskReturn() throws Exception {
        // Arrange
        List<TaskResponse> response = tasks.stream()
                .filter(task -> task.getAuthor().equals(users.get(2)))
                .map(taskDtoConverter::convertEntityToDto)
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();

        // Act
        mockMvc.perform(get("/api/users/{id}/created-tasks", users.get(2).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getTasksCreatedByUser_WhenUserNonExist_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/created-tasks", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksCreatedByUser_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/created-tasks", users.get(2)))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasksAssignedToUser_WhenUserExist_ShouldReturnOkStatusAndListOfTaskReturn() throws Exception {
        // Arrange
        List<TaskResponse> response = tasks.stream()
                .filter(task -> task.getAssignees().contains(users.get(2)))
                .map(taskDtoConverter::convertEntityToDto)
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();

        // Act
        mockMvc.perform(get("/api/users/{id}/assigned-tasks", users.get(2).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getTasksAssignedToUser_WhenUserNonExist_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/assigned-tasks", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksAssignedToUser_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/assigned-tasks", users.get(2)))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasksCreatedByMe_ShouldReturnOkStatusAndListOfTaskReturn() throws Exception {
        // Arrange
        List<TaskResponse> response = tasks.stream()
                .filter(task -> task.getAuthor().equals(users.get(0)))
                .map(taskDtoConverter::convertEntityToDto)
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();

        // Act
        mockMvc.perform(get("/api/users/me/created-tasks")
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getTasksCreatedByMe_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/me/created-tasks"))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasksAssignedToMe_ShouldReturnOkStatusAndListOfTaskReturn() throws Exception {
        // Arrange
        List<TaskResponse> response = tasks.stream()
                .filter(task -> task.getAssignees().contains(users.get(0)))
                .map(taskDtoConverter::convertEntityToDto)
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();

        // Act
        mockMvc.perform(get("/api/users/me/assigned-tasks")
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getTasksAssignedToMe_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/me/assigned-tasks"))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void updateName_WhenValidNameInput_ShouldReturnOkStatusAndUserResponseWithNewName() throws Exception {
        // Arrange
        String name = "Valid Name";
        UserResponse response = userResponseConverter.convertUserToResponse(users.get(0));
        response.setName(name);

        // Act
        mockMvc.perform(put("/api/users/me/name")
                        .header("Authorization", "Bearer " + token)
                        .param("name", name))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updateName_WhenEmptyNameInput_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String name = null;

        // Act
        mockMvc.perform(put("/api/users/me/name")
                        .header("Authorization", "Bearer " + token)
                        .param("name", name))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateName_WhenBlankNameInput_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String name = "";

        // Act
        mockMvc.perform(put("/api/users/me/name")
                        .header("Authorization", "Bearer " + token)
                        .param("name", name))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateName_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Arrange
        String name = "Valid Name";

        // Act
        mockMvc.perform(put("/api/users/me/name")
                        .param("name", name))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void updateEmail_WhenValidEmailInput_ShouldReturnOkStatusAndUserResponseWithNewEmail() throws Exception {
        // Arrange
        String email = "valid.email@mail.test";
        UserResponse response = userResponseConverter.convertUserToResponse(users.get(0));
        response.setEmail(email);

        // Act
        mockMvc.perform(put("/api/users/me/email")
                        .header("Authorization", "Bearer " + token)
                        .param("email", email))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updateEmail_WhenEmailInputBelongsAnotherUser_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String email = users.get(1).getEmail();

        // Act
        mockMvc.perform(put("/api/users/me/email")
                        .header("Authorization", "Bearer " + token)
                        .param("email", email))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmail_WhenInvalidEmailInput_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String email = "Invalid Email";

        // Act
        mockMvc.perform(put("/api/users/me/email")
                        .header("Authorization", "Bearer " + token)
                        .param("email", email))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmail_WhenEmptyEmailInput_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String email = null;

        // Act
        mockMvc.perform(put("/api/users/me/email")
                        .header("Authorization", "Bearer " + token)
                        .param("email", email))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmail_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Arrange
        String email = "valid.email@mail.test";

        // Act
        mockMvc.perform(put("/api/users/me/email")
                        .param("email", email))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePassword_WhenValidPasswordInput_ShouldReturnOkStatusAndUserResponse() throws Exception {
        // Arrange
        String password = "valid_password12321";
        UserResponse response = userResponseConverter.convertUserToResponse(users.get(0));

        // Act
        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", "Bearer " + token)
                        .param("password", password))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        // Assert
        assertTrue(passwordEncoder.matches(password, users.get(0).getPassword()));
    }

    @Test
    void updatePassword_WhenEmptyPasswordInput_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String password = null;

        // Act
        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", "Bearer " + token)
                        .param("password", password))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePassword_WhenBlankPasswordInput_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String password = "";

        // Act
        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", "Bearer " + token)
                        .param("password", password))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePassword_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Arrange
        String password = "valid_password12321";

        // Act
        mockMvc.perform(put("/api/users/me/password")
                        .param("password", password))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserIdById_WhenUserExist_ShouldReturnOkStatusAndUserId() throws Exception {
        // Arrange
        Map<String, Long> response = new HashMap<>();
        response.put("id", users.get(2).getId());

        // Act
        mockMvc.perform(get("/api/users/{id}/id", users.get(2).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getUserIdById_WhenUserNonExist_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/id", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserIdById_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/id", users.get(2).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getMeId_ShouldReturnOkStatusAndUserId() throws Exception {
        // Arrange
        Map<String, Long> response = new HashMap<>();
        response.put("id", users.get(0).getId());

        // Act
        mockMvc.perform(get("/api/users/me/id")
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getMeId_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/me/id"))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserNameById_WhenUserExist_ShouldReturnOkStatusAndUserName() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("name", users.get(2).getName());

        // Act
        mockMvc.perform(get("/api/users/{id}/name", users.get(2).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getUserNameById_WhenUserNonExist_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/name", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserNameById_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/name", users.get(2).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getMeName_ShouldReturnOkStatusAndUserId() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("name", users.get(0).getName());

        // Act
        mockMvc.perform(get("/api/users/me/name")
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getMeName_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/me/name"))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserEmailById_WhenUserExist_ShouldReturnOkStatusAndUserName() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("email", users.get(2).getEmail());

        // Act
        mockMvc.perform(get("/api/users/{id}/email", users.get(2).getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getUserEmailById_WhenUserNonExist_ShouldReturnNotFoundStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/email", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserEmailById_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/{id}/email", users.get(2).getId()))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void getMeEmail_ShouldReturnOkStatusAndUserId() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("email", users.get(0).getEmail());

        // Act
        mockMvc.perform(get("/api/users/me/email")
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getMeEmail_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/users/me/email"))
                // Assert
                .andExpect(status().isForbidden());
    }
}