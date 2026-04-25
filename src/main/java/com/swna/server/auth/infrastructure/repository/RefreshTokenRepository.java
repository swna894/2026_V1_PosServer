package com.swna.server.auth.infrastructure.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.auth.domain.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}