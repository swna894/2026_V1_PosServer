package com.swna.server.supplier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SupplierRequestRecord(

    // 대문자, 숫자, 특수문자(ASCII 기호) 허용. 공백/한글은 불가. (클라이언트 ABBR_REGEX 와 동일)
    @NotBlank(message = "Supplier abbreviation is required")
    @Size(min = 2, max = 8, message = "Supplier abbreviation must be between 2 and 8 characters")
    @Pattern(regexp = "^[A-Z0-9\\p{Punct}]+$",
             message = "Supplier abbreviation must contain only uppercase letters, numbers and special characters (no spaces)")
    String abbr,

    @NotBlank(message = "Contact person name is required")
    @Size(max = 32, message = "Contact person name must not exceed 32 characters")
    String name,

    @Size(max = 64, message = "Company name must not exceed 64 characters")
    String company,

    @Size(max = 64, message = "Email must not exceed 64 characters")
    @Email(message = "Invalid email format")
    String email,

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phone,

    @Size(max = 128, message = "Address must not exceed 128 characters")
    String address,

    @Size(max = 20, message = "Cellphone number must not exceed 20 characters")
    String cellphone
) {
    // Compact constructor: 공백 제거 + 기본값 처리
    // 검증(@Valid)은 이 생성자가 끝난 뒤의 값을 대상으로 하므로, 앞뒤 공백이나 소문자 입력도 정규화된 값으로 검사된다.
    public SupplierRequestRecord {
        abbr = normalizeAbbr(abbr);
        name = trimOrNull(name);          // null 이면 @NotBlank 가 잡도록 null 유지
        company = nullToEmpty(company);
        email = nullToEmpty(email);
        phone = nullToEmpty(phone);
        address = nullToEmpty(address);
        cellphone = nullToEmpty(cellphone);
    }

    // ===== 정규화 헬퍼 =====

    /** 앞뒤 공백 제거 후 대문자로 변환. null 은 그대로 둔다. */
    private static String normalizeAbbr(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private static String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    // ===== Static factory method =====
    public static SupplierRequestRecord of(String abbr, String name) {
        return new SupplierRequestRecord(abbr, name, "", "", "", "", "");
    }

    // ===== 편의 메서드 =====
    public boolean hasCompany() {
        return !company.isBlank();
    }

    public boolean hasEmail() {
        return !email.isBlank();
    }

    public boolean hasPhone() {
        return !phone.isBlank();
    }

    public boolean hasCellphone() {
        return !cellphone.isBlank();
    }

    public boolean hasAddress() {
        return !address.isBlank();
    }
}
