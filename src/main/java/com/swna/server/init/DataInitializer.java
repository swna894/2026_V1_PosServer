package com.swna.server.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.repository.ShopRepository;
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

        if (!userRepository.existsByEmail("admin@gmail.com")) {
            
            String encodedPassword = passwordEncoder.encode("1234");
            User admin = User.createWithRole("admin@gmail.com", encodedPassword , Role.ADMIN);
            admin.setName("admin");

            userRepository.save(admin);

            System.out.println("기본 관리자 계정 생성 완료");
        }


        Shop shop = Shop.builder()
                .name("Martin")
                .address("58 main st, Gore")
                .company("Hello Banana")
                .fax("03-208-0545")
                .phone("010-1234-5678")
                .cellphone("021-117-9922")
                .email("parkgap75@naver.com")
                .businessNo("Hello Banana")
                .build();
                
        if (shop != null) {
           // shopRepository.save(shop);
        }
    }
        
}