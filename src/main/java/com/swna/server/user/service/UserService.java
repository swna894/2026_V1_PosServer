package com.swna.server.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.common.service.AbstractBaseService;
import com.swna.server.user.dto.CreateUserRequest;
import com.swna.server.user.dto.UserResponse;
import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;
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
     * 1. 회원가입
     */
    @Transactional
    public void signup(String email, String password) {
        String encoded = passwordEncoder.encode(password);
        User user = User.createWithNoRole(email, encoded);
        
        if (user == null) {
            throw BusinessException.builder(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Failed to create user entity")
                .detail("email", email)
                .build();
        }
        
        userRepository.save(user);
    }

    /**
     * 2. 사용자 생성 (도메인 서비스 연동)
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        User user = userDomainService.create(req.name(), req.email());
        User savedResult = userRepository.save(user);
        return UserResponse.from(savedResult);
    }

    /**
     * 3. 사용자 조회 (ID)
     */
    public User findUserById(@NonNull Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    /**
     * 4. 사용자 조회 (Email)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 5. 권한별 사용자 목록 조회
     */
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * 6. 사용자 정보 저장 및 수정
     */
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 7. 사용자 삭제
     */
    @Transactional
    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    public Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
}