package com.example.taskmanagementsystem.dto.task;

import com.example.taskmanagementsystem.dto.DtoConverter;
import com.example.taskmanagementsystem.dto.profile.UserResponse;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@Component
public class TaskDtoConverter implements DtoConverter<Task, TaskDto, TaskRequest, TaskResponse> {
    @Override
    public TaskDto convertEntityToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .author(task.getAuthor())
                .assignees(task.getAssignees().stream().toList())
                .build();
    }

    @Override
    public Task convertDtoToEntity(TaskDto taskDto) {
        return Task.builder()
                .id(taskDto.getId())
                .title(getNonBlankString(taskDto.getTitle()))
                .description(taskDto.getDescription() != null ?
                        taskDto.getDescription() :
                        "")
                .priority(taskDto.getPriority())
                .status(taskDto.getStatus())
                .author(taskDto.getAuthor())
                .assignees(new HashSet<>(taskDto.getAssignees()))
                .build();
    }

    @Override
    public TaskDto convertRequestToDto(TaskRequest taskRequest) {
        List<User> assignees = taskRequest.getAssigneesEmail().stream()
                .map(email -> User.builder().email(email).build())
                .toList();
        taskRequest.getAssigneesId().forEach(id -> assignees.add(User.builder().id(id).build()));
        return TaskDto.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(taskRequest.getStatusValue() != null ?
                        TaskStatus.getByValue(taskRequest.getStatusValue()) :
                        TaskStatus.PENDING)
                .priority(taskRequest.getPriorityValue() != null ?
                        TaskPriority.getByValue(taskRequest.getPriorityValue()) :
                        TaskPriority.LOW)
                .assignees(assignees)
                .build();
    }

    @Override
    public TaskResponse convertDtoToResponse(TaskDto taskDto) {
        return TaskResponse.builder()
                .id(taskDto.getId())
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .priority(taskDto.getPriority())
                .status(taskDto.getStatus())
                .author(UserResponse.builder()
                        .id(taskDto.getAuthor().getId())
                        .name(taskDto.getAuthor().getName())
                        .email(taskDto.getAuthor().getEmail())
                        .build())
                .assignees(taskDto.getAssignees()
                        .stream()
                        .map(assignee -> UserResponse.builder()
                                .id(assignee.getId())
                                .name(assignee.getName())
                                .email(assignee.getEmail())
                                .build())
                        .toList())
                .build();
    }

    private String getNonBlankString(String value) {
        return (value != null && !value.isBlank()) ? value : null;
    }
}
