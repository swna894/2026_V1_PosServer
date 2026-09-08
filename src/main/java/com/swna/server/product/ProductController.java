package com.swna.server.product;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.product.dto.ProductCreateRequest;
import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.dto.ProductUpdateRequest;
import com.swna.server.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ==========================================
    // Create
    // ==========================================
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.success(productService.createProduct(request));
    }

    // ==========================================
    // Read
    // ==========================================
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id) {
        return ApiResponse.success(productService.getProductById(id));
    }

    @GetMapping("/barcode/{barcode}")
    public ApiResponse<ProductResponse> getProductByBarcode(@PathVariable("barcode") String barcode) {
        return ApiResponse.success(productService.getProductByBarcode(barcode));
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.success(productService.getAllProducts());
    }

    @GetMapping("/abbr/{abbr}")
    public ApiResponse<List<ProductResponse>> getProductsByAbbr(@PathVariable("abbr") String abbr) {
        return ApiResponse.success(productService.getProductsByAbbr(abbr));
    }

    @GetMapping("/labels")
    public ApiResponse<List<ProductLabelDto>> getProductLabels(
            @RequestParam(name = "company", required = false) String company,
            @RequestParam(name = "abbrs", required = false) List<String> abbrs,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly
    ) {
        if (company != null && !company.isBlank()) {
            return ApiResponse.success(productService.getProductLabelsByCompany(company));
        }
        if (abbrs != null && !abbrs.isEmpty()) {
            return ApiResponse.success(productService.getProductLabelsByAbbrs(abbrs));
        }
        if (activeOnly) {
            return ApiResponse.success(productService.getProductLabelsFromActiveSuppliers());
        }
        if (limit != null && limit > 0) {
            return ApiResponse.success(productService.getProductLabelsWithCompany(limit));
        }
        return ApiResponse.success(productService.getProductLabelsWithCompany());
    }

    // ==========================================
    // Update
    // ==========================================
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        return ApiResponse.success(productService.updateProduct(id, request));
    }

    @PutMapping("/bulk")
    public ApiResponse<List<ProductResponse>> updateProductsBulk(
            @Valid @RequestBody List<ProductUpdateRequest> requests
    ) {
        return ApiResponse.success(productService.updateProductsBulk(requests));
    }

    /**
     * 🔥 주문일자(lastOrderedAt) 단독 변경 API
     * 예시: PATCH /api/v1/products/1/last-ordered-at?orderedAt=2026-09-08T10:00:00
     */
    @PatchMapping("/{id}/last-ordered-at")
    public ApiResponse<ProductResponse> updateLastOrderedAt(
            @PathVariable("id") Long id,
            @RequestParam(name = "orderedAt", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime orderedAt
    ) {
        return ApiResponse.success(productService.updateLastOrderedAt(id, orderedAt));
    }

    // ==========================================
    // Delete
    // ==========================================
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/bulk")
    public ApiResponse<Integer> deleteProductsBulk(@RequestBody List<Long> ids) {
        return ApiResponse.success(productService.deleteProductsBulk(ids));
    }
}