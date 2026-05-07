package com.swna.server.sale.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.sale.dto.response.PaymentResponse;
import com.swna.server.sale.dto.response.SaleItemResponse;
import com.swna.server.sale.dto.response.SaleResponse;
import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.entity.SaleItem;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaleMapper {
    private final ProductRepository productRepository;
    
    /**
     * Sale 엔티티 → SaleResponse DTO 변환
     */
    public SaleResponse toResponse(Sale sale) {
        return SaleResponse.from(sale);
    }
    
    /**
     * SaleItem → SaleItemResponse 변환
     */
    public SaleItemResponse toSaleItemResponse(SaleItem item) {
        Long productId = item.getProductId();
        if (productId == null) {
            throw new IllegalArgumentException(
                String.format("Product ID is null - Barcode: %s", item.getBarcode())
            );
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("Product not found - ID: %d, Barcode: %s", 
                        productId, item.getBarcode())
                ));
        
        return SaleItemResponse.from(item, product.getDescription());
    }
}