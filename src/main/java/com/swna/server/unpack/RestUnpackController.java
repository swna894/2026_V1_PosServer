package com.swna.server.unpack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.common.response.ApiResponse;
import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.unpack.dto.UnpackDto;
import com.swna.server.unpack.model.Unpack;
import com.swna.server.unpack.model.UnpackItem;
import com.swna.server.unpack.repository.UnpackRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ApiResponse<T> + BusinessException/ErrorCode 예외 체계 적용 리팩토링.
 * - 모든 응답을 ApiResponse.success(data)로 감싸서 일관된 응답 포맷 제공.
 * - "찾을 수 없음" 등 비즈니스 예외는 ExceptionUtils로 BusinessException을 던지면
 *   기존에 정의된 GlobalExceptionHandler + ErrorResponseFactory가 ApiResponse.error로 변환한다.
 * - @Valid를 추가해 UnpackDto의 @NotBlank(invoice) 검증이 실제로 동작하도록 했다.
 * - Product 바코드 동기화 비즈니스 로직(isNewProduct)은 동작 변경 없이 그대로 유지.
 */
@RestController
@RequestMapping("/api/unpack")
@RequiredArgsConstructor
public class RestUnpackController {

    private final ProductRepository productRepository;
    private final UnpackRepository unpackRepository;

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<ApiResponse<UnpackDto>> post(@Valid @RequestBody UnpackDto dto) {
        Unpack unpack = dto.toNewEntity();

        dto.items().forEach(itemDto -> {
            UnpackItem item = itemDto.toNewEntity();
            unpack.addItem(item);
            isNewProduct(item, unpack.getSync());
        });

        Unpack saved = unpackRepository.save(unpack);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(UnpackDto.fromEntity(saved)));
    }

    private void isNewProduct(UnpackItem item, Boolean sync) {
        // Optional 처리
        Product product = productRepository.findByBarcode(item.getBarcode()).orElse(null);

        if (product != null) {
            // Product 엔티티 구조에 맞추어 price 사용 (BigDecimal -> Double 변환)
            item.setPriceout(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);

            // 기본값 설정 (ProductStock 연동 전 fallback 수치)
            item.setMinOrderQty(12);
            item.setMinStock(6);

            if (sync == null || !sync) {
                item.setAbbr(product.getAbbr());
            }
            item.setIsNew(false);
        } else {
            item.setIsNew(true);
            item.setMinOrderQty(12);
            item.setMinStock(6);
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<List<UnpackDto>>> put(@Valid @RequestBody List<UnpackDto> dtos) {
        List<Unpack> unpacks = dtos.stream().map(dto -> {
            if (dto.id() == null) {
                // 갱신 대상은 id가 필수이므로, 없는 경우 명확한 400 에러로 안내
                throw ExceptionUtils.invalidInput("id", "수정 요청에는 id가 반드시 포함되어야 합니다.");
            }
            // 기존 레코드 갱신이므로 id 포함 변환 (toEntity) -> saveAll이 upsert로 처리
            Unpack unpack = dto.toEntity();
            if (dto.items() != null) {
                dto.items().forEach(itemDto -> unpack.addItem(itemDto.toEntity()));
            }
            return unpack;
        }).collect(Collectors.toList());

        List<UnpackDto> result = unpackRepository.saveAll(unpacks).stream()
                .map(UnpackDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UnpackDto>>> getLocalDate(
            @RequestParam Map<String, String> parameter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start;
        LocalDateTime end;
        try {
            start = LocalDate.parse(parameter.get("start"), formatter).atStartOfDay();
            end = LocalDate.parse(parameter.get("end"), formatter).atTime(23, 59, 59);
        } catch (Exception e) {
            throw ExceptionUtils.invalidInput("start/end", "yyyy-MM-dd 형식이어야 합니다.");
        }

        // UnpackRepository에 선언된 올바른 메서드명 호출
        List<UnpackDto> result = unpackRepository.findByUpdatedBetweenOrderByUpdatedDesc(start, end)
                .stream()
                .map(UnpackDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deletes(@RequestBody List<UnpackDto> dtos) {
        for (UnpackDto dto : dtos) {
            if (dto.invoice() != null) {
                unpackRepository.deleteByInvoice(dto.invoice());
            }
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}