package com.example.taskmanagementsystem.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * @author Yina-ship-it
 * @since 14.12.2023
 */
@OpenAPIDefinition(
        info = @Info(
                title = "",
                version = "",
                description = "jwt token from demo user: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZWZhdWx0X3VzZXJAbWFpbC50ZXN0IiwiZXhwIjo0ODU4MTY3NjAwfQ.Ejkccdx_E8gaT0K-pweEMsUTxpj3NlHpi31L9x4YlkTU8UWXgUhWKzvz6QG9tvlYK4eDLOjJHiQqIPQWdWYFtQ\n"
        ),
        security = {
                        @SecurityRequirement(
                                name = "bearer Auth"
                        )
        }
)
@SecurityScheme(
        name = "bearer Auth",
        description = "JWT auth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
