package com.swna.server.user.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자 호출 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @JsonIgnore // 보안상 비밀번호는 JSON 출력에서 제외
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Embedded
    private Address address;

    @Embedded
    private ContactInfo contact;

    // =========================
    // 정적 팩토리 메서드 (Factory Methods)
    // =========================
    
    /**
     * 기본 유저 생성 (Role.USER 기본값)
     */
    public static User createDefault(String name, String email, String encodedPassword) {
        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .role(Role.USER)
                .build();
    }

    /**
     * 특정 역할을 가진 유저 생성
     */
    public static User createWithNoRole(String email, String password) {
        return User.builder()
                .email(email)
                .password(password)
                .role(Role.USER) // 기본값은 USER로 설정
                .build();
    }
    /**
     * 특정 역할을 가진 유저 생성
     */
    public static User createWithRole(String email, String password, Role role) {
        return User.builder()
                .email(email)
                .password(password)
                .role(role)
                .build();
    }

    // =========================
    // 비즈니스 로직
    // =========================

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void updateContact(ContactInfo contact) {
        this.contact = contact;
    }
}