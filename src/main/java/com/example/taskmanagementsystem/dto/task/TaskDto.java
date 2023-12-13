package com.example.taskmanagementsystem.dto.task;

import com.example.taskmanagementsystem.models.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private User author;
    private List<User> assignees = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
}
