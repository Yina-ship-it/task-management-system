package com.example.taskmanagementsystem.services;

import com.example.taskmanagementsystem.dto.comment.CommentDto;
import com.example.taskmanagementsystem.dto.task.TaskDto;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import com.example.taskmanagementsystem.models.User;

import java.util.List;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
public interface TaskService {
    List<TaskDto> findAllTasks();
    TaskDto findTaskById(Long id);
    TaskDto createTask(TaskDto taskDto);
    void deleteTaskById(Long id, User author);

    TaskDto updateTaskTitleById(Long id, String title, User author);
    TaskDto updateTaskDescriptionById(Long id, String description, User author);
    TaskDto updateTaskStatusById(Long id, Integer taskStatusValue, User authorOrAssignee);
    TaskDto updateTaskPriorityById(Long id, Integer taskPriorityValue, User author);
    TaskDto removeAssigneeByIdInTask( Long taskId, Long assigneeId, User author);
    TaskDto removeAssigneeByEmailInTask(Long taskId, String assigneeEmail, User author);
    TaskDto appendAssigneeByIdInTask(Long taskId, Long assigneeId, User author);
    TaskDto appendAssigneeByEmailInTask(Long taskId, String assigneeEmail, User author);
    TaskDto appendCommentInTask(Long taskId, CommentDto commentDto, User commentator);
    TaskDto removeCommentByIdInTask(Long taskId, Long commentId, User commentatorOrTaskAuthor);

    List<TaskDto> findAllTasksByAuthor(User author);
    List<TaskDto> findAllTasksByAssignee(User assignee);
}
