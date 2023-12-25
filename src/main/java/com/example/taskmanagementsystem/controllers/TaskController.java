package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskRequest;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.security.dto.AuthResponse;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Yina-ship-it
 * @since 10.12.2023
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Api")
@Log
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

    @Operation(summary = "Getting all the tasks.", description = "Allows to get all the tasks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(ref = "#/components/schemas/taskResponseSchema")))),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<TaskResponse>> getAllTasks(){
        List<TaskResponse> tasks = taskService.findAllTasks().stream()
                .map(taskDtoConverter::convertDtoToResponse).toList();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Getting task by Id.", description = "Allows to get task by Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable @Parameter(description = "Task identifier.") Long id){
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.findTaskById(id));
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Creating task.", description = "Allows to create task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creating",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/taskResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest taskRequest) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        TaskDto taskDto = taskDtoConverter.convertRequestToDto(taskRequest);
        taskDto.setAuthor(user);
        TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.createTask(taskDto));
        URI location = new URI("/api/tasks/" + task.getId());
        return ResponseEntity.created(location).body(task);
    }

    @Operation(summary = "Deleting task by Id.", description = "Allows to delete task by Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable @Parameter(description = "Task identifier.") Long id){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        taskService.deleteTaskById(id, user);
        return ResponseEntity.ok().build();
    }
}
