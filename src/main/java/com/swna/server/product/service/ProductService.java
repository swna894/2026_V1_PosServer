package com.swna.server.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 라벨 출력을 위한 상품 목록을 조회하고 DTO로 변환합니다.
     * 랜덤 상품 라벨 목록 조회 (Optional 버전)
     */
    public List<ProductLabelDto> getProductLabels() {
        return Optional.ofNullable(productRepository.findRandomProducts())
                .filter(products -> !products.isEmpty())
                .map(products -> products.stream()
                        .map(this::convertToLabelDto)
                        .toList())
                .orElseThrow(() -> 
                    ExceptionUtils.productNotFound("No products found in database", "findRandomProducts")
                );
    }

    /**
     * 바코드로 상품 조회 (상세 정보 포함)
     */
    public ProductResponse getProductByBarcode(String barcode) {
        // 1. 바코드 유효성 검증 (간결해짐!)
        validateBarcode(barcode);
        
        // 2. 상품 조회 (매우 간결해짐!)
        return productRepository
                .findProductResponseByBarcode(barcode)
                .orElseThrow(() -> ExceptionUtils.productNotFound(barcode));
    }

    private void validateBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            throw BusinessException.builder(ErrorCode.INVALID_INPUT)
                .message("Barcode cannot be empty")
                .detail("field", "barcode")
                .build();
        }
    }

    private ProductLabelDto convertToLabelDto(Product product) {
        return new ProductLabelDto(
                product.getBarcode(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
