package com.swna.server.user.application.usecase;

import org.springframework.stereotype.Service;

import com.swna.server.user.infrastructure.security.SecurityUtils;

@Service
public class GetCurrentUserUseCase {

    public Long execute() {
        return SecurityUtils.getCurrentUserId();
    }
}
