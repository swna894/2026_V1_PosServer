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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String businessNo;
    private String name;
    private String email;
    private String password;
    private String ccEmail;
    private String mobilePhone;
    private String phone;
    private String street;
    private String suburb;
    private String city;
    private String comment;
    private String backupFolder;
    private String reportFolder;

    @Builder.Default
    private boolean active = true;

    // =========================
    // Factory Method
    // =========================
    public static Shop create(
            String company,
            String businessNo,
            String name,
            String email,
            String password,
            String ccEmail,
            String mobilePhone,
            String phone,
            String street,
            String suburb,
            String city,
            String comment,
            String backupFolder,
            String reportFolder
    ) {
        Shop shop = new Shop();
        shop.company = company;
        shop.businessNo = businessNo;
        shop.name = name;
        shop.email = email;
        shop.password = password;
        shop.ccEmail = ccEmail;
        shop.mobilePhone = mobilePhone;
        shop.phone = phone;
        shop.street = street;
        shop.suburb = suburb;
        shop.city = city;
        shop.comment = comment;
        shop.backupFolder = backupFolder;
        shop.reportFolder = reportFolder;
        shop.active = true;

        return shop;
    }

    // =========================
    // Business Methods
    // =========================
    public void update(
            String company,
            String businessNo,
            String name,
            String email,
            String password,
            String ccEmail,
            String mobilePhone,
            String phone,
            String street,
            String suburb,
            String city,
            String comment,
            String backupFolder,
            String reportFolder
    ) {
        this.company = company;
        this.businessNo = businessNo;
        this.name = name;
        this.email = email;
        this.password = password;
        this.ccEmail = ccEmail;
        this.mobilePhone = mobilePhone;
        this.phone = phone;
        this.street = street;
        this.suburb = suburb;
        this.city = city;
        this.comment = comment;
        this.backupFolder = backupFolder;
        this.reportFolder = reportFolder;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public String getAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        if (street != null && !street.isEmpty()) {
            addressBuilder.append(street);
        }
        if (suburb != null && !suburb.isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(suburb);
        }
        if (city != null && !city.isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(city);
        }
        return addressBuilder.toString();
    }
}