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
    @Schema(example = "user@mail.example")
    private String email;

    @Size(max = 255)
    @Schema(example = "example password")
    private String password;

    @Size(max = 255)
    @Schema(example = "Example name")
    private String name;
}
