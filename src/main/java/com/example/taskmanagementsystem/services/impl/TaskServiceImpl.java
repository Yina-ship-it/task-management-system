package com.example.taskmanagementsystem.services.impl;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

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
        return null;
    }

    @Override
    public TaskDto updateTaskDto(TaskDto updatedTaskDto) {
        return null;
    }

    @Override
    public void deleteTaskDtoById(Long id) {

    }
}
