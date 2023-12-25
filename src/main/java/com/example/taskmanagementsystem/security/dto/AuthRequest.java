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
public class AuthRequest {
    @Size(max = 255)
    @Schema(description = "User's email address", example = "default_user@mail.test")
    private String email;

    @Size(max = 255)
    @Schema(description = "User's password", example = "defoult-user-password")
    private String password;
}
