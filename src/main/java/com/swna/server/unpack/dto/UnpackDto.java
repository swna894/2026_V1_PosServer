package com.swna.server.unpack.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.swna.server.unpack.model.Unpack;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Unpack CRUD 공용 DTO.
 * - Create: id/created/updated는 null로 전달 (서버가 채움)
 * - Read: 전체 필드 채워서 응답
 * - Update: id 필수, 나머지는 null이 아닌 값만 반영 (Unpack#applyUpdate)
 * 클라이언트(JavaFX)의 com.swna.javafx.admin.unpacking.model.dto.UnpackDto 와
 * 필드/이름을 동일하게 유지해 JSON 계약을 맞춘다.
 */
public record UnpackDto(
        Long id,
        LocalDateTime created,
        LocalDateTime updated,
        LocalDateTime unpacked,
        @NotBlank(message = "invoice는 필수입니다") String invoice,
        String supplierAbbr,
        String comment,
        Double amount,
        Boolean sync,
        @Valid List<UnpackItemDto> items
) {
    public static UnpackDto fromEntity(Unpack entity) {
        if (entity == null) return null;

        List<UnpackItemDto> itemDtos = entity.getItems() == null
                ? Collections.emptyList()
                : entity.getItems().stream()
                    .map(UnpackItemDto::fromEntity)
                    .collect(Collectors.toList());

        return new UnpackDto(
                entity.getId(),
                entity.getCreated(),
                entity.getUpdated(),
                entity.getUnpacked(),
                entity.getInvoice(),
                entity.getSupplierAbbr(),
                entity.getComment(),
                entity.getAmount(),
                entity.getSync(),
                itemDtos
        );
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
