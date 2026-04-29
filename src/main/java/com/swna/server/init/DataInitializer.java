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

        if (!userRepository.existsByEmail("admin@gmail.com")) {
            
            String encodedPassword = passwordEncoder.encode("1234");
            User admin = User.createWithRole("admin@gmail.com", encodedPassword , Role.ADMIN);
            admin.setName("admin");

            userRepository.save(admin);

            System.out.println("기본 관리자 계정 생성 완료");
        }
    }
}