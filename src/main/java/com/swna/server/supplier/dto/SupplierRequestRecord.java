package com.swna.server.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SupplierRequestRecord(
    
    @NotBlank(message = "Supplier abbreviation is required")
    @Size(min = 2, max = 8, message = "Supplier abbreviation must be between 2 and 8 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Supplier abbreviation must contain only uppercase letters and numbers")
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
    // Compact constructor for default value handling
    public SupplierRequestRecord {
        company = nullToEmpty(company);
        email = nullToEmpty(email);
        phone = nullToEmpty(phone);
        address = nullToEmpty(address);
        cellphone = nullToEmpty(cellphone);
    }
    
    private static String nullToEmpty(String value) {
        return value != null ? value : "";
    }
    
    // Static factory method
    public static SupplierRequestRecord of(String abbr, String name) {
        return new SupplierRequestRecord(abbr, name, "", "", "", "", "");
    }
    
    // Validation helper methods
    public boolean hasCompany() {
        return company != null && !company.isBlank();
    }
    
    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
    
    public boolean hasPhone() {
        return phone != null && !phone.isBlank();
    }
    
    public boolean hasCellphone() {
        return cellphone != null && !cellphone.isBlank();
    }
    
    public boolean hasAddress() {
        return address != null && !address.isBlank();
    }
}