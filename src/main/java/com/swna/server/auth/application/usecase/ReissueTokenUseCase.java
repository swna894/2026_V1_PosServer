package com.swna.server.auth.application.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.swna.server.auth.domain.model.RefreshToken;
import com.swna.server.auth.domain.service.AuthDomainService;
import com.swna.server.auth.dto.request.TokenRequest;
import com.swna.server.auth.dto.response.TokenResponse;
import com.swna.server.auth.infrastructure.repository.RefreshTokenRepository;
import com.swna.server.auth.jwt.JwtProvider;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {

    private final RefreshTokenRepository refreshRepo;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthDomainService domainService;

    @Transactional
    public TokenResponse execute(TokenRequest req) {

        RefreshToken saved = refreshRepo.findByToken(req.refreshToken())
                .orElseThrow(() -> ExceptionUtils.invalidToken());

        domainService.validateNotExpired(saved);

        Long userId = jwtProvider.getUserId(saved.getToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ExceptionUtils.userNotFound(String.valueOf(userId)));

        String newAccess = jwtProvider.createAccessToken(userId, user.getRole());
        String newRefresh = jwtProvider.createRefreshToken(userId, user.getRole());

        // rotation
        refreshRepo.delete(saved);
        refreshRepo.save(new RefreshToken(
                userId,
                newRefresh,
                LocalDateTime.now().plusDays(7)
        ));

        return new TokenResponse(newAccess, newRefresh);
    }
}