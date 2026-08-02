package com.swna.server.unpack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.common.response.ApiResponse;
import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.unpack.dto.UnpackItemDto;
import com.swna.server.unpack.model.Unpack;
import com.swna.server.unpack.model.UnpackItem;
import com.swna.server.unpack.repository.UnpackItemRepository;
import com.swna.server.unpack.repository.UnpackRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ApiResponse<T> + BusinessException/ErrorCode 예외 체계 적용 리팩토링.
 * - 모든 응답을 ApiResponse.success(data)로 감싸서 일관된 응답 포맷 제공.
 * - 기존에는 대상이 없으면 null을 그대로 반환했는데, 이제 ExceptionUtils.resourceNotFound(...)로
 *   BusinessException(ErrorCode.RESOURCE_NOT_FOUND)을 던지고 GlobalExceptionHandler가
 *   404 + ApiResponse.error로 변환한다 (동작이 "조용한 null 반환"에서 "명시적 에러 응답"으로
 *   바뀌는 부분이니 클라이언트 쪽 null 체크 로직이 있다면 확인이 필요하다).
 * - @Valid로 UnpackItemDto의 @NotBlank(barcode)/@NotNull(qty) 검증이 실제로 동작하도록 했다.
 * - Product 동기화/생성 비즈니스 로직은 동작 변경 없이 그대로 유지.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RestUnpackItemController {

    private final ProductRepository productRepository;
    private final UnpackRepository unpackRepository;
    private final UnpackItemRepository unpackItemRepository;

    @PutMapping("/unpackItem")
    public ResponseEntity<ApiResponse<UnpackItemDto>> put(@Valid @RequestBody UnpackItemDto dto) {
        if (dto.id() == null) {
            throw ExceptionUtils.invalidInput("id", "수정 요청에는 id가 반드시 포함되어야 합니다.");
        }

        UnpackItem findedItem = unpackItemRepository.findById(dto.id())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("UnpackItem", String.valueOf(dto.id())));

        findedItem.setQty(dto.qty());
        findedItem.setConfirm(dto.confirm());
        findedItem.setComment(dto.comment());
        findedItem.setAmount(dto.amount());
        findedItem.setPriceout(dto.priceout());
        findedItem.setBarcode(dto.barcode());
        findedItem.setDescription(dto.description());

        UnpackItem saved = unpackItemRepository.save(findedItem);

        return ResponseEntity.ok(ApiResponse.success(UnpackItemDto.fromEntity(saved)));
    }


    @PutMapping("/unpackItems")
    @Transactional
    public ResponseEntity<ApiResponse<List<UnpackItemDto>>> puts(@Valid @RequestBody List<UnpackItemDto> dtos) {
        // 기존 레코드 갱신이 섞여 있을 수 있으므로 id 포함 변환 (toEntity)
        List<UnpackItem> items = dtos.stream()
                .map(UnpackItemDto::toEntity)
                .collect(Collectors.toList());

        List<Product> products = new ArrayList<>();

        for (UnpackItem item : items) {
            Product product = productRepository.findByBarcode(item.getBarcode()).orElse(null);

            if (product != null) {
                product = updateProduct(item, product);
            } else {
                // Product 생성 팩토리 메서드 활용
                BigDecimal price = item.getPriceout() != null ? BigDecimal.valueOf(item.getPriceout()) : BigDecimal.ZERO;
                BigDecimal cost = item.getPricein() != null ? BigDecimal.valueOf(item.getPricein()) : BigDecimal.ZERO;

                product = Product.create(
                    item.getCode() != null ? item.getCode() : "TEMP_CODE",
                    item.getDescription() != null ? item.getDescription() : "No Description",
                    price,
                    cost,
                    item.getBarcode(),
                    item.getCategory()
                );
            }
            products.add(product);
        }
        products = productRepository.saveAll(products);

        Unpack unpack = null;
        if (!items.isEmpty() && items.get(0).getInvoice() != null) {
            unpack = unpackRepository.findByInvoice(items.get(0).getInvoice());
        }

        for (Product product : products) {
            UnpackItem item = items.stream()
                    .filter(i -> i.getBarcode() != null && i.getBarcode().equals(product.getBarcode()))
                    .findAny()
                    .orElse(null);

            if (item != null) {
                item.setPriceout(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
                item.setDescription(product.getDescription());
                item.setAbbr(product.getAbbr());
                item.setIsSaved(true);
                if (unpack != null) {
                    item.setUnpack(unpack);
                }
            }
        }

        List<UnpackItemDto> result = unpackItemRepository.saveAll(items).stream()
                .map(UnpackItemDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private Product updateProduct(UnpackItem item, Product product) {
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            product.changeName(item.getDescription());
        }
        if (item.getPriceout() != null) {
            product.changePrice(BigDecimal.valueOf(item.getPriceout()));
        }
        if (item.getCategory() != null) {
            product.changeCategory(item.getCategory());
        }
        return product;
    }
}