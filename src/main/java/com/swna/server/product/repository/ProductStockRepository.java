package com.swna.server.product.repository;

import com.swna.server.product.entity.Product;
import com.swna.server.product.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 상품 재고 Repository
 * 
 * @author SWNA
 * @version 1.0
 */
@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    
    // ===================================================
    // 기본 조회 메서드
    // ===================================================
    
    /**
     * 특정 상품의 재고 정보 조회
     * 
     * @param product 상품 엔티티
     * @return Optional<ProductStock>
     */
    Optional<ProductStock> findByProduct(Product product);
    
    /**
     * 특정 상품 ID로 재고 정보 조회
     * 
     * @param productId 상품 ID
     * @return Optional<ProductStock>
     */
    @Query("SELECT ps FROM ProductStock ps WHERE ps.product.id = :productId")
    Optional<ProductStock> findByProductId(@Param("productId") Long productId);
    
    /**
     * 재고 부족 상품 조회 (quantity <= minStock)
     * 
     * @return 재고 부족 상품 목록
     */
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity <= ps.minStock")
    List<ProductStock> findLowStockProducts();
    
    /**
     * 품절 상품 조회 (quantity == 0)
     * 
     * @return 품절 상품 목록
     */
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity = 0")
    List<ProductStock> findOutOfStockProducts();
    
    /**
     * 특정 재고 수량 이하인 상품 조회
     * 
     * @param threshold 기준 수량
     * @return 재고 부족 상품 목록
     */
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity <= :threshold")
    List<ProductStock> findProductsWithStockLessThan(@Param("threshold") int threshold);
    
    // ===================================================
    // 재고 업데이트 메서드 (벌크 연산)
    // ===================================================
    
    /**
     * 상품 재고 증가
     * 
     * @param productId 상품 ID
     * @param amount 증가할 수량
     * @return 업데이트된 행 수
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductStock ps SET ps.quantity = ps.quantity + :amount WHERE ps.product.id = :productId")
    int increaseStock(@Param("productId") Long productId, @Param("amount") int amount);
    
    /**
     * 상품 재고 감소 (동시성 제어 필요 시 version 필드 추가 권장)
     * 
     * @param productId 상품 ID
     * @param amount 감소할 수량
     * @return 업데이트된 행 수
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductStock ps SET ps.quantity = ps.quantity - :amount WHERE ps.product.id = :productId AND ps.quantity >= :amount")
    int decreaseStock(@Param("productId") Long productId, @Param("amount") int amount);
    
    /**
     * 재고 설정값 업데이트
     * 
     * @param productId 상품 ID
     * @param minStock 최소 재고
     * @param maxStock 최대 재고
     * @param minOrderQuantity 최소 발주 수량
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductStock ps SET ps.minStock = :minStock, ps.maxStock = :maxStock, ps.minOrderQuantity = :minOrderQuantity WHERE ps.product.id = :productId")
    void updateStockSettings(@Param("productId") Long productId, 
                             @Param("minStock") int minStock,
                             @Param("maxStock") int maxStock, 
                             @Param("minOrderQuantity") int minOrderQuantity);
    
    // ===================================================
    // 통계 및 집계 - ✅ Category String 버전으로 수정
    // ===================================================
    
    /**
     * 전체 상품의 총 재고 수량
     * 
     * @return 총 재고 수량
     */
    @Query("SELECT COALESCE(SUM(ps.quantity), 0) FROM ProductStock ps")
    long getTotalStockQuantity();
    
    /**
     * ✅ 카테고리별 총 재고 수량 (String 버전)
     * Product.category가 String 타입이므로 직접 비교
     * 
     * @param categoryName 카테고리명 (예: "temp", "ELECTRONICS")
     * @return 카테고리별 총 재고
     */
    @Query("SELECT COALESCE(SUM(ps.quantity), 0) FROM ProductStock ps WHERE ps.product.category = :categoryName")
    long getTotalStockByCategory(@Param("categoryName") String categoryName);
    
    /**
     * ✅ 네이티브 쿼리 버전 (성능 최적화 필요시)
     * 
     * @param categoryName 카테고리명
     * @return 카테고리별 총 재고
     */
    @Query(value = "SELECT COALESCE(SUM(ps.quantity), 0) FROM product_stocks ps " +
           "INNER JOIN products p ON ps.product_id = p.id " +
           "WHERE p.category = :categoryName", nativeQuery = true)
    long getTotalStockByCategoryNative(@Param("categoryName") String categoryName);
    
    /**
     * 재고 부족 상품 개수
     * 
     * @return 재고 부족 상품 수
     */
    @Query("SELECT COUNT(ps) FROM ProductStock ps WHERE ps.quantity <= ps.minStock")
    long countLowStockProducts();
    
    // ===================================================
    // 발주 관련
    // ===================================================
    
    /**
     * 발주 필요한 상품 조회 (재고 < 최소 재고)
     * 
     * @return 발주 필요 상품 목록
     */
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity < ps.minStock")
    List<ProductStock> findProductsNeedReorder();
    
    /**
     * 특정 상품의 권장 발주 수량 계산
     * 
     * @param productId 상품 ID
     * @return 권장 발주 수량
     */
    @Query("SELECT CASE WHEN (ps.maxStock - ps.quantity) > ps.minOrderQuantity " +
           "THEN (ps.maxStock - ps.quantity) ELSE ps.minOrderQuantity END " +
           "FROM ProductStock ps WHERE ps.product.id = :productId")
    int calculateRecommendedOrderQuantity(@Param("productId") Long productId);
    
    // ===================================================
    // 삭제 관련
    // ===================================================
    
    /**
     * 특정 상품의 재고 정보 삭제
     * 
     * @param product 상품 엔티티
     * @return 삭제된 행 수
     */
    Long deleteByProduct(Product product);
    
    /**
     * 특정 상품 ID의 재고 정보 삭제
     * 
     * @param productId 상품 ID
     * @return 삭제된 행 수
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductStock ps WHERE ps.product.id = :productId")
    int deleteByProductId(@Param("productId") Long productId);
}