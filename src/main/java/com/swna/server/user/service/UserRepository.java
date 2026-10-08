package com.swna.server.user.service; // 또는 repository 패키지 경로

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndRole(Long id, Role role);

    boolean existsByEmail(String email);

    // [추가] 권한(Role)별 사용자 목록 조회를 위한 메서드
    List<User> findByRole(Role role);
}