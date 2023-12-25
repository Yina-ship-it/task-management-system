package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.comment.CommentDto;
import com.example.taskmanagementsystem.dto.comment.CommentDtoConverter;
import com.example.taskmanagementsystem.dto.comment.CommentResponse;
import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Task Api")
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

    @Operation(summary = "Getting task id.", description = "Allows to get task id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/idSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/id")
    public ResponseEntity<Map<String, Long>> getId(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("id", task.getId()));
    }

    @Operation(summary = "Getting task title.", description = "Allows to get task title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/titleSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/title")
    public ResponseEntity<Map<String, String>> getTitle(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("title", task.getTitle()));
    }

    @Operation(summary = "Updating task title.", description = "Allows to update task title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PutMapping("/title")
    public ResponseEntity<TaskResponse> updateTitle(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam @Parameter(description = "Task title.\nMaximum length 255.") String title) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskTitleById(taskId, title, user));
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Getting task description.", description = "Allows to get task description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/descriptionSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/description")
    public ResponseEntity<Map<String, String>> getDescription(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("description", task.getDescription()));
    }

    @Operation(summary = "Updating task description.", description = "Allows to update task description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PutMapping("/description")
    public ResponseEntity<TaskResponse> updateDescription(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam @Parameter(description = "Task description.") String description) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskDescriptionById(taskId, description, user));
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Getting task status.", description = "Allows to get task status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/statusSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/status")
    public ResponseEntity<Map<String, TaskStatus>> getStatus(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskDto task = taskService.findTaskById(taskId);
        return ResponseEntity.ok(getResponse("status", task.getStatus()));
    }

    @Operation(summary = "Updating task status.", description = "Allows to update task status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PutMapping("/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam(name = "status-value")
            @Parameter(description = "Task status value.\n\nMinimum value 1.\n\nMaximum value 3.") Integer statusValue) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskStatusById(taskId, statusValue, user));
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Getting task priority.", description = "Allows to get task priority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/prioritySchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/priority")
    public ResponseEntity<Map<String, TaskPriority>> getPriority(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskDto task = taskService.findTaskById(taskId);
        return ResponseEntity.ok(getResponse("priority", task.getPriority()));
    }

    @Operation(summary = "Updating task priority.", description = "Allows to update task priority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PutMapping("/priority")
    public ResponseEntity<TaskResponse> updatePriority(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam(name = "priority-value")
            @Parameter(description = "Task priority value.\n\nMinimum value 1.\n\nMaximum value 3.") Integer priorityValue) {
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.updateTaskPriorityById(taskId, priorityValue, user));
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Getting task author.", description = "Allows to get task author.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/authorSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/author")
    public ResponseEntity<Map<String, UserResponse>> getAuthor(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("author", task.getAuthor()));
    }

    @Operation(summary = "Getting task assignees.", description = "Allows to get task assignees.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/assigneesSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/assignees")
    public ResponseEntity<Map<String, List<UserResponse>>> getAssignees(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("assignees", task.getAssignees()));
    }

    @Operation(summary = "Adding task assignee.", description = "Allows to add task assignee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PostMapping("/assignees")
    public ResponseEntity<TaskResponse> addAssignee(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam(name = "assignee-id", required = false)
            @Parameter(description = "Task assignee identifier.") Long assigneeId,
            @RequestParam(name = "assignee-email", required = false)
            @Parameter(description = "Task assignee email.\n\nMaximum length 255.") String assigneeEmail) {
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

    @Operation(summary = "Deleting task assignee.", description = "Allows to delete task assignee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @DeleteMapping("/assignees")
    public ResponseEntity<TaskResponse> deleteAssignee(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam(name = "assignee-id", required = false)
            @Parameter(description = "Task assignee identifier.") Long assigneeId,
            @RequestParam(name = "assignee-email", required = false)
            @Parameter(description = "Task assignee email.\n\nMaximum length 255.") String assigneeEmail) {
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

    @Operation(summary = "Getting task comments.", description = "Allows to get task comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/commentsSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/comments")
    public ResponseEntity<Map<String, List<CommentResponse>>> getComments(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId) {
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(taskId));
        return ResponseEntity.ok(getResponse("comments", task.getComments()));
    }

    @Operation(summary = "Adding task comment.", description = "Allows to add task comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PostMapping("/comments")
    public ResponseEntity<TaskResponse> addComment(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam(name = "comment-text")
            @Parameter(description = "Task comment text.") String commentText) {
        CommentDto commentDto = commentDtoConverter.convertRequestToDto(commentText);
        User user = getUserOutOfContext();
        TaskResponse task = taskDtoConverter.convertDtoToResponse(
                taskService.appendCommentInTask(taskId, commentDto, user));
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Deleting task comment.", description = "Allows to delete task comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @DeleteMapping("/comments")
    public ResponseEntity<TaskResponse> deleteComment(
            @PathVariable @Parameter(description = "Task identifier.") Long taskId,
            @RequestParam(name = "comment-id")
            @Parameter(description = "Task comment identifier.") Long commentId) {
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
