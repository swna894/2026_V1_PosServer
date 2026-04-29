package com.swna.server.common.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @CreationTimestamp @Column(name = "created", updatable = false) private LocalDateTime created;
    @UpdateTimestamp @Column(name = "updated") private LocalDateTime updated;
}