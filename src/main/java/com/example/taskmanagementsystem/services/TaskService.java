package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.task.TaskDto;

import java.util.List;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
public interface TaskService {
    List<TaskDto> findAllTasks();
    TaskDto findTaskById(Long id);
    TaskDto createTaskDto(TaskDto taskDto);
    TaskDto updateTaskDto(TaskDto updatedTaskDto);
    void deleteTaskDtoById(Long id);
}
