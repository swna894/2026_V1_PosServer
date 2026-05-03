package com.swna.server.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
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
     */
    public List<ProductLabelDto> getProductLabels() {
        // 1. 조회 결과를 Optional로 판단 (비어있으면 empty로 간주)
        return Optional.ofNullable(productRepository.findRandomProducts())
                .filter(products -> !products.isEmpty()) // 리스트가 비어있지 않을 때만 통과
                .map(products -> products.stream()
                        .map(this::convertToLabelDto)
                        .toList())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)); // 데이터가 없으면 예외 발생
    }

    /**
     * 바코드로 상품 조회
     */
    public ProductResponse getProductByBarcode(String barcode) {
        return productRepository
                .findProductResponseByBarcode(barcode)
                .orElseThrow(() -> 
                    // 조회 실패 시 BusinessException 발생
                    new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    private ProductLabelDto convertToLabelDto(Product product) {
        return new ProductLabelDto(
                product.getBarcode(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
