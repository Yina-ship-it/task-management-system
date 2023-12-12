package com.example.taskmanagementsystem.dto.profile;

import com.example.taskmanagementsystem.models.User;
import org.springframework.stereotype.Component;

/**
 * @author Yina-ship-it
 * @since 12.12.2023
 */
@Component
public class UserResponseConverter {
    public UserResponse convertUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }
}
