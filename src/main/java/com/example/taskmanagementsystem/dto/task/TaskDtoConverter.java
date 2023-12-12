package com.example.taskmanagementsystem.dto.task;

import com.example.taskmanagementsystem.dto.DtoConverter;
import com.example.taskmanagementsystem.dto.profile.UserResponse;
import com.example.taskmanagementsystem.dto.profile.UserResponseConverter;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@Component
public class TaskDtoConverter implements DtoConverter<Task, TaskDto, TaskRequest, TaskResponse> {

    @Autowired
    UserResponseConverter userResponseConverter;

    @Override
    public TaskDto convertEntityToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .author(task.getAuthor())
                .assignees(new ArrayList<>(task.getAssignees()))
                .build();
    }

    @Override
    public Task convertDtoToEntity(TaskDto taskDto) {
        return Task.builder()
                .id(taskDto.getId())
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .priority(taskDto.getPriority())
                .status(taskDto.getStatus())
                .author(taskDto.getAuthor())
                .assignees(taskDto.getAssignees())
                .build();
    }

    @Override
    public TaskDto convertRequestToDto(TaskRequest taskRequest) {
        List<User> assignees = new ArrayList<>();
        if (taskRequest.getAssigneesEmail() != null)
            assignees = taskRequest.getAssigneesEmail().stream()
                .map(email -> User.builder().email(email).build())
                .collect(Collectors.toList());
        if (taskRequest.getAssigneesId() != null){
            for (Long id : taskRequest.getAssigneesId()) {
                assignees.add(User.builder().id(id).build());
            }
        }
        return TaskDto.builder()
                .title(getNonBlankString(taskRequest.getTitle()))
                .description(taskRequest.getDescription() != null ?
                        taskRequest.getDescription() :
                        "")
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
                .author(userResponseConverter.convertUserToResponse(taskDto.getAuthor()))
                .assignees(taskDto.getAssignees()
                        .stream()
                        .map(userResponseConverter::convertUserToResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private String getNonBlankString(String value) {
        return (value != null && !value.isBlank()) ? value : null;
    }
}
