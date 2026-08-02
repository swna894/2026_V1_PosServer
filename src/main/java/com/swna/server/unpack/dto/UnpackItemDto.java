package com.swna.server.unpack.dto;

import java.time.LocalDateTime;

import com.swna.server.unpack.model.UnpackItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * UnpackItem CRUD 공용 DTO.
 * unpackId를 포함해 상위 Unpack과의 연관관계를 표현한다 (JsonIgnore된 unpack 객체 대신).
 * 필드가 20개 넘어 Lombok @Builder를 적용해 위치 기반 생성자 호출을 이름 기반으로 대체한다.
 */
@Builder
public record UnpackItemDto(
        Long id,
        LocalDateTime created,
        LocalDateTime updated,
        Long unpackId,
        String invoice,
        @NotBlank(message = "barcode must be provided") String barcode,
        String code,
        String description,
        String supplier,
        String category,
        String abbr,
        String comment,
        @NotNull(message = "qty must be provided") Integer qty,
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

        return UnpackItemDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .unpackId(entity.getUnpack() != null ? entity.getUnpack().getId() : null)
                .invoice(entity.getInvoice())
                .barcode(entity.getBarcode())
                .code(entity.getCode())
                .description(entity.getDescription())
                .supplier(entity.getSupplier())
                .category(entity.getCategory())
                .abbr(entity.getAbbr())
                .comment(entity.getComment())
                .qty(entity.getQty())
                .stock(entity.getStock())
                .minOrderQty(entity.getMinOrderQty())
                .minStock(entity.getMinStock())
                .amount(entity.getAmount())
                .pricein(entity.getPricein())
                .priceout(entity.getPriceout())
                .oldPricein(entity.getOldPricein())
                .confirm(entity.getConfirm())
                .isSaved(entity.getIsSaved())
                .isNew(entity.getIsNew())
                .build();
    }

    /**
     * 기존 레코드 갱신/병합(update)용: id를 포함해 엔티티로 변환한다.
     * saveAll() upsert 시나리오(예: 바코드 동기화 벌크 업데이트)에서 사용한다.
     */
    public UnpackItem toEntity() {
        return UnpackItem.builder()
                .id(id)
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