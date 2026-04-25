package com.swna.server.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.swna.server.auth.jwt.JwtFilter;

import lombok.RequiredArgsConstructor;

/*
    Atuhentication: 사용자가 누구인지 확인하는 과정
    Authorization: 사용자가 어떤 권한을 가지고 있는지 확인하는 과정

    1️⃣ JwtFilter에서 JWT를 검증하고, UserPrincipal을 생성하여 SecurityContext에 저장
    2️⃣ UserController에서 @PreAuthorize 어노테이션을 사용하여 권한 체크  

    Atuhentication과 Authorization이 모두 JWT 기반으로 처리되므로, 세션을 사용하지 않고 Stateless하게 구현

    Authentication -> JwtFilter -> SecurityContext -> Authorization -> @PreAuthorize

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));  
*/

@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")    
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // 🔥 PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}