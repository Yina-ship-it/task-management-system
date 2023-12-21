package com.example.taskmanagementsystem.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @Schema(example = "1")
    private Long id;

    @Size(max = 255)
    @Schema(example = "Example name")
    private String name;

    @Size(max = 255)
    @Schema(example = "user@mail.example")
    private String email;
}
