package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.dto.user.UserResponseConverter;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
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
 * @since 12.12.2023
 */

@Log
@RestController
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

    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<UserResponse> users = userService.findAllUsers().stream()
                .map(userResponseConverter::convertUserToResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        User user = userService.findById(id);
        UserResponse userResponse = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(){
        User user = getUserOutOfContext();
        UserResponse userResponse = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<Map<String, String>> getUserNameById(@PathVariable Long id){
        User user = userService.findById(id);
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("name", response.getName()));
    }

    @GetMapping("/me/name")
    public ResponseEntity<Map<String, String>> getMeName(){
        User user = getUserOutOfContext();
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("name", response.getName()));
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> getUserEmailById(@PathVariable Long id){
        User user = userService.findById(id);
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("email", response.getEmail()));
    }

    @GetMapping("/me/email")
    public ResponseEntity<Map<String, String>> getMeEmail(){
        User user = getUserOutOfContext();
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("email", response.getEmail()));
    }

    @GetMapping("/{id}/id")
    public ResponseEntity<Map<String, Long>> getUserIdById(@PathVariable Long id){
        User user = userService.findById(id);
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("id", response.getId()));
    }

    @GetMapping("/me/id")
    public ResponseEntity<Map<String, Long>> getMeId(){
        User user = getUserOutOfContext();
        UserResponse response = userResponseConverter.convertUserToResponse(user);
        return ResponseEntity.ok(getResponse("id", response.getId()));
    }

    @GetMapping("/{id}/created-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByUser(@PathVariable Long id){
        User user = userService.findById(id);
        List<TaskResponse> tasks = taskService.findAllTasksByAuthor(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/assigned-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksAssignedToUser(@PathVariable Long id){
        User user = userService.findById(id);
        List<TaskResponse> tasks = taskService.findAllTasksByAssignee(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/me/created-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByMe(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        List<TaskResponse> tasks = taskService.findAllTasksByAuthor(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/me/assigned-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksAssignedToMe(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        List<TaskResponse> tasks = taskService.findAllTasksByAssignee(user).stream()
                .map(taskDtoConverter::convertDtoToResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/me/name")
    public ResponseEntity<UserResponse> updateName(
            @RequestParam(name = "name") String name){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.updateUserNameByEmail(userDetails.getUsername(), name);
        return ResponseEntity.ok(userResponseConverter.convertUserToResponse(user));
    }

    @PutMapping("/me/email")
    public ResponseEntity<UserResponse> updateEmail(
            @RequestParam(name = "email") String email){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.updateUserEmailByEmail(userDetails.getUsername(), email);
        return ResponseEntity.ok(userResponseConverter.convertUserToResponse(user));
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @RequestParam(name = "password") String password){
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
