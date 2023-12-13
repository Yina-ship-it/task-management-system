package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.dto.user.UserResponseConverter;
import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
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
import java.util.function.BiFunction;
import java.util.function.Function;

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
        try {
            List<UserResponse> users = userService.findAllUsers().stream()
                    .map(userResponseConverter::convertUserToResponse)
                    .toList();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        return handleGetUserResponse((value) -> userService.findById(value), id);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return handleGetUserResponse((value) -> userService.findByEmail(value), userDetails.getUsername());
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<Map<String, String>> getUserNameById(@PathVariable Long id){
        return handleFieldRequest((value) -> userService.findById(value), id,
                "name", UserResponse::getName);
    }

    @GetMapping("/me/name")
    public ResponseEntity<Map<String, String>> getMeName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return handleFieldRequest((value) -> userService.findByEmail(value), userDetails.getUsername(),
                "name", UserResponse::getName);
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> getUserEmailById(@PathVariable Long id){
        return handleFieldRequest((value) -> userService.findById(value), id,
                "email", UserResponse::getEmail);
    }

    @GetMapping("/me/email")
    public ResponseEntity<Map<String, String>> getMeEmail(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return handleFieldRequest((value) -> userService.findByEmail(value), userDetails.getUsername(),
                "email", UserResponse::getEmail);
    }

    @GetMapping("/{id}/id")
    public ResponseEntity<Map<String, Long>> getUserIdById(@PathVariable Long id){
        return handleFieldRequest((value) -> userService.findById(value), id,
                "id", UserResponse::getId);
    }

    @GetMapping("/me/id")
    public ResponseEntity<Map<String, Long>> getMeId(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return handleFieldRequest((value) -> userService.findByEmail(value), userDetails.getUsername(),
                "id", UserResponse::getId);
    }

    @GetMapping("/{id}/created-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByUser(@PathVariable Long id){
        return handleGetListTasksResponse((value) -> userService.findById(value), id,
                (user) -> taskService.findAllTasksByAuthor(user));
    }

    @GetMapping("/{id}/assigned-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksAssignedToUser(@PathVariable Long id){
        return handleGetListTasksResponse((value) -> userService.findById(value), id,
                (user) -> taskService.findAllTasksByAssignee(user));
    }

    @GetMapping("/me/created-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByMe(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return handleGetListTasksResponse((value) -> userService.findByEmail(value), userDetails.getUsername(),
                (user) -> taskService.findAllTasksByAuthor(user));
    }

    @GetMapping("/me/assigned-tasks")
    public ResponseEntity<List<TaskResponse>> getTasksAssignedToMe(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return handleGetListTasksResponse((value) -> userService.findByEmail(value), userDetails.getUsername(),
                (user) -> taskService.findAllTasksByAssignee(user));
    }

    @PutMapping("/me/name")
    public ResponseEntity<UserResponse> updateName(
            @RequestParam(name = "name") String name){
        return handleUpdateUserField(name, (email, value) -> userService.updateUserNameByEmail(email, value));
    }

    @PutMapping("/me/email")
    public ResponseEntity<UserResponse> updateEmail(
            @RequestParam(name = "email") String email){
        return handleUpdateUserField(email, (oldEmail, value) -> userService.updateUserEmailByEmail(oldEmail, value));
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @RequestParam(name = "password") String password){
        return handleUpdateUserField(password, (email, value) -> userService.updateUserPasswordByEmail(email, value));
    }


    private <V> ResponseEntity<UserResponse> handleGetUserResponse(Function<V, User> userExtractor, V value) {
        try {
            User user = userExtractor.apply(value);
            UserResponse userResponse = userResponseConverter.convertUserToResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (EntityNotFoundException e) {
            log.severe(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private <V> ResponseEntity<List<TaskResponse>> handleGetListTasksResponse(
            Function<V, User> userExtractor,
            V value,
            Function<User, List<TaskDto>> taskDtoListExtractor) {
        try {
            User user = userExtractor.apply(value);
            List<TaskResponse> tasks = taskDtoListExtractor.apply(user).stream()
                    .map(taskDtoConverter::convertDtoToResponse)
                    .toList();
            return ResponseEntity.ok(tasks);
        } catch (EntityNotFoundException e) {
            log.severe(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<UserResponse> handleUpdateUserField(
            String value,
            BiFunction<String, String, User> updateFunction) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetails.getUsername();
            User user = updateFunction.apply(email, value);
            return ResponseEntity.ok(userResponseConverter.convertUserToResponse(user));
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

    private <T, V> ResponseEntity<Map<String, T>> handleFieldRequest(
            Function<V, User> userExtractor,
            V value,
            String key,
            Function<UserResponse, T> valueExtractor) {
        try {
            User user = userExtractor.apply(value);
            UserResponse response = userResponseConverter.convertUserToResponse(user);
            return ResponseEntity.ok(getResponse(key, valueExtractor.apply(response)));
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
