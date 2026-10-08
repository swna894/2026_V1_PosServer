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
import com.swna.server.user.dto.UserRecordDto;
import com.swna.server.user.dto.UserResponse;
import com.swna.server.user.entity.model.Address;
import com.swna.server.user.entity.model.ContactInfo;
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
     * 클라이언트(관리자 화면 등)에서 전달된 DTO를 기반으로 사용자 신규 등록
     */
    @Transactional
    public UserRecordDto createUserFromDto(UserRecordDto req) {
        // 1. 이메일 중복 검증 (이미 존재하면 예외 처리)
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already exists: " + req.email());
        }

        // 2. 임시 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(req.email()); // 이메일을 임시 비밀번호로 사용 (실제 서비스에서는 별도의 비밀번호를 받아야 함)

        // 3. User 엔티티 생성 (이름, 이메일, 주소, 연락처 등 포함)
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(encodedPassword)
                .role(req.role() != null ? req.role() : Role.USER)
                .address(new Address(req.city(), req.street(), req.surburb()))
                .contact(new ContactInfo(req.phone(), req.mobile()))
                .build();

        // 4. DB 저장 후 DTO로 변환하여 반환
        User savedUser = userRepository.save(user);
        return UserRecordDto.from(savedUser);
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