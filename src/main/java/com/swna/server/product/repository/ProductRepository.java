package com.swna.server.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByDeletedFalse();
    @Query(value = "SELECT * FROM products WHERE deleted = false ORDER BY RAND() LIMIT 65", nativeQuery = true)
    List<Product> findRandomProducts();

        /**
     * POS 바코드 상품 조회
     */
    @Query("""
        select new com.swna.server.product.dto.ProductResponse(
            p.code,
            p.barcode,
            p.description,
            p.price,
            p.price,
            coalesce(ps.quantity, 0)
        )
        from Product p
        left join ProductStock ps on ps.product = p
        where p.barcode = :barcode and p.deleted = false
    """)
    Optional<ProductResponse> findProductResponseByBarcode(
            @Param("barcode") String barcode
    );
}
