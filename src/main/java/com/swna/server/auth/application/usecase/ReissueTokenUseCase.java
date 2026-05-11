package com.swna.server.auth.application.usecase;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.swna.server.auth.domain.model.RefreshToken;
import com.swna.server.auth.domain.service.AuthDomainService;
import com.swna.server.auth.dto.request.TokenRequest;
import com.swna.server.auth.dto.response.TokenResponse;
import com.swna.server.auth.infrastructure.repository.RefreshTokenRepository;
import com.swna.server.auth.jwt.JwtProvider;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {

    private final RefreshTokenRepository refreshRepo;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthDomainService domainService;

        @Transactional
        public TokenResponse execute(TokenRequest req) {
        // 1. 전달받은 토큰 자체가 유효한 JWT인지 + 만료되지 않았는지 먼저 검증 (JwtProvider 활용)
        // parseClaims 내부에 이미 서명 및 만료 체크가 포함되어 있음
        Long userIdFromToken = jwtProvider.getUserId(req.refreshToken());

        // 2. DB에서 해당 토큰 존재 여부 확인
        // [보안 팁] 만약 DB에 없는데 유효한 JWT라면? 
        // 탈취된 토큰으로 재사용 공격(Replay Attack)을 시도했을 가능성이 큼
        // 이때는 해당 유저의 모든 리프레시 토큰을 삭제하는 로직을 추가하면 더 안전합니다.
        RefreshToken saved = refreshRepo.findByToken(req.refreshToken())
                .orElseThrow( ExceptionUtils::invalidToken  );

        // 3. DB 기반 추가 만료 검증 (Optional)
        domainService.validateNotExpired(saved);
        
        // 4. 유저 확인
        User user = userRepository.findById(userIdFromToken)
                .orElseThrow(() -> ExceptionUtils.userNotFound(String.valueOf(userIdFromToken)));

        // 5. 새로운 토큰 쌍 생성
        String newAccess = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String newRefresh = jwtProvider.createRefreshToken(user.getId(), user.getRole());

        // 6. Rotation: 기존 것 삭제 후 새 토큰 저장
        refreshRepo.delete(java.util.Objects.requireNonNull(saved));
        refreshRepo.save(new RefreshToken(
                user.getId(),
                newRefresh,
                LocalDateTime.now().plusDays(7)
        ));

        return new TokenResponse(newAccess, newRefresh);
        }
}