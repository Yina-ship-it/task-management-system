package com.example.taskmanagementsystem.dto.comment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yina-ship-it
 * @since 13.12.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class CommentRequest {
    private String text;
    private Long taskId;
}
