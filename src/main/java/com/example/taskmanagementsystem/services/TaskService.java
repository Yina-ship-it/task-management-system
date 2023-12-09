package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.models.User;

import java.util.List;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
public interface TaskService {
    List<TaskDto> findAllTasks();
    TaskDto findTaskById(Long id);
    TaskDto createTask(TaskDto taskDto);
    TaskDto updateTask(TaskDto updatedTaskDto);
    void deleteTaskById(Long id, User user);
}
