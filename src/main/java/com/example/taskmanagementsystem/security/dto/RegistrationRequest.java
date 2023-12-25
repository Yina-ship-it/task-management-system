package com.example.taskmanagementsystem.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yina-ship-it
 * @since 08.12.2023
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {
    @Size(max = 255)
    @Schema(description = "User's email address", example = "user@mail.example")
    private String email;

    @Size(max = 255)
    @Schema(description = "User's password", example = "ExamplePassword123")
    private String password;

    @Size(max = 255)
    @Schema(description = "User's name", example = "Maksim")
    private String name;
}
