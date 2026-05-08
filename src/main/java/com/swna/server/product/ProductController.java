package com.swna.server.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 라벨 목록 조회
     */
    @GetMapping("/labels")
    public ApiResponse<List<ProductLabelDto>> getLabels() {
        // Service 레이어 호출 결과를 ApiResponse.success로 감싸서 반환
        List<ProductLabelDto> labels = productService.getProductLabels();
        return ApiResponse.success(labels);
    }

    /**
     * 바코드로 상품 조회
     *
     * example:
     * GET /products/barcode/8801234567890
     */
    @GetMapping("/barcode/{barcode}")
    public ApiResponse<ProductResponse> getProductByBarcode(
            @PathVariable("barcode") String barcode
    ) {
        // 조회된 상품 정보를 ApiResponse.success로 감싸서 반환
        ProductResponse response = productService.getProductByBarcode(barcode);
        return ApiResponse.success(response);
    }
}
