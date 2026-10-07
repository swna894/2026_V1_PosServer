package com.swna.server.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ShopDto(
        @NotBlank String company,
        @NotBlank String businessNo,
        @NotBlank String name,
        @Email String email,
        @NotBlank String password,
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
}
