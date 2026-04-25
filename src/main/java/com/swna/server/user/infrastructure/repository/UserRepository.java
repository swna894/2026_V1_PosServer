package com.swna.server.user.infrastructure.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndRole(Long id, Role role);

    boolean existsByEmail(String email);
}

