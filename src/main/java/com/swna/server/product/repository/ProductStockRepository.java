package com.swna.server.product.repository;

import com.swna.server.product.entity.Product;
import com.swna.server.product.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    
    Optional<ProductStock> findByProduct(Product product);

    @Query("SELECT ps FROM ProductStock ps WHERE ps.product.id IN :productIds")
    List<ProductStock> findAllByProductIdIn(@Param("productIds") List<Long> productIds);
    
    @Query("SELECT ps FROM ProductStock ps WHERE ps.product.id = :productId")
    Optional<ProductStock> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity <= ps.minStock")
    List<ProductStock> findLowStockProducts();
    
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity = 0")
    List<ProductStock> findOutOfStockProducts();
    
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity <= :threshold")
    List<ProductStock> findProductsWithStockLessThan(@Param("threshold") int threshold);
    
    /**
     * 🔥 벌크 수량 증가 및 lastOrderedAt 일시 업데이트
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductStock ps SET ps.quantity = ps.quantity + :amount, ps.lastOrderedAt = :orderedAt WHERE ps.product.id = :productId")
    int increaseStock(@Param("productId") Long productId, @Param("amount") int amount, @Param("orderedAt") LocalDateTime orderedAt);

    default int increaseStock(Long productId, int amount) {
        return increaseStock(productId, amount, LocalDateTime.now());
    }
    
    /**
     * 🔥 벌크 수량 감소 및 lastOrderedAt 일시 업데이트
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductStock ps SET ps.quantity = ps.quantity - :amount, ps.lastOrderedAt = :orderedAt WHERE ps.product.id = :productId AND ps.quantity >= :amount")
    int decreaseStock(@Param("productId") Long productId, @Param("amount") int amount, @Param("orderedAt") LocalDateTime orderedAt);

    default int decreaseStock(Long productId, int amount) {
        return decreaseStock(productId, amount, LocalDateTime.now());
    }

    /**
     * 🔥 lastOrderedAt 단독 업데이트 쿼리
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductStock ps SET ps.lastOrderedAt = :orderedAt WHERE ps.product.id = :productId")
    int updateLastOrderedAt(@Param("productId") Long productId, @Param("orderedAt") LocalDateTime orderedAt);
    
    @Modifying
    @Transactional
    @Query("UPDATE ProductStock ps SET ps.minStock = :minStock, ps.maxStock = :maxStock, ps.minOrderQuantity = :minOrderQuantity WHERE ps.product.id = :productId")
    void updateStockSettings(@Param("productId") Long productId, 
                             @Param("minStock") int minStock,
                             @Param("maxStock") int maxStock, 
                             @Param("minOrderQuantity") int minOrderQuantity);
    
    @Query("SELECT COALESCE(SUM(ps.quantity), 0) FROM ProductStock ps")
    long getTotalStockQuantity();
    
    @Query("SELECT COALESCE(SUM(ps.quantity), 0) FROM ProductStock ps WHERE ps.product.category = :categoryName")
    long getTotalStockByCategory(@Param("categoryName") String categoryName);
    
    @Query(value = "SELECT COALESCE(SUM(ps.quantity), 0) FROM product_stocks ps " +
           "INNER JOIN products p ON ps.product_id = p.id " +
           "WHERE p.category = :categoryName", nativeQuery = true)
    long getTotalStockByCategoryNative(@Param("categoryName") String categoryName);
    
    @Query("SELECT COUNT(ps) FROM ProductStock ps WHERE ps.quantity <= ps.minStock")
    long countLowStockProducts();
    
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity < ps.minStock")
    List<ProductStock> findProductsNeedReorder();
    
    @Query("SELECT CASE WHEN (ps.maxStock - ps.quantity) > ps.minOrderQuantity " +
           "THEN (ps.maxStock - ps.quantity) ELSE ps.minOrderQuantity END " +
           "FROM ProductStock ps WHERE ps.product.id = :productId")
    int calculateRecommendedOrderQuantity(@Param("productId") Long productId);
    
    Long deleteByProduct(Product product);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductStock ps WHERE ps.product.id = :productId")
    int deleteByProductId(@Param("productId") Long productId);
}