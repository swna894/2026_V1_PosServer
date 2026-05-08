package com.swna.server.user.application.usecase;


import org.springframework.stereotype.Service;

import com.swna.server.user.dto.CreateUserRequest;
import com.swna.server.user.dto.UserResponse;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.entity.service.UserDomainService;
import com.swna.server.user.infrastructure.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;

    @SuppressWarnings("null")
    public UserResponse execute(CreateUserRequest req) {

        // 1. Domain에서 객체 생성 + 검증
        User user = userDomainService.create( req.name(), req.email());

        // 2. 저장
        User savedResult = userRepository.save(user);
        
        // 3. 응답
        return UserResponse.from(savedResult);
    }
}
