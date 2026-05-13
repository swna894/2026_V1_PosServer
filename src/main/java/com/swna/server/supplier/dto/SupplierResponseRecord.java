package com.swna.server.supplier.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swna.server.supplier.entity.Supplier;

import java.time.LocalDateTime;
import java.util.List;

public record SupplierResponseRecord(
    Long id,
    String abbr,
    String name,
    String company,
    String email,
    String phone,
    String cellphone,
    String address,
    boolean active
) {
    // 단건 변환
    public static SupplierResponseRecord from(Supplier supplier) {
        if (supplier == null) return null;
        
        return new SupplierResponseRecord(
            supplier.getId(),
            supplier.getAbbr(),
            supplier.getName(),
            supplier.getCompany(),
            supplier.getEmail(),
            supplier.getPhone(),
            supplier.getCellphone(),
            supplier.getAddress(),
            supplier.isActive()
        );
    }
    
    // 리스트 변환
    public static List<SupplierResponseRecord> from(List<Supplier> suppliers) {
        if (suppliers == null) return List.of();
        
        return suppliers.stream()
                .map(SupplierResponseRecord::from)
                .toList();
    }
    
    // with 메서드들 (불변 객체 수정용)
    public SupplierResponseRecord withActive(boolean active) {
        return new SupplierResponseRecord( id, abbr, name, company, email, phone, cellphone, address,  active );
    }
}