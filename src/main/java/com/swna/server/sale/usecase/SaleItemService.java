package com.swna.server.sale.usecase;

import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.sale.dto.response.SaleItemResponse;
import com.swna.server.sale.entity.SaleItem;
import com.swna.server.sale.repository.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleItemService {

    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;

    /**
     * 특정 saleId에 해당하는 SaleItem 목록을 조회하여 SaleItemResponse DTO 리스트로 변환합니다.
     */
    public List<SaleItemResponse> getSaleItemsBySaleId(Long saleId) {
        List<SaleItem> saleItems = saleItemRepository.findBySaleId(saleId);

        return saleItems.stream()
                .map(item -> {
                    // 1. 해당 상품(Product) 정보를 조회합니다.
                    Product product = productRepository.findById(item.getProductId())
                            .orElse(null);

                    // 2. Product 정보가 있으면 매핑하고, 없으면 기본값(Fallback)을 처리합니다.
                    BigDecimal cost = (product != null) ? product.getCost() : item.getCost(); 
                    String supplier = (product != null) ? product.getAbbr() : "알 수 없는 공급처"; 
                    // ※ 만약 상품명(productName) 필드가 레코드에 다시 추가된다면 여기서 product.getName()을 쓰시면 됩니다.

                    // 3. 새로 제시해주신 from 메서드 규격에 맞추어 명시적으로 return 합니다.
                    return SaleItemResponse.from(item, cost, supplier);
                })
                .collect(Collectors.toList());
    }
}