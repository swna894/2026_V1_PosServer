package com.swna.server.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 라벨 목록 조회 (id, code, company 포함)
     * GET /products/labels/full?limit=65 (limit 파라미터 선택 사항)
     */
    @GetMapping("/labels")
    public ApiResponse<List<ProductLabelDto>> getFullLabels(
            @RequestParam(name = "limit", defaultValue = "65") int limit
    ) {
        List<ProductLabelDto> labels = productService.getProductLabelsWithCompany(limit);
        return ApiResponse.success(labels);
    }

    /**
     * active Supplier의 상품만 조회
     * GET /products/labels/active-suppliers
     */
    @GetMapping("/labels/active-suppliers")
    public ApiResponse<List<ProductLabelDto>> getLabelsFromActiveSuppliers() {
        List<ProductLabelDto> labels = productService.getProductLabelsFromActiveSuppliers();
        return ApiResponse.success(labels);
    }

    /**
     * 특정 회사의 상품만 조회
     * GET /products/labels/company?company=ABC Trading
     */
    @GetMapping("/labels/company")
    public ApiResponse<List<ProductLabelDto>> getLabelsByCompany(
            @RequestParam String company
    ) {
        List<ProductLabelDto> labels = productService.getProductLabelsByCompany(company);
        return ApiResponse.success(labels);
    }

    /**
     * 바코드로 상품 라벨 조회 (id, code, company 포함)
     * GET /products/barcode/8801234567890/label
     */
    @GetMapping("/barcode/{barcode}/label")
    public ApiResponse<ProductLabelDto> getProductLabelByBarcode(
            @PathVariable("barcode") String barcode
    ) {
        ProductLabelDto label = productService.getProductLabelByBarcode(barcode);
        return ApiResponse.success(label);
    }

    /**
     * 바코드로 상품 상세 조회
     * GET /products/barcode/8801234567890
     */
    @GetMapping("/barcode/{barcode}")
    public ApiResponse<ProductResponse> getProductByBarcode(
            @PathVariable("barcode") String barcode
    ) {
        ProductResponse response = productService.getProductByBarcode(barcode);
        return ApiResponse.success(response);
    }
}