package com.example.taskmanagementsystem.controllers;

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

import java.util.List;
import java.util.Optional;
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

    private String token;
    private List<User> users;

    @BeforeEach
    void setUp() {
        String password = passwordEncoder.encode("Password");
        this.users = userRepository.saveAll(List.of(
                User.builder().name("maksim1").email("maksim1@mail.test").password(password).build(),
                User.builder().name("maksim2").email("maksim2@mail.test").password(password).build(),
                User.builder().name("maksim3").email("maksim3@mail.test").password(password).build()
        ));
        this.token = jwtProvider.generateToken(users.get(0).getEmail());
    }

    @Test
    void getAllTasks_ShouldReturnListOfTaskResponses() throws Exception {
        // Arrange
        List<Task> tasks = taskRepository.saveAll(List.of(
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
        List<TaskResponse> taskResponses = tasks.stream()
                .map(taskDtoConverter::convertEntityToDto)
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();

        // Act
        mockMvc.perform(get("/api/tasks/")
                .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(taskResponses)));

    }

    @Test
    void getAllTasks_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        mockMvc.perform(get("/api/tasks/"))
                // Assert
                .andExpect(status().isForbidden());

    }

    @Test
    void getTaskById_WhenTaskFound_ShouldTaskResponse() throws Exception {
        // Arrange
        Task task = taskRepository.save(Task.builder()
                        .title("TestTask1")
                        .description("task 1")
                        .priority(TaskPriority.MEDIUM)
                        .status(TaskStatus.IN_PROGRESS)
                        .author(users.get(0))
                        .assignees(Set.of(users.get(1), users.get(2)))
                        .build());
        TaskResponse taskResponse = taskDtoConverter.convertDtoToResponse(taskDtoConverter.convertEntityToDto(task));

        // Act
        mockMvc.perform(get("/api/tasks/{id}", task.getId())
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(taskResponse)));

    }

    @Test
    void getTaskById_WhenTaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Arrange
        long id = Long.MAX_VALUE;

        // Act
        mockMvc.perform(get("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + token))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskById_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        long id = Long.MAX_VALUE;

        mockMvc.perform(get("/api/tasks/{id}", id))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void addTask_WhenTaskRequestWithValidData_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals(taskRequest.getDescription(), createdTask.get().getDescription());
        assertEquals(taskRequest.getStatusValue(), createdTask.get().getStatus().getValue());
        assertEquals(taskRequest.getPriorityValue(), createdTask.get().getPriority().getValue());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertTrue(createdTask.get().getAssignees().contains(users.get(1)));
        assertTrue(createdTask.get().getAssignees().contains(users.get(2)));
        assertEquals(2, createdTask.get().getAssignees().size());
    }

    @Test
    void addTask_UnauthorisedRequest_ShouldReturnForbiddenStatus() throws Exception {
        // Act
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        mockMvc.perform(post("/api/tasks/")
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isForbidden());
    }

    @Test
    void addTask_WhenTaskRequestWithoutTitle_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTask_WhenTaskRequestWithBlankTitle_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTask_WhenTaskRequestWithoutDescription_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals("", createdTask.get().getDescription());
        assertEquals(taskRequest.getStatusValue(), createdTask.get().getStatus().getValue());
        assertEquals(taskRequest.getPriorityValue(), createdTask.get().getPriority().getValue());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertTrue(createdTask.get().getAssignees().contains(users.get(1)));
        assertTrue(createdTask.get().getAssignees().contains(users.get(2)));
        assertEquals(2, createdTask.get().getAssignees().size());
    }

    @Test
    void addTask_WhenTaskRequestWithInvalidStatus_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(5)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTask_WhenTaskRequestWithoutStatus_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals(taskRequest.getDescription(), createdTask.get().getDescription());
        assertEquals(TaskStatus.PENDING, createdTask.get().getStatus());
        assertEquals(taskRequest.getPriorityValue(), createdTask.get().getPriority().getValue());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertTrue(createdTask.get().getAssignees().contains(users.get(1)));
        assertTrue(createdTask.get().getAssignees().contains(users.get(2)));
        assertEquals(2, createdTask.get().getAssignees().size());
    }

    @Test
    void addTask_WhenTaskRequestWithInvalidPriority_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(5)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTask_WhenTaskRequestWithoutPriority_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals(taskRequest.getDescription(), createdTask.get().getDescription());
        assertEquals(taskRequest.getStatusValue(), createdTask.get().getStatus().getValue());
        assertEquals(TaskPriority.LOW, createdTask.get().getPriority());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertTrue(createdTask.get().getAssignees().contains(users.get(1)));
        assertTrue(createdTask.get().getAssignees().contains(users.get(2)));
        assertEquals(2, createdTask.get().getAssignees().size());
    }

    @Test
    void addTask_WhenTaskRequestWithoutAssigneesEmail_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesId(List.of(users.get(1).getId(), users.get(2).getId()))
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals(taskRequest.getDescription(), createdTask.get().getDescription());
        assertEquals(taskRequest.getStatusValue(), createdTask.get().getStatus().getValue());
        assertEquals(taskRequest.getPriorityValue(), createdTask.get().getPriority().getValue());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertTrue(createdTask.get().getAssignees().contains(users.get(1)));
        assertTrue(createdTask.get().getAssignees().contains(users.get(2)));
        assertEquals(2, createdTask.get().getAssignees().size());
    }

    @Test
    void addTask_WhenTaskRequestWithoutAssigneesId_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail(), users.get(2).getEmail()))
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals(taskRequest.getDescription(), createdTask.get().getDescription());
        assertEquals(taskRequest.getStatusValue(), createdTask.get().getStatus().getValue());
        assertEquals(taskRequest.getPriorityValue(), createdTask.get().getPriority().getValue());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertTrue(createdTask.get().getAssignees().contains(users.get(1)));
        assertTrue(createdTask.get().getAssignees().contains(users.get(2)));
        assertEquals(2, createdTask.get().getAssignees().size());
    }

    @Test
    void addTask_WhenSameUsersSpecifiedInAssigneesIdAndAssigneesEmail_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesId(List.of(users.get(1).getId(), users.get(2).getId()))
                .assigneesEmail(List.of(users.get(1).getEmail(), users.get(2).getEmail()))
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals(taskRequest.getDescription(), createdTask.get().getDescription());
        assertEquals(taskRequest.getStatusValue(), createdTask.get().getStatus().getValue());
        assertEquals(taskRequest.getPriorityValue(), createdTask.get().getPriority().getValue());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertTrue(createdTask.get().getAssignees().contains(users.get(1)));
        assertTrue(createdTask.get().getAssignees().contains(users.get(2)));
        assertEquals(2, createdTask.get().getAssignees().size());
    }

    @Test
    void addTask_WhenAssigneesIdContainsNonExistentUser_ShouldReturnNotFoundStatus() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail()))
                .assigneesId(List.of(users.get(2).getId(), Long.MAX_VALUE))
                .build();

        // Act
        mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void addTask_WhenAssigneesEmailContainsNonExistentUser_ShouldReturnNotFoundStatus() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .assigneesEmail(List.of(users.get(1).getEmail(), "TEst NonExisten USer Email"))
                .assigneesId(List.of(users.get(2).getId()))
                .build();

        // Act
        mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void addTask_WhenTaskRequestWithoutAssignees_ShouldReturnCreatedStatusAndTaskResponse() throws Exception {
        // Arrange
        TaskRequest taskRequest = TaskRequest.builder()
                .title("TestTask")
                .description("testing task")
                .statusValue(1)
                .priorityValue(2)
                .build();

        // Act
        String responseContent  = mockMvc.perform(post("/api/tasks/")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Assert
        TaskResponse response = objectMapper.readValue(responseContent, TaskResponse.class);

        Optional<Task> createdTask = taskRepository.findById(response.getId());

        assertTrue(createdTask.isPresent());
        assertEquals(taskRequest.getTitle(), createdTask.get().getTitle());
        assertEquals(taskRequest.getDescription(), createdTask.get().getDescription());
        assertEquals(taskRequest.getStatusValue(), createdTask.get().getStatus().getValue());
        assertEquals(taskRequest.getPriorityValue(), createdTask.get().getPriority().getValue());
        assertEquals(users.get(0), createdTask.get().getAuthor());
        assertEquals(0, createdTask.get().getAssignees().size());
    }

}