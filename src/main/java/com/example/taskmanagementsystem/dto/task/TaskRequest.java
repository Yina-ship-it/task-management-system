package com.example.taskmanagementsystem.dto.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class TaskRequest {
    private String title;
    private String description;
    private Integer statusValue;
    private Integer priorityValue;
    private List<String> assigneesEmail = new ArrayList<>();
    private List<Long> assigneesId = new ArrayList<>();
}
