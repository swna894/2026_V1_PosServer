package com.swna.server.auth.application.usecase;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.swna.server.auth.domain.model.RefreshToken;
import com.swna.server.auth.domain.service.AuthDomainService;
import com.swna.server.auth.dto.request.LoginRequest;
import com.swna.server.auth.dto.response.LoginResponse;
import com.swna.server.auth.infrastructure.repository.RefreshTokenRepository;
import com.swna.server.auth.jwt.JwtProvider;
import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthDomainService authDomainService;

    @Transactional
    public LoginResponse execute(LoginRequest req) {

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        authDomainService.validatePassword(
                req.password(),
                user.getPassword(),
                passwordEncoder
        );

        String access = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtProvider.createRefreshToken(user.getId(), user.getRole());

        refreshTokenRepository.deleteByUserId(user.getId());

        refreshTokenRepository.save( new RefreshToken(user.getId(), refresh, LocalDateTime.now().plusDays(7)));

        return new LoginResponse( access, refresh,user.getRole().name());
    }
}