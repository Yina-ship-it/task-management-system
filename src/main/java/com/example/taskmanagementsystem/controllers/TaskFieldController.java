package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    private UserService userService;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

    @GetMapping("/id")
    public ResponseEntity<Map<String, Long>> getId(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "id", TaskResponse::getId);
    }

    @GetMapping("/title")
    public ResponseEntity<Map<String, String>> getTitle(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "title", TaskResponse::getTitle);
    }

    @PutMapping("/title")
    public ResponseEntity<TaskResponse> updateTitle(
            @PathVariable Long taskId,
            @RequestParam String title) {
        return handleUpdateTaskField(taskId, title, (id, val, user) -> taskService.updateTaskTitleById(id, val, user));
    }

    @GetMapping("/description")
    public ResponseEntity<Map<String, String>> getDescription(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "description", TaskResponse::getDescription);
    }

    @PutMapping("/description")
    public ResponseEntity<TaskResponse> updateDescription(
            @PathVariable Long taskId,
            @RequestParam String description) {
        return handleUpdateTaskField(taskId, description, (id, val, user) -> taskService.updateTaskDescriptionById(id, val, user));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, TaskStatus>> getStatus(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "status", TaskResponse::getStatus);
    }

    @PutMapping("/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @RequestParam(name = "status-value") Integer statusValue) {
        return handleUpdateTaskField(taskId, statusValue, (id, val, user) -> taskService.updateTaskStatusById(id, val, user));
    }

    @GetMapping("/priority")
    public ResponseEntity<Map<String, TaskPriority>> getPriority(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "priority", TaskResponse::getPriority);
    }

    @PutMapping("/priority")
    public ResponseEntity<TaskResponse> updatePriority(
            @PathVariable Long taskId,
            @RequestParam(name = "priority-value") Integer priorityValue) {
        return handleUpdateTaskField(taskId, priorityValue, (id, val, user) -> taskService.updateTaskPriorityById(id, val, user));
    }

    @GetMapping("/author")
    public ResponseEntity<Map<String, UserResponse>> getAuthor(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "author", TaskResponse::getAuthor);
    }

    @GetMapping("/assignees")
    public ResponseEntity<Map<String, List<UserResponse>>> getAssignees(@PathVariable Long taskId) {
        return handleFieldRequest(taskId, "assignees", TaskResponse::getAssignees);
    }

    @PostMapping("/assignees")
    public ResponseEntity<TaskResponse> addAssignee(@PathVariable Long taskId,
            @RequestParam(name = "assignee-id", required = false) Long assigneeId,
            @RequestParam(name = "assignee-email", required = false) String assigneeEmail) {
        if (assigneeId != null)
            return handleUpdateTaskField(taskId, assigneeId,
                    (id, val, user) -> taskService.appendAssigneeByIdInTask(id, val, user));
        else if (assigneeEmail != null)
            return handleUpdateTaskField(taskId, assigneeEmail,
                    (id, val, user) -> taskService.appendAssigneeByEmailInTask(id, val, user));
        else
            return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/assignees")
    public ResponseEntity<TaskResponse> deleteAssignee(@PathVariable Long taskId,
            @RequestParam(name = "assignee-id", required = false) Long assigneeId,
            @RequestParam(name = "assignee-email", required = false) String assigneeEmail) {
        if (assigneeId != null)
            return handleUpdateTaskField(taskId, assigneeId,
                    (id, val, user) -> taskService.removeAssigneeByIdInTask(id, val, user));
        else if (assigneeEmail != null)
            return handleUpdateTaskField(taskId, assigneeEmail,
                    (id, val, user) -> taskService.removeAssigneeByEmailInTask(id, val, user));
        else
            return ResponseEntity.badRequest().build();
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

    private <T> ResponseEntity<TaskResponse> handleUpdateTaskField(
            Long taskId,
            T value,
            FunctionWithThreeArguments<Long, T, User, TaskDto> updateFunction) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            TaskResponse task = taskDtoConverter.convertDtoToResponse(
                    updateFunction.apply(taskId, value, user));
            return ResponseEntity.ok(task);
        } catch (EntityNotFoundException e) {
            log.severe(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.severe(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @FunctionalInterface
    interface FunctionWithThreeArguments<T, U, V, R> {
        R apply(T t, U u, V v);
    }
}
