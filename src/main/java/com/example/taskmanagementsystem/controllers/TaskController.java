package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskRequest;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yina-ship-it
 * @since 10.12.2023
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

    @GetMapping("/")
    public ResponseEntity<List<TaskResponse>> getAllTasks(){
        try{
            List<TaskResponse> tasks = taskService.findAllTasks().stream()
                    .map(taskDtoConverter::convertDtoToResponse).toList();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
