package com.swna.server.product.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
        @NotBlank(message = "상품 코드는 필수입니다.")
        String code,

        String barcode,

        @NotBlank(message = "상품 설명은 필수입니다.")
        String description,

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @NotNull(message = "원가는 필수입니다.")
        @Min(value = 0, message = "원가는 0 이상이어야 합니다.")
        BigDecimal cost,

        BigDecimal priceOld,  // 선택 입력 (미입력 시 price 값으로 초기화)
        BigDecimal costOld,   // 선택 입력 (미입력 시 cost 값으로 초기화)

        String category,

        @Min(value = 0, message = "초기 재고 수량은 0 이상이어야 합니다.")
        int quantity,

        @Min(value = 0, message = "최소 재고 수량은 0 이상이어야 합니다.")
        int minStock,

        @Min(value = 0, message = "최대 재고 수량은 0 이상이어야 합니다.")
        int maxStock,

        @Min(value = 0, message = "최소 주문 수량은 0 이상이어야 합니다.")
        int minOrderQuantity
) {}