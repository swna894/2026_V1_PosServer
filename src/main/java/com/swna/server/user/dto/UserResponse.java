package com.swna.server.user.dto;

import com.swna.server.user.entity.model.User;

public record UserResponse(
        Long id,
        String name,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}