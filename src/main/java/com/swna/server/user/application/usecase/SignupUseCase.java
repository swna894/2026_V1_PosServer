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

        String encoded = passwordEncoder.encode(password);
        User user = User.createWithNoRole(encoded, email);
        
        // 명시적 null 체크 (권장)
        if (user == null) {
            throw BusinessException.builder(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Failed to create user entity")
                .detail("email", email)
                .build();
        }
        
        userRepository.save(user);
    }
}
