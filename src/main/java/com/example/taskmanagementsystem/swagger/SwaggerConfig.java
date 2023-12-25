package com.example.taskmanagementsystem.swagger;

import com.example.taskmanagementsystem.dto.comment.CommentResponse;
import com.example.taskmanagementsystem.dto.task.TaskProperty;
import com.example.taskmanagementsystem.dto.task.TaskResponse;
import com.example.taskmanagementsystem.dto.user.UserResponse;
import com.example.taskmanagementsystem.models.TaskPriority;
import com.example.taskmanagementsystem.models.TaskStatus;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Yina-ship-it
 * @since 14.12.2023
 */

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearer Auth")
                .description("JWT auth")
                .scheme("bearer")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        Schema idSchema = new Schema<Map<String, Object>>()
                .addProperty("id", new IntegerSchema().format("int64").example(1));
        Schema nameSchema = new Schema<Map<String, Object>>()
                .addProperty("name", new StringSchema().example("example name"));
        Schema emailSchema = new Schema<Map<String, Object>>()
                .addProperty("email", new StringSchema().example("user@mail.example"));
        Schema titleSchema = new Schema<Map<String, Object>>()
                .addProperty("title", new StringSchema().example("Example task title"));
        Schema descriptionSchema = new Schema<Map<String, Object>>()
                .addProperty("description", new StringSchema().example("Example task description"));
        Schema statusSchema = new Schema<Map<String, Object>>()
                .addProperty("status", new Schema().example(new TaskProperty(TaskStatus.IN_PROGRESS)));
        Schema prioritySchema = new Schema<Map<String, Object>>()
                .addProperty("priority", new Schema().example(new TaskProperty(TaskPriority.MEDIUM)));
        Schema authorSchema = new Schema<Map<String, Object>>()
                .addProperty("author", new Schema().example(new UserResponse(1L, "Example name", "user@mail.example")));
        Schema assigneesSchema = new Schema<Map<String, Object>>()
                .addProperty("assignees", new Schema().example(List.of(
                        new UserResponse(2L, "Example name 2", "user2@mail.example"),
                        new UserResponse(3L, "Example name 3", "user3@mail.example")
                )));
        Schema commentsSchema = new Schema<Map<String, Object>>()
                .addProperty("comments", new Schema().example(List.of(
                        new CommentResponse(1L, "Example comment text 1",
                                new UserResponse(2L, "Example name 2", "user2@mail.example"), LocalDateTime.now()),
                        new CommentResponse(2L, "Example comment text 2",
                                new UserResponse(1L, "Example name", "user@mail.example"), LocalDateTime.now())
                )));

        Schema taskResponseSchema = new Schema<TaskResponse>()._default(new TaskResponse(
                1L,
                "Example task title", "Example task description",
                new TaskProperty(TaskStatus.IN_PROGRESS),
                new TaskProperty(TaskPriority.MEDIUM),
                new UserResponse(1L, "Example name", "user@mail.example"),
                List.of(
                        new UserResponse(2L, "Example name 2", "user2@mail.example"),
                        new UserResponse(3L, "Example name 3", "user3@mail.example")
                ),
                List.of(
                        new CommentResponse(1L, "Example comment text 1",
                                new UserResponse(2L, "Example name 2", "user2@mail.example"), LocalDateTime.now()),
                        new CommentResponse(2L, "Example comment text 2",
                                new UserResponse(1L, "Example name", "user@mail.example"), LocalDateTime.now())
                )
        ));

        return new OpenAPI()
                .info(new Info()
                        .title("")
                        .version("")
                        .description("jwt token from demo user: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZWZhdWx0X3VzZXJAbWFpbC50ZXN0IiwiZXhwIjo0ODU4MTY3NjAwfQ.Ejkccdx_E8gaT0K-pweEMsUTxpj3NlHpi31L9x4YlkTU8UWXgUhWKzvz6QG9tvlYK4eDLOjJHiQqIPQWdWYFtQ\n")
                )
                .components(new Components()
                        .addSecuritySchemes(securityScheme.getName(), securityScheme)
                        .addSchemas("idSchema", idSchema)
                        .addSchemas("nameSchema", nameSchema)
                        .addSchemas("emailSchema", emailSchema)
                        .addSchemas("titleSchema", titleSchema)
                        .addSchemas("descriptionSchema", descriptionSchema)
                        .addSchemas("statusSchema", statusSchema)
                        .addSchemas("prioritySchema", prioritySchema)
                        .addSchemas("authorSchema", authorSchema)
                        .addSchemas("assigneesSchema", assigneesSchema)
                        .addSchemas("commentsSchema", commentsSchema)
                        .addSchemas("taskResponseSchema", taskResponseSchema)
                );
    }
}
