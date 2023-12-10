package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.profile.UserResponse;
import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Yina-ship-it
 * @since 10.12.2023
 */
@RestController
@RequestMapping("/api/tasks/{taskId}")
@Log
public class TaskFieldController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

    @GetMapping("/title")
    public ResponseEntity<Map<String, String>> getTitle(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "title", TaskResponse::getTitle);
    }

    @GetMapping("/description")
    public ResponseEntity<Map<String, String>> getDescription(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "description", TaskResponse::getDescription);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, TaskStatus>> getStatus(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "status", TaskResponse::getStatus);
    }

    @GetMapping("/priority")
    public ResponseEntity<Map<String, TaskPriority>> getPriority(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "priority", TaskResponse::getPriority);
    }

    @GetMapping("/author")
    public ResponseEntity<Map<String, UserResponse>> getAuthor(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "author", TaskResponse::getAuthor);
    }

    @GetMapping("/assignees")
    public ResponseEntity<Map<String, List<UserResponse>>> getAssignees(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "assignees", TaskResponse::getAssignees);
    }

    private <T> ResponseEntity<Map<String, T>> handleFieldRequest(
            Long taskId,
            String key,
            Function<TaskResponse, T> valueExtractor) {
        try {
            TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
            return ResponseEntity.ok(getResponse(key, valueExtractor.apply(task)));
        } catch (EntityNotFoundException e) {
            log.severe(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private <T> Map<String, T> getResponse(String key, T value) {
        Map<String, T> response = new HashMap<>();
        response.put(key, value);
        return response;
    }
}
