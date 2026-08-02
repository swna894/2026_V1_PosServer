package com.swna.server.unpack.dto;

import java.time.LocalDateTime;

import com.swna.server.unpack.model.UnpackItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * UnpackItem CRUD 공용 DTO.
 * unpackId를 포함해 상위 Unpack과의 연관관계를 표현한다 (JsonIgnore된 unpack 객체 대신).
 */
public record UnpackItemDto(
        Long id,
        LocalDateTime created,
        LocalDateTime updated,
        Long unpackId,
        String invoice,
        @NotBlank(message = "barcode는 필수입니다") String barcode,
        String code,
        String description,
        String supplier,
        String category,
        String abbr,
        String comment,
        @NotNull(message = "qty는 필수입니다") Integer qty,
        Integer stock,
        Integer minOrderQty,
        Integer minStock,
        Double amount,
        Double pricein,
        Double priceout,
        Double oldPricein,
        Boolean confirm,
        Boolean isSaved,
        Boolean isNew
) {
    public static UnpackItemDto fromEntity(UnpackItem entity) {
        if (entity == null) return null;

        return new UnpackItemDto(
                entity.getId(),
                entity.getCreated(),
                entity.getUpdated(),
                entity.getUnpack() != null ? entity.getUnpack().getId() : null,
                entity.getInvoice(),
                entity.getBarcode(),
                entity.getCode(),
                entity.getDescription(),
                entity.getSupplier(),
                entity.getCategory(),
                entity.getAbbr(),
                entity.getComment(),
                entity.getQty(),
                entity.getStock(),
                entity.getMinOrderQty(),
                entity.getMinStock(),
                entity.getAmount(),
                entity.getPricein(),
                entity.getPriceout(),
                entity.getOldPricein(),
                entity.getConfirm(),
                entity.getIsSaved(),
                entity.getIsNew()
        );
    }

    /** Create 용: id 없이 새 엔티티 생성. unpack 연관관계는 Unpack#addItem에서 설정 */
    public UnpackItem toNewEntity() {
        return UnpackItem.builder()
                .invoice(invoice)
                .barcode(barcode)
                .code(code)
                .description(description)
                .supplier(supplier)
                .category(category)
                .abbr(abbr)
                .comment(comment)
                .qty(qty)
                .stock(stock)
                .minOrderQty(minOrderQty)
                .minStock(minStock)
                .amount(amount)
                .pricein(pricein)
                .priceout(priceout)
                .oldPricein(oldPricein)
                .confirm(confirm)
                .isSaved(isSaved)
                .isNew(isNew)
                .build();
    }
}
