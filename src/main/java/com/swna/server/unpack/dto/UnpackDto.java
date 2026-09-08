package com.swna.server.unpack.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.swna.server.unpack.model.Unpack;

import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Unpack CRUD 공용 DTO.
 * - Create: id/created/updated는 null로 전달 (서버가 채움)
 * - Read: 전체 필드 채워서 응답
 * - Update: id 필수, 나머지는 null이 아닌 값만 반영 (Unpack#applyUpdate)
 * 클라이언트(JavaFX)의 com.swna.javafx.admin.unpacking.model.dto.UnpackDto 와
 * 필드/이름을 동일하게 유지해 JSON 계약을 맞춘다.
 * 필드가 많아 Lombok @Builder를 canonical constructor에 적용해 가독성을 높인다.
 */
@Builder
public record UnpackDto(
        Long id,
        LocalDateTime created,
        LocalDateTime updated,
        LocalDateTime unpacked,
        @NotBlank(message = "invoice must be provided") String invoice,
        String supplierAbbr,
        String comment,
        Double amount,
        Boolean sync,
        @Valid  List<UnpackItemDto> items
) {
    public static UnpackDto fromEntity(Unpack entity) {
        if (entity == null) return null;

        List<UnpackItemDto> itemDtos = entity.getItems() == null
                ? Collections.emptyList()
                : entity.getItems().stream()
                    .map(UnpackItemDto::fromEntity)
                    .toList();

        return UnpackDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .unpacked(entity.getUnpacked())
                .invoice(entity.getInvoice())
                .supplierAbbr(entity.getSupplierAbbr())
                .comment(entity.getComment())
                .amount(entity.getAmount())
                .sync(entity.getSync())
                .items(itemDtos)
                .build();
    }

    /**
     * 기존 레코드 갱신/병합(update)용: id를 포함해 엔티티로 변환한다.
     * saveAll() 같은 upsert 시나리오에서 id가 있어야 신규 삽입이 아닌 갱신으로 처리된다.
     * items는 이 메서드에서 변환하지 않으므로 호출부에서 unpack.addItem(...)으로 연결해야 한다.
     */
    @Nonnull 
    public Unpack toEntity() {
        return Unpack.builder()
                .id(id)
                .unpacked(unpacked)
                .invoice(invoice)
                .supplierAbbr(supplierAbbr)
                .comment(comment)
                .amount(amount)
                .sync(sync)
                .build();
    }

    /** Create 용: id/created/updated 없이 새 엔티티 생성 (items는 서비스 계층에서 addItem으로 연결) */
    public Unpack toNewEntity() {
        return Unpack.builder()
                .unpacked(unpacked)
                .invoice(invoice)
                .supplierAbbr(supplierAbbr)
                .comment(comment)
                .amount(amount)
                .sync(sync)
                .build();
    }
}