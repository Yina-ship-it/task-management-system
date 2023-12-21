package com.example.taskmanagementsystem.dto.comment;

import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Yina-ship-it
 * @since 13.12.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class CommentResponse {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "Example comment text")
    private String text;

    private UserResponse commentator;

    private LocalDateTime dateTime;
}
