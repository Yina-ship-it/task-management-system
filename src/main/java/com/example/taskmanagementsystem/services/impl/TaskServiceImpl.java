package com.example.taskmanagementsystem.services.impl;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@Service
@Primary
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

    @Override
    public List<TaskDto> findAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(taskDtoConverter::convertEntityToDto).toList();
    }

    @Override
    public TaskDto findTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task with id=" + id + " not found!"));
        return taskDtoConverter.convertEntityToDto(task);
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        validateTaskDto(taskDto);

        Set<User> assignees = getAssigneesFromDto(taskDto);

        Task task = taskDtoConverter.convertDtoToEntity(taskDto);
        task.setAssignees(assignees);

        return taskDtoConverter.convertEntityToDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateTask(TaskDto updatedTaskDto) {
        Task oldTask = taskRepository.findById(updatedTaskDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task with id=" + updatedTaskDto.getId() + " not found!"));

        validateTaskDto(updatedTaskDto);
        validateAuthor(oldTask, updatedTaskDto.getAuthor());

        Set<User> assignees = getAssigneesFromDto(updatedTaskDto);

        oldTask = taskDtoConverter.convertDtoToEntity(updatedTaskDto);
        oldTask.setAssignees(assignees);

        return taskDtoConverter.convertEntityToDto(taskRepository.save(oldTask));
    }

    @Override
    public void deleteTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id=" + id + " not found!"));
        validateAuthor(task, user);

        taskRepository.delete(task);
    }


    private void validateTaskDto(TaskDto taskDto) {
        if (taskDto.getTitle() == null) {
            throw new IllegalArgumentException("Invalid title=null");
        }
    }

    private void validateAuthor(Task oldTask, User user) {
        if (!oldTask.getAuthor().equals(user)) {
            throw new IllegalArgumentException("Only the author can update the task");
        }
    }

    private Set<User> getAssigneesFromDto(TaskDto taskDto) {
        return taskDto.getAssignees().stream()
                .map(assignee -> {
                    if (assignee.getId() != null) {
                        return userService.findById(assignee.getId());
                    }
                    if (assignee.getEmail() != null) {
                        return userService.findByEmail(assignee.getEmail());
                    }
                    throw new IllegalArgumentException("Task contains an invalid assignee! The assignee must have at least an id or email.");
                })
                .collect(Collectors.toSet());
    }
}
