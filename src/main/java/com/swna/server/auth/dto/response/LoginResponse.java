package com.swna.server.auth.dto.response;

public record LoginResponse(String accessToken, String refreshToken,  String role) {}
