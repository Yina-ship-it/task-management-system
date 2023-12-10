package com.example.taskmanagementsystem.controllers;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.dto.task.TaskRequest;
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

import java.net.URI;
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
    private UserService userService;

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

    @PostMapping("/")
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest taskRequest){
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            TaskDto taskDto = taskDtoConverter.convertRequestToDto(taskRequest);
            taskDto.setAuthor(user);
            TaskResponse task = taskDtoConverter.convertDtoToResponse(taskService.createTask(taskDto));
            URI location = new URI("/api/tasks/" + task.getId());
            return ResponseEntity.created(location).body(task);
        } catch (EntityNotFoundException e) {
            log.severe(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.severe(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
