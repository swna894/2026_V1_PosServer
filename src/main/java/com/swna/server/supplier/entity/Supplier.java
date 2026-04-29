package com.swna.server.supplier.entity;

import com.swna.server.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "suppliers", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"abbr"}),
       indexes = {
           @Index(name = "idx_name", columnList = "name"),
           @Index(name = "idx_active", columnList = "active")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String abbr;

    @Column(length = 64, nullable = false)
    private String company = "";

    @Column(nullable = false, length = 32)
    private String name;

    @Column(length = 64, nullable = false)
    private String email = "";

    @Column(length = 128, nullable = false)
    private String address = "";

    @Column(length = 20, nullable = false)
    private String cellphone = "";

    @Column(length = 20, nullable = false)
    private String phone = "";

    @Column(nullable = false)
    private boolean active = true;

    // ===== 생성 메서드 =====
    public Supplier(String abbr, String name) {
        this(abbr, name, "", "", "", "");
    }

    public Supplier(String abbr, String name, String company) {
        this(abbr, name, company, "", "", "");
    }

    public Supplier(String abbr, String name, String company, 
                    String email, String phone, String address) {
        this.abbr = abbr;
        this.name = name;
        this.company = nullToEmpty(company);
        this.email = nullToEmpty(email);
        this.phone = nullToEmpty(phone);
        this.address = nullToEmpty(address);
        this.cellphone = "";
        this.active = true;
    }

    // ===== 비즈니스 메서드 =====
    public void updateInfo(String name, String phone, String email, String address) {
        if (name != null) this.name = name;
        this.phone = nullToEmpty(phone);
        this.email = nullToEmpty(email);
        this.address = nullToEmpty(address);
    }

    public void updateCompany(String company) {
        this.company = nullToEmpty(company);
    }

    public void updateCellphone(String cellphone) {
        this.cellphone = nullToEmpty(cellphone);
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    // ===== NULL 안전 처리 =====
    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    // ===== 편의 메서드 =====
    public boolean isActive() {
        return active;
    }

    public boolean isEmptyAddress() {
        return address == null || address.isBlank();
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", abbr='" + abbr + '\'' +
                ", name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", active=" + active +
                '}';
    }
}