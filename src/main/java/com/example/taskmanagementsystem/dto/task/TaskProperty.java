package com.example.taskmanagementsystem.dto.task;

import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yina-ship-it
 * @since 25.12.2023
 */
@Data
@NoArgsConstructor
public class TaskProperty {
    private String text;
    private int value;

    public TaskProperty(TaskStatus taskStatus) {
        this.text = taskStatus.getText();
        this.value = taskStatus.getValue();
    }

    public TaskProperty(TaskPriority taskPriority) {
        this.text = taskPriority.getText();
        this.value = taskPriority.getValue();
    }
}
