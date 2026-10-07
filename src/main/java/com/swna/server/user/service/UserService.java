package com.swna.server.user.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring 트랜잭션 임포트 권장

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.common.service.AbstractBaseService;
import com.swna.server.user.dto.CreateUserRequest;
import com.swna.server.user.dto.UserResponse;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.entity.service.UserDomainService;
import com.swna.server.user.security.SecurityUtils;
import com.swna.server.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractBaseService<User, Long> {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    /**
     * 1. 회원가입 (SignupUseCase 대체)
     */
    @Transactional
    public void signup(String email, String password) {
        String encoded = passwordEncoder.encode(password);
        User user = User.createWithNoRole(encoded, email);
        
        // 명시적 null 체크
        if (user == null) {
            throw BusinessException.builder(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Failed to create user entity")
                .detail("email", email)
                .build();
        }
        
        userRepository.save(user);
    }

    /**
     * 2. 사용자 생성 (CreateUserUseCase 대체)
     */
    @SuppressWarnings("null")
    public UserResponse createUser(CreateUserRequest req) {
        User user = userDomainService.create(req.name(), req.email());
        User savedResult = userRepository.save(user);
        return UserResponse.from(savedResult);
    }

    /**
     * 3. 사용자 조회 (GetUserUseCase 대체)
     */
    public UserResponse getUser(@NonNull Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return UserResponse.from(user);
    }

    /**
     * 4. 현재 로그인한 사용자의 ID 조회 (GetCurrentUserUseCase 대체)
     */
    public Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * 5. 사용자 삭제 (DeleteUserUseCase 대체)
     */
    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 기존 UserService의 로직 유지
     */
    public void someLogic() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            principal.getUserId();
        }
    }
}