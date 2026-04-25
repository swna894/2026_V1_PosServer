package com.swna.server.user.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;

    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.role = Role.USER; // 기본 역할 설정
    }

    public User(String name, String password, Role role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public User(String name, String email, String encodedPassword) {
        this.name = name;
        this.email = email;
        this.password = encodedPassword;
        this.role = Role.USER; // 기본 역할 설정
    }

}
