package com.example.taskmanagementsystem.services.impl;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskDtoConverter taskDtoConverter;

    @Mock
    UserService userService;

    @InjectMocks
    TaskServiceImpl taskService;

    @Test
    void findAllTasks_WhenTasksExist_ShouldReturnListOfTaskDto() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();
        User user2 = User.builder().id(2L).name("maksim2").email("maksim2@mail.test").password("****").build();
        User user3 = User.builder().id(3L).name("maksim2").email("maksim3@mail.test").password("****").build();

        Task task1 = Task.builder()
                .id(1L)
                .title("TestTask1")
                .description("task 1")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(new ArrayList<>(List.of(user2, user3)))
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("TestTask2")
                .description("task 2")
                .priority(TaskPriority.LOW)
                .status(TaskStatus.COMPLETED)
                .author(user2)
                .assignees(new ArrayList<>(List.of(user1, user3)))
                .build();

        List<Task> tasks = List.of(task1, task2);

        TaskDto taskDto1 = TaskDto.builder()
                .id(1L)
                .title("TestTask1")
                .description("task 1")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(List.of(user2, user3))
                .build();

        TaskDto taskDto2 = TaskDto.builder()
                .id(2L)
                .title("TestTask2")
                .description("task 2")
                .priority(TaskPriority.LOW)
                .status(TaskStatus.COMPLETED)
                .author(user2)
                .assignees(List.of(user1, user3))
                .build();

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskDtoConverter.convertEntityToDto(task1)).thenReturn(taskDto1);
        when(taskDtoConverter.convertEntityToDto(task2)).thenReturn(taskDto2);

        // Act
        List<TaskDto> result = taskService.findAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(tasks.size(), result.size());
        assertEquals(task1.getId(), result.get(0).getId());
        assertEquals(task2.getId(), result.get(1).getId());

        verify(taskRepository, times(1)).findAll();
        verify(taskDtoConverter, times(2)).convertEntityToDto(any(Task.class));
    }

    @Test
    void findAllTasks_WhenTasksNonExist_ShouldReturnListOfTaskDto() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(List.of());

        // Act
        List<TaskDto> result = taskService.findAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(taskRepository, times(1)).findAll();
        verify(taskDtoConverter, times(0)).convertEntityToDto(any(Task.class));
    }

    @Test
    void findTaskById_WhenTaskExist_ShouldReturnTaskDto() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();
        User user2 = User.builder().id(2L).name("maksim2").email("maksim2@mail.test").password("****").build();
        User user3 = User.builder().id(3L).name("maksim2").email("maksim3@mail.test").password("****").build();

        long id = 1L;

        Task task = Task.builder()
                .id(id)
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(new ArrayList<>(List.of(user2, user3)))
                .build();

        TaskDto taskDto = TaskDto.builder()
                .id(id)
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(List.of(user2, user3))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskDtoConverter.convertEntityToDto(task)).thenReturn(taskDto);

        // Act
        TaskDto result = taskService.findTaskById(id);

        // Assert
        assertNotNull(result);
        assertEquals(taskDto, result);

        verify(taskRepository, times(1)).findById(id);
        verify(taskDtoConverter, times(1)).convertEntityToDto(any(Task.class));
    }

    @Test
    void findTaskById_WhenTaskNonExist_ShouldThrowException() {
        // Arrange
        long id = 1L;

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act && Assert
        assertThrows(EntityNotFoundException.class,
                () -> taskService.findTaskById(id)
        );

        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void createTask_WhenTaskDTOWithValidData_ShouldCreateTaskAndReturnTaskDto() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();
        User user2 = User.builder().id(2L).name("maksim2").email("maksim2@mail.test").password("****").build();
        User user3 = User.builder().id(3L).name("maksim2").email("maksim3@mail.test").password("****").build();

        Task task = Task.builder()
                .id(1L)
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(new ArrayList<>(List.of(user2, user3)))
                .build();

        TaskDto taskDto = TaskDto.builder()
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(List.of(
                        User.builder().id(user2.getId()).build(),
                        User.builder().email(user3.getEmail()).build())
                )
                .build();

        TaskDto resultTaskDto = TaskDto.builder()
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(List.of(user2,user3)
                )
                .build();

        when(userService.findById(user2.getId())).thenReturn(user2);
        when(userService.findByEmail(user3.getEmail())).thenReturn(user3);
        when(taskDtoConverter.convertDtoToEntity(taskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskDtoConverter.convertEntityToDto(task)).thenReturn(resultTaskDto);
        //Assert
        TaskDto result = taskService.createTask(taskDto);

        // Assert
        assertNotNull(result);
        assertEquals(resultTaskDto, result);

        verify(taskDtoConverter, times(1)).convertDtoToEntity(any(TaskDto.class));
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskDtoConverter, times(1)).convertEntityToDto(any(Task.class));
    }

    @Test
    void createTask_WhenTaskDTOWithoutTitle_ShouldThrowException() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();
        User user2 = User.builder().id(2L).name("maksim2").email("maksim2@mail.test").password("****").build();
        User user3 = User.builder().id(3L).name("maksim2").email("maksim3@mail.test").password("****").build();

        TaskDto taskDto = TaskDto.builder()
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(List.of(
                        User.builder().id(user2.getId()).build(),
                        User.builder().email(user3.getEmail()).build())
                )
                .build();

        // Act && Assert
        assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(taskDto)
        );
    }

    @Test
    void createTask_WhenTaskDTOWithEmptySetAssignees_ShouldCreateTaskWithoutAssigneesAndReturnTaskDto() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();

        Task task = Task.builder()
                .id(1L)
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(new ArrayList<>())
                .build();

        TaskDto taskDto = TaskDto.builder()
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .assignees(List.of())
                .author(user1)
                .build();

        TaskDto resultTaskDto = TaskDto.builder()
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(List.of())
                .build();

        when(taskDtoConverter.convertDtoToEntity(taskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskDtoConverter.convertEntityToDto(task)).thenReturn(resultTaskDto);

        //Assert
        TaskDto result = taskService.createTask(taskDto);

        // Assert
        assertNotNull(result);
        assertEquals(resultTaskDto, result);
        assertEquals(0, result.getAssignees().size());

        verify(taskDtoConverter, times(1)).convertDtoToEntity(any(TaskDto.class));
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskDtoConverter, times(1)).convertEntityToDto(any(Task.class));
    }

    @Test
    void createTask_WhenTaskDTOWithEmptyAssignee_ShouldThrowException() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();
        User user3 = User.builder().id(3L).name("maksim2").email("maksim3@mail.test").password("****").build();

        TaskDto taskDto = TaskDto.builder()
                .title("NewTestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(List.of(
                        User.builder().build(),
                        User.builder().email(user3.getEmail()).build())
                )
                .build();

        // Act && Assert
        assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(taskDto)
        );
    }

    @Test
    void deleteTaskById_WhenTaskExist_ShouldReturnTaskDto() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();
        User user2 = User.builder().id(2L).name("maksim2").email("maksim2@mail.test").password("****").build();
        User user3 = User.builder().id(3L).name("maksim2").email("maksim3@mail.test").password("****").build();

        long id = 1L;

        Task task = Task.builder()
                .id(id)
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user1)
                .assignees(new ArrayList<>(List.of(user2, user3)))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // Act
        taskService.deleteTaskById(id, user1);

        // Assert

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).delete(any(Task.class));
    }

    @Test
    void deleteTaskById_WhenTaskNonExist_ShouldThrowException() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();

        long id = 1L;

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act && Assert
        assertThrows(EntityNotFoundException.class,
                () -> taskService.deleteTaskById(id, user1)
        );

        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void deleteTaskById_WhenAnotherAuthor_ShouldThrowException() {
        // Arrange
        User user1 = User.builder().id(1L).name("maksim1").email("maksim1@mail.test").password("****").build();
        User user2 = User.builder().id(2L).name("maksim2").email("maksim2@mail.test").password("****").build();
        User user3 = User.builder().id(3L).name("maksim2").email("maksim3@mail.test").password("****").build();

        long id = 1L;

        Task task = Task.builder()
                .id(id)
                .title("TestTask")
                .description("task")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .author(user2)
                .assignees(new ArrayList<>(List.of(user2, user3)))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // Act && Assert
        assertThrows(IllegalArgumentException.class,
                () -> taskService.deleteTaskById(id,user1)
        );

        verify(taskRepository, times(1)).findById(id);
    }
}