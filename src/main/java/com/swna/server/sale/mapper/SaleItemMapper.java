package com.swna.server.sale.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.product.entity.Product;
import com.swna.server.product.entity.ProductStock;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.product.repository.ProductStockRepository;
import com.swna.server.sale.dto.request.SaleItemRequest;
import com.swna.server.sale.entity.SaleItem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaleItemMapper {
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    
    @PersistenceContext
    private EntityManager entityManager; 

    public SaleItem toEntity(SaleItemRequest request) {
        // request null 체크
        if (request == null) {
            throw ExceptionUtils.missingField("SaleItemRequest");
        }
        
        Product product = productRepository.findByBarcode(request.barcode())
                .orElseGet(() -> handleProductNotFound(request));
        
        return SaleItem.of(product, request);
    }


  
 // ===================================================
// 2. 상품을 찾거나 생성하는 메인 메서드
// ===================================================
/**
 * 바코드로 상품을 조회하고, 없으면 QUICK_ 으로 시작하는 경우 임시 상품 생성
 * 
 * @param request 판매 요청 정보 (바코드, 가격, 수량 등)
 * @return 찾거나 생성한 Product 엔티티
 * @throws BusinessException 상품이 없거나 생성 실패 시
 */
@Transactional  // ⚠️ 주의: private 메서드가 아닌 public 메서드에 선언해야 실제 동작함
public Product handleProductNotFound(SaleItemRequest request) {
    String barcode = request.barcode();
    
    // 1) 바코드 null 체크
    if (barcode == null) {
        throw ExceptionUtils.missingField("barcode");
    }
    
    // 2) QUICK_ 으로 시작하는 바코드면 임시 상품 생성
    if (barcode.startsWith("QUICK_")) {
        try {
            return createQuickProduct(request);
        } catch (Exception e) {
            throw ExceptionUtils.internalError("Error creating temporary product: %s", e.getMessage());
        }
    }
    
    // 3) 일반 상품도 없는 경우 (기존 비즈니스 로직)
    throw ExceptionUtils.productNotFound(barcode);
}

    // ===================================================
    // 3. QUICK_ 임시 상품 생성 핵심 로직
    // ===================================================
    /**
     * QUICK_ 으로 시작하는 바코드에 대한 임시 상품 생성
     * - Category는 "temp"로 설정
     * - ProductStock은 재고 0으로 생성
     * 
     * @param request 판매 요청 정보
     * @return 생성되어 저장된 Product (Category, Stock 포함)
     */
    @SuppressWarnings("null")
    private Product createQuickProduct(@NonNull SaleItemRequest request) {
        
        // ===================================================
        // Step 1: 요청 데이터 추출 및 검증
        // ===================================================
        String barcode = request.barcode();
        BigDecimal originalPrice = request.originalPrice();   // 정가
        BigDecimal sellingPrice = request.sellingPrice();     // 판매가 (할인 적용가)
        //BigDecimal discountValue = request.discountValue();   // 할인 금액
        
        // originalPrice 필수 검증
        if (originalPrice == null) {
            throw ExceptionUtils.missingField("originalPrice");
        }
        
        // 가격 음수 검증
        if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw ExceptionUtils.invalidInputWithValue(
                "originalPrice", 
                originalPrice, 
                "price must be greater than or equal to 0"
            );
        }
        
        // ===================================================
        // Step 2: Category 처리 ("temp" 카테고리 확보)
        // ===================================================
        final String TEMP_CATEGORY_NAME = "temp";
        
        // ===================================================
        // Step 3: 임시 상품 정보 생성
        // ===================================================
        String tempCode = "QUICK_" + System.currentTimeMillis();  // 고유 코드 생성
        String description = String.format("Temporary Product - %.2f", originalPrice);
        
        // 원가(cost) 설정: 판매가가 있으면 판매가, 없으면 정가로 설정
        BigDecimal cost = (sellingPrice != null && sellingPrice.compareTo(BigDecimal.ZERO) > 0) 
                        ? sellingPrice 
                        : originalPrice;

        
        // ===================================================
        // Step 4: Product 엔티티 생성 및 저장
        // ===================================================
    
            // ⚠️ 주의: Product.create() 메서드는 Category를 String으로 받음
            // Category 엔티티 연결이 필요하면 Product 엔티티 수정 필요
        Product product = Product.create(
                tempCode,           // 상품 코드
                description,        // 상품명
                originalPrice,      // 판매가 (price)
                cost,              // 원가 (cost)
                barcode,           // 바코드
                TEMP_CATEGORY_NAME  // 카테고리명 (String)
            );
            
        
        // Product 저장
        Product savedProduct = productRepository.save(product);
        
        // ===================================================
        // Step 5: ProductStock 생성 및 저장 (재고 0)
        // ===================================================
        // 💡 재고 관련 설정값 설명:
        // - initialQty: 0 (초기 재고 없음)
        // - minStock: 5 (최소 안전 재고)
        // - maxStock: 100 (최대 재고 한도)
        // - minOrderQuantity: 12 (최소 발주 수량)
        ProductStock stock = ProductStock.create(
            savedProduct,  // 연관된 Product
            0,             // 초기 재고량 (0으로 시작)
            5,             // 최소 재고 경고 기준
            100,           // 최대 재고 한도
            12             // 최소 발주 수량
        );
        
        productStockRepository.save(stock);
        
        // ===================================================
        // Step 6: (선택사항) 강제 flush로 DB 반영 확인
        // ===================================================
        entityManager.flush();
        

        
        return savedProduct;
    }

}