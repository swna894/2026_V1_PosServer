package com.swna.server.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 라벨 출력을 위한 상품 목록 조회 (id, code, company 포함)
     * Product와 Supplier를 abbr로 조인하여 company 정보를 함께 반환합니다.
     */
    public List<ProductLabelDto> getProductLabelsWithCompany() {
        List<ProductLabelDto> labels = productRepository.findRandomProductLabelsWithSupplier();
        if (labels == null || labels.isEmpty()) {
            throw ExceptionUtils.productNotFound("No products found in database", "findRandomProductLabelsWithSupplier");
        }
        return labels;
    }

    /**
     * 라벨 출력을 위한 상품 목록 조회 (제한된 개수)
     */
    public List<ProductLabelDto> getProductLabelsWithCompany(int limit) {
        PageRequest pageable = PageRequest.of(0, limit);

        List<ProductLabelDto> labels = productRepository.findRandomProductLabelsWithSupplierLimit(pageable);
        if (labels == null || labels.isEmpty()) {
            throw ExceptionUtils.productNotFound("No products found in database", "findRandomProductLabelsWithSupplier");
        }
        return labels;
    }

    /**
     * active 상태인 Supplier의 상품만 조회
     */
    public List<ProductLabelDto> getProductLabelsFromActiveSuppliers() {
        List<ProductLabelDto> labels = productRepository.findRandomProductLabelsFromActiveSuppliers();
        if (labels == null || labels.isEmpty()) {
            throw ExceptionUtils.productNotFound("No products found from active suppliers", "findRandomProductLabelsFromActiveSuppliers");
        }
        return labels;
    }

    /**
     * 특정 회사(company)의 상품만 조회
     */
    public List<ProductLabelDto> getProductLabelsByCompany(String company) {
        if (company == null || company.isBlank()) {
            return List.of();
        }
        return productRepository.findProductLabelsByCompany(company);
    }

    /**
     * 특정 abbr 목록의 상품 조회
     */
    public List<ProductLabelDto> getProductLabelsByAbbrs(List<String> abbrs) {
        if (abbrs == null || abbrs.isEmpty()) {
            return List.of();
        }
        return productRepository.findProductLabelsByAbbrs(abbrs);
    }

    /**
     * 바코드로 상품 라벨 DTO 조회 (id, code, company 포함)
     */
    public ProductLabelDto getProductLabelByBarcode(String barcode) {
        validateBarcode(barcode);
        
        return productRepository.findProductLabelByBarcode(barcode)
                .orElseThrow(() -> ExceptionUtils.productNotFound(barcode));
    }


    /**
     * 바코드로 상품 조회 (상세 정보 포함)
     */
    public ProductResponse getProductByBarcode(String barcode) {
        validateBarcode(barcode);
        
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
}