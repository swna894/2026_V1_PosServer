package com.swna.server.supplier.entity;

import com.swna.server.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "suppliers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supplier extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String abbr;


    @Column(nullable = false, length = 100)
    private String name;
    private String phone;
    private String email;
    private String address;

    // 사용 여부 (삭제 대신 사용)
    private boolean active;

    // ===== 생성 메서드 =====
    public Supplier(String supplierCode, String name) {
        this.abbr = supplierCode;
        this.name = name;
        this.active = true;
    }

    // ===== 비즈니스 메서드 =====
    public void updateInfo(String name, String phone, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public void deactivate() {
        this.active = false;
    }
}