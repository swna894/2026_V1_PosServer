package com.swna.server.shop.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shops")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String phone;

    private String businessNo;

    private boolean active = true;

    // =========================
    // Factory
    // =========================
    public static Shop create(String name, String address, String phone, String businessNo) {

        Shop shop = new Shop();

        shop.name = name;
        shop.address = address;
        shop.phone = phone;
        shop.businessNo = businessNo;
        shop.active = true;

        return shop;
    }

    // =========================
    // Business
    // =========================
    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
