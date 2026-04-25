package com.swna.server.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(

    @NotBlank(message = "email is required")
    @Size(min = 3, max = 20)
    String email,

    @NotBlank(message = "password is required")
    @Size(min = 4, max = 100)
    String password

) {}
