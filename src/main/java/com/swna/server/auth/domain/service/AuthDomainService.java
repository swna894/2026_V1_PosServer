package com.swna.server.auth.domain.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.swna.server.auth.domain.model.RefreshToken;
import com.swna.server.common.exception.ExceptionUtils;

@Component
public class AuthDomainService {
    public void validatePassword(String password, String encodedPassword, PasswordEncoder encoder) {
        if (!encoder.matches(password, encodedPassword)) {
            throw ExceptionUtils.invalidPassword();
        }
    }

    public void validateNotExpired(RefreshToken token) {
        if (token.isExpired()) {
            throw ExceptionUtils.tokenExpired();
        }
    }
}
