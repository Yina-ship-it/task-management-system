package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.dto.user.UserResponseConverter;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
 * @since 12.12.2023
 */

@Log
@RestController
@Tag(name = "User Api")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    TaskDtoConverter taskDtoConverter;

    @Autowired
    UserResponseConverter userResponseConverter;

    @Operation(summary = "Getting all users.", description = "Allows to get all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<UserResponse> users = userService.findAllUsers().stream()
                .map(userResponseConverter::convertUserToResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Getting user by Id.", description = "Allows to get user by Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @Parameter(description = "User identifier.") Long id){
        User user = userService.findById(id);
        UserResponse userResponse = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Getting current user.", description = "Allows to get current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(){
        User user = getUserOutOfContext();
        UserResponse userResponse = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Getting user name.", description = "Allows to get user name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/nameSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/{id}/name")
    public ResponseEntity<Map<String, String>> getUserNameById(
            @PathVariable @Parameter(description = "User identifier.") Long id){
        User user = userService.findById(id);
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("name", response.getName()));
    }

    @Operation(summary = "Getting current user name.", description = "Allows to get current user name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/nameSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/me/name")
    public ResponseEntity<Map<String, String>> getMeName(){
        User user = getUserOutOfContext();
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("name", response.getName()));
    }

    @Operation(summary = "Getting user email.", description = "Allows to get user email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/emailSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> getUserEmailById(
            @PathVariable @Parameter(description = "User identifier.") Long id){
        User user = userService.findById(id);
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("email", response.getEmail()));
    }

    @Operation(summary = "Getting current user email.", description = "Allows to get current user email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/emailSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/me/email")
    public ResponseEntity<Map<String, String>> getMeEmail(){
        User user = getUserOutOfContext();
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("email", response.getEmail()));
    }

    @Operation(summary = "Getting user id.", description = "Allows to get user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/idSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/{id}/id")
    public ResponseEntity<Map<String, Long>> getUserIdById(
            @PathVariable @Parameter(description = "User identifier.") Long id){
        User user = userService.findById(id);
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("id", response.getId()));
    }

    @Operation(summary = "Getting current user id.", description = "Allows to get current user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/idSchema"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/me/id")
    public ResponseEntity<Map<String, Long>> getMeId(){
        User user = getUserOutOfContext();
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("id", response.getId()));
    }

    @Operation(summary = "Getting all tasks created by the user.",
            description = "Allows to get all tasks created by the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(ref = "#/components/schemas/taskResponseSchema")))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/{id}/created-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByUser(
            @PathVariable @Parameter(description = "User identifier.") Long id){
        User user = userService.findById(id);
        List<TaskResponse> tasks = taskService.findAllTasksByAuthor(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Getting all tasks assigned by the user.",
            description = "Allows to get all tasks assigned by the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(ref = "#/components/schemas/taskResponseSchema")))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/{id}/assigned-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksAssignedToUser(
            @PathVariable @Parameter(description = "User identifier.") Long id){
        User user = userService.findById(id);
        List<TaskResponse> tasks = taskService.findAllTasksByAssignee(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Getting all tasks created by the current user.",
            description = "Allows to get all tasks created by the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(ref = "#/components/schemas/taskResponseSchema")))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/me/created-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByMe(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        List<TaskResponse> tasks = taskService.findAllTasksByAuthor(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Getting all tasks assigned by the current user.",
            description = "Allows to get all tasks assigned by the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(ref = "#/components/schemas/taskResponseSchema")))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @GetMapping("/me/assigned-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksAssignedToMe(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        List<TaskResponse> tasks = taskService.findAllTasksByAssignee(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Updating current user name.", description = "Allows to update current user name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PutMapping("/me/name")
    public ResponseEntity<UserResponse> updateName(
            @RequestParam(name = "name")
            @Parameter(description = "New name of the current user.\n\nMaximum length 255.") String name){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.updateUserNameByEmail(userDetails.getUsername(), name);
        return ResponseEntity.ok(userResponseConverter.convertUserToResponse(user));
    }

    @Operation(summary = "Updating current user email.", description = "Allows to update current user email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PutMapping("/me/email")
    public ResponseEntity<UserResponse> updateEmail(
            @RequestParam(name = "email")
            @Parameter(description = "New email of the current user.\n\nMaximum length 255.") String email){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.updateUserEmailByEmail(userDetails.getUsername(), email);
        return ResponseEntity.ok(userResponseConverter.convertUserToResponse(user));
    }

    @Operation(summary = "Updating current user password.", description = "Allows to update current user password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
    })
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @RequestParam(name = "password")
            @Parameter(description = "New password of the current user.\n\nMaximum length 255.") String password){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.updateUserPasswordByEmail(userDetails.getUsername(), password);
        return ResponseEntity.ok(userResponseConverter.convertUserToResponse(user));
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
