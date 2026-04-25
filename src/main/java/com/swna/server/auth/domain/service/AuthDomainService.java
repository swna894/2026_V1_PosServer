package com.swna.server.auth.domain.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.swna.server.auth.domain.model.RefreshToken;
import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;

@Component
public class AuthDomainService {
    public void validatePassword(String password, String encodedPassword, PasswordEncoder encoder) {
        if (!encoder.matches(password, encodedPassword)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_PASSWORD);
        }
    }

    public void validateNotExpired(RefreshToken token) {
        if (token.isExpired()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED);
        }
    }
}
