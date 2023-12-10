package com.example.taskmanagementsystem.services.impl;

import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.dto.task.TaskDtoConverter;
import com.example.taskmanagementsystem.models.Task;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;
import com.example.taskmanagementsystem.repositories.TaskRepository;
import com.example.taskmanagementsystem.services.TaskService;
import com.example.taskmanagementsystem.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@Service
@Primary
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskDtoConverter taskDtoConverter;

    @Override
    public List<TaskDto> findAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(taskDtoConverter::convertEntityToDto).toList();
    }

    @Override
    public TaskDto findTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task with id=" + id + " not found!"));
        return taskDtoConverter.convertEntityToDto(task);
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        validateTaskDto(taskDto);

        List<User> assignees = getAssigneesFromDto(taskDto);

        Task task = taskDtoConverter.convertDtoToEntity(taskDto);
        task.setAssignees(assignees);

        return taskDtoConverter.convertEntityToDto(taskRepository.save(task));
    }

    @Override
    public void deleteTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id=" + id + " not found!"));
        validateAuthor(task, user);

        taskRepository.delete(task);
    }

    @Override
    public TaskDto updateTaskTitleById(Long id, String title, User author) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task with id=" + id + " not found!"));

        validateAuthor(task, author);
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Invalid title=" + title);

        task.setTitle(title);
        System.out.println(task);
        Task newtask = taskRepository.save(task);
        System.out.println(newtask);
        return taskDtoConverter.convertEntityToDto(newtask);
    }

    @Override
    public TaskDto updateTaskDescriptionById(Long id, String description, User author) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task with id=" + id + " not found!"));

        validateAuthor(task, author);
        if (description == null)
            throw new IllegalArgumentException("Invalid description=null");

        task.setDescription(description);
        return taskDtoConverter.convertEntityToDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateTaskStatusById(Long id, Integer taskStatusValue, User authorOrAssignee) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task with id=" + id + " not found!"));

        validateAuthorOrAssignee(task, authorOrAssignee);
        task.setStatus(TaskStatus.getByValue(taskStatusValue));
        return taskDtoConverter.convertEntityToDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateTaskPriorityById(Long id, Integer taskPriorityValue, User author) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task with id=" + id + " not found!"));

        validateAuthor(task, author);
        task.setPriority(TaskPriority.getByValue(taskPriorityValue));
        return taskDtoConverter.convertEntityToDto(taskRepository.save(task));
    }

    @Override
    public TaskDto removeAssigneeByIdInTask(Long taskId, Long assigneeId, User author) {
        return null;
    }

    @Override
    public TaskDto removeAssigneeByEmailInTask(Long taskId, String assigneeEmail, User author) {
        return null;
    }

    @Override
    public TaskDto appendAssigneeByIdInTask(Long taskId, Long assigneeId, User author) {
        return null;
    }

    @Override
    public TaskDto appendAssigneeByEmailInTask(Long taskId, String assigneeEmail, User author) {
        return null;
    }

    private void validateTaskDto(TaskDto taskDto) {
        if (taskDto.getTitle() == null) {
            throw new IllegalArgumentException("Invalid title=null");
        }
    }

    private void validateAuthorOrAssignee(Task task, User authorOrAssignee) {
        if (!task.getAuthor().equals(authorOrAssignee) && !task.getAssignees().contains(authorOrAssignee)) {
            throw new IllegalArgumentException("Only the author can update the task");
        }
    }

    private void validateAuthor(Task task, User user) {
        if (!task.getAuthor().equals(user)) {
            throw new IllegalArgumentException("Only the author can update the task");
        }
    }

    private List<User> getAssigneesFromDto(TaskDto taskDto) {
        return taskDto.getAssignees().stream()
                .map(assignee -> {
                    if (assignee.getId() != null) {
                        return userService.findById(assignee.getId());
                    }
                    if (assignee.getEmail() != null) {
                        return userService.findByEmail(assignee.getEmail());
                    }
                    throw new IllegalArgumentException("Task contains an invalid assignee! The assignee must have at least an id or email.");
                }).distinct().collect(Collectors.toList());
    }
}
