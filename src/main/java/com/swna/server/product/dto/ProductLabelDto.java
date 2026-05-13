package com.swna.server.product.dto;

import java.math.BigDecimal;

/**
 * 바코드 라벨 출력을 위한 상품 DTO
 * @param id 상품 ID
 * @param barcode 바코드
 * @param company 제조사/회사명 (supplier.company)
 * @param code 상품 코드
 * @param description 상품명
 * @param price 가격
 */
public record ProductLabelDto(
    Long id, 
    String barcode, 
    String company, 
    String code, 
    String description, 
    BigDecimal price
) {
    /**
     * 전체 필드 생성자 (null safety 적용)
     */
    public ProductLabelDto {
        // null safety 처리
        barcode = barcode != null ? barcode : "";
        company = company != null ? company : "";
        code = code != null ? code : "";
        description = description != null ? description : "";
        price = price != null ? price : BigDecimal.ZERO;
    }
    
    /**
     * 기본 정보만 있는 간소화 버전 생성
     */
    public ProductLabelDto(String barcode, String description, BigDecimal price) {
        this(null, barcode, "", "", description, price);
    }
    
    /**
     * company를 포함한 간소화 버전 생성
     */
    public ProductLabelDto(String barcode, String description, BigDecimal price, String company) {
        this(null, barcode, company, "", description, price);
    }
}