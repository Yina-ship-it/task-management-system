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
    public TaskDto createTaskDto(TaskDto taskDto) {
        if (taskDto.getTitle() == null)
            throw new IllegalArgumentException("Invalid title=null");
        Set<User> assignees = taskDto.getAssignees().stream()
                .map(assignee -> {
                    if(assignee.getId() != null)
                        return userService.findById(assignee.getId());
                    if(assignee.getEmail() != null)
                        return  userService.findByEmail(assignee.getEmail());
                    throw new IllegalArgumentException("Task contains an invalid assignee! The assignee must have at least an id or email.");
                })
                .collect(Collectors.toSet());
        Task task = taskDtoConverter.convertDtoToEntity(taskDto);
        task.setAssignees(assignees);
        return taskDtoConverter.convertEntityToDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateTaskDto(TaskDto updatedTaskDto) {
        return null;
    }

    @Override
    public void deleteTaskDtoById(Long id) {

    }
}
