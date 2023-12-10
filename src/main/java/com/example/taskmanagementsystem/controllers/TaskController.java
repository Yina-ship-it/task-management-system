package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskRequest;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
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
@Log
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

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id){
        try{
            TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(id));
            return ResponseEntity.ok(task);
        } catch (EntityNotFoundException e) {
            log.severe(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
