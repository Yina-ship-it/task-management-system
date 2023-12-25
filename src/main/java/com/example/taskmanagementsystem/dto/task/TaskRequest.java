package com.example.taskmanagementsystem.dto.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255)
    @Schema(example = "Example task title")
    private String title;

    @Schema(example = "Example task description")
    private String description;

    @Min(1)
    @Max(3)
    private Integer statusValue;

    @Min(1)
    @Max(3)
    private Integer priorityValue;

    @Valid
    private List<@Size(max = 255) String> assigneesEmail = new ArrayList<>();
    private List<Long> assigneesId = new ArrayList<>();
}
