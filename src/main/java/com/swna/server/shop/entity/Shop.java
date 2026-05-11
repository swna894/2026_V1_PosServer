package com.swna.server.shop.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor  // Add this - JPA requires it
@AllArgsConstructor // Add this - Builder requires it
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;
    private String company;
    private String email;
    private String cellphone;   
    private String fax;
    private String phone;
    private String businessNo;
    @Builder.Default
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
