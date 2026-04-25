package com.swna.server.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;

import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (!userRepository.existsByEmail("admin@swna.com")) {

            User admin = new User("admin", "admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setRole(Role.ADMIN); // 관리자 권한

            userRepository.save(admin);

            System.out.println("기본 관리자 계정 생성 완료");
        }
    }
}