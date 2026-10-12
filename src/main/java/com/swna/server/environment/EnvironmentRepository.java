package com.swna.server.environment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<EnvironmentEntity, Long> {
}