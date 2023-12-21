package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.comment.CommentDto;
import com.example.taskmanagementsystem.dto.comment.CommentDtoConverter;
import com.example.taskmanagementsystem.dto.comment.CommentResponse;
import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
        private CommentDtoConverter commentDtoConverter;

    @GetMapping("/id")
    public ResponseEntity<Map<String, Long>> getId(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("id", task.getId()));
    }

    @GetMapping("/title")
    public ResponseEntity<Map<String, String>> getTitle(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("title", task.getTitle()));
    }

    @PutMapping("/title")
    public ResponseEntity<TaskResponse> updateTitle(
            @PathVariable Long taskId,
            @RequestParam String title) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskTitleById(taskId, title, user));
        return ResponseEntity.ok(task);
    }

    @GetMapping("/description")
    public ResponseEntity<Map<String, String>> getDescription(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("description", task.getDescription()));
    }

    @PutMapping("/description")
    public ResponseEntity<TaskResponse> updateDescription(
            @PathVariable Long taskId,
            @RequestParam String description) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskDescriptionById(taskId, description, user));
        return ResponseEntity.ok(task);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, TaskStatus>> getStatus(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("status", task.getStatus()));
    }

    @PutMapping("/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @RequestParam(name = "status-value") Integer statusValue) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskStatusById(taskId, statusValue, user));
        return ResponseEntity.ok(task);
    }

    @GetMapping("/priority")
    public ResponseEntity<Map<String, TaskPriority>> getPriority(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("priority", task.getPriority()));
    }

    @PutMapping("/priority")
    public ResponseEntity<TaskResponse> updatePriority(
            @PathVariable Long taskId,
            @RequestParam(name = "priority-value") Integer priorityValue) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskPriorityById(taskId, priorityValue, user));
        return ResponseEntity.ok(task);
    }

    @GetMapping("/author")
    public ResponseEntity<Map<String, UserResponse>> getAuthor(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("author", task.getAuthor()));
    }

    @GetMapping("/assignees")
    public ResponseEntity<Map<String, List<UserResponse>>> getAssignees(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("assignees", task.getAssignees()));
    }

    @PostMapping("/assignees")
    public ResponseEntity<TaskResponse> addAssignee(@PathVariable Long taskId,
            @RequestParam(name = "assignee-id", required = false) Long assigneeId,
            @RequestParam(name = "assignee-email", required = false) String assigneeEmail) {
        User user = getUserOutOfContext();
        if (assigneeId != null) {
            TaskResponse task = taskDtoConverter.convertDtoToResponse(
                    taskService.appendAssigneeByIdInTask(taskId, assigneeId, user));
            return ResponseEntity.ok(task);
        }
        else if (assigneeEmail != null) {
            TaskResponse task = taskDtoConverter.convertDtoToResponse(
                    taskService.appendAssigneeByEmailInTask(taskId, assigneeEmail, user));
            return ResponseEntity.ok(task);
        }
        else
            return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/assignees")
    public ResponseEntity<TaskResponse> deleteAssignee(@PathVariable Long taskId,
            @RequestParam(name = "assignee-id", required = false) Long assigneeId,
            @RequestParam(name = "assignee-email", required = false) String assigneeEmail) {
        User user = getUserOutOfContext();
        if (assigneeId != null) {
            TaskResponse task = taskDtoConverter.convertDtoToResponse(
                    taskService.removeAssigneeByIdInTask(taskId, assigneeId, user));
            return ResponseEntity.ok(task);
        }
        else if (assigneeEmail != null) {
            TaskResponse task = taskDtoConverter.convertDtoToResponse(
                    taskService.removeAssigneeByEmailInTask(taskId, assigneeEmail, user));
            return ResponseEntity.ok(task);
        }
        else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping("/comments")
    public ResponseEntity<Map<String, List<CommentResponse>>> getComments(@PathVariable Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("comments", task.getComments()));
    }

    @PostMapping("/comments")
    public ResponseEntity<TaskResponse> addComment(@PathVariable Long taskId,
                                                    @RequestParam(name = "comment-text") String commentText) {
        CommentDto commentDto = commentDtoConverter.convertRequestToDto(commentText);
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.appendCommentInTask(taskId, commentDto, user));
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<TaskResponse> deleteComment(@PathVariable Long taskId,
                                                   @RequestParam(name = "comment-id") Long commentId) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.removeCommentByIdInTask(taskId, commentId, user));
        return ResponseEntity.ok(task);
    }

    private <T> Map<String, T> getResponse(String key, T value) {
        Map<String, T> response = new HashMap<>();
        response.put(key, value);
        return response;
    }

    private User getUserOutOfContext() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }
}
