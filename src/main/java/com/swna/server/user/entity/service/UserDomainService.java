package com.swna.server.user.entity.service;

import org.springframework.stereotype.Service;

import com.swna.server.user.entity.model.User;

@Service
public class UserDomainService {

    public User create(String name, String email, String encodedPassword) {
        // 순수 비즈니스 규칙
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        return User.createDefault(name, email, encodedPassword);
    }
}
