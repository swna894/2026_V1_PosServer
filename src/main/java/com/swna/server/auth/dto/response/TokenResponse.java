package com.swna.server.auth.dto.response;

public record TokenResponse(String accessToken, String refreshToken) {}