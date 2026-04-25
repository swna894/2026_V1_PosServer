package com.swna.server.user.dto;

public record CreateUserRequest(
        String name,
        String email
) {}
