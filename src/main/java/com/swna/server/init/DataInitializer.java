package com.swna.server.init; // 패키지 경로는 프로젝트에 맞게 확인해주세요

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.service.ShopRepository;
import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShopRepository shopRepository;

    @Override
    public void run(String... args) {

        // 1. 관리자 계정 초기화 (존재하지 않을 경우에만 생성)
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            String encodedPassword = passwordEncoder.encode("1234");
            User admin = User.createWithRole("admin@gmail.com", encodedPassword, Role.ADMIN);
            admin.setName("admin");

            userRepository.save(admin);
        }

        // 2. 초기 샵(Shop) 데이터 초기화 (데이터가 하나도 없을 경우에만 생성)
        if (shopRepository.count() == 0) {
            Shop shop = Shop.builder()
                    .company("Hello Banana")
                    .businessNo("123-45-67890")
                    .name("Martin")
                    .email("parkgap75@naver.com")
                    .password(passwordEncoder.encode("1234")) // 패스워드 필드 추가 반영
                    .mobilePhone("010-1234-5678")
                    .phone("03-208-0545")
                    // 세분화된 주소 필드 반영 (street, suburb, city)
                    .street("58 main st")
                    .suburb("Gore")
                    .city("Southland")
                    .comment("Default initial shop")
                    .backupFolder("/backup")
                    .reportFolder("/report")
                    .active(true)
                    .build();

            shopRepository.save(shop);
        }
    }
}