package com.swna.server.user.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String encoded = passwordEncoder.encode(password);

        userRepository.save(
            User.createWithNoRole(encoded, password)
        );
    }
}
