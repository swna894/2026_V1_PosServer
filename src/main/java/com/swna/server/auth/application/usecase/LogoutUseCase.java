package com.swna.server.auth.application.usecase;

import org.springframework.stereotype.Service;

import com.swna.server.auth.infrastructure.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void execute(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}