package com.swna.server.user.application.usecase;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.swna.server.user.dto.UserResponse;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;

    public UserResponse execute(@NonNull Long id) {

        User user = userRepository.findById(id)
                .orElseThrow();

        return UserResponse.from(user);
    }
}
