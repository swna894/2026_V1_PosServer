package com.swna.server.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByDeletedFalse();

    /**
     * 랜덤 상품 조회 (기존)
     */
    @Query(value = "SELECT * FROM products WHERE deleted = false ORDER BY RAND() LIMIT 65", nativeQuery = true)
    List<Product> findRandomProducts();

    /**
     * Product와 Supplier를 abbr로 조인하여 ProductLabelDto 리스트로 반환 (id, code 포함)
     * JPQL 사용 - DTO 직접 매핑
     */
    @Query("""
        SELECT new com.swna.server.product.dto.ProductLabelDto(
            p.id,
            p.barcode,
            COALESCE(s.company, ''),
            p.code,
            p.description,
            p.price
        )
        FROM Product p
        LEFT JOIN Supplier s ON p.abbr = s.abbr
        WHERE p.deleted = false
        ORDER BY FUNCTION('RAND')
    """)
    List<ProductLabelDto> findRandomProductLabelsWithSupplier();
    
    /**
     * 랜덤 상품 조회 (LIMIT 적용)
     */
    @Query(value = """
        SELECT new com.swna.server.product.dto.ProductLabelDto(
            p.id,
            p.barcode,
            COALESCE(s.company, ''),
            p.code,
            p.description,
            p.price
        )
        FROM Product p
        LEFT JOIN Supplier s ON p.abbr = s.abbr
        WHERE p.deleted = false
    """)
    List<ProductLabelDto> findRandomProductLabelsWithSupplierLimit(Pageable pageable);
    
    
    /**
     * 특정 company (supplier)의 상품만 조회
     */
    @Query("""
        SELECT new com.swna.server.product.dto.ProductLabelDto(
            p.id,
            p.barcode,
            COALESCE(s.company, ''),
            p.code,
            p.description,
            p.price
        )
        FROM Product p
        LEFT JOIN Supplier s ON p.abbr = s.abbr
        WHERE p.deleted = false AND s.company = :company
        ORDER BY FUNCTION('RAND')
    """)
    List<ProductLabelDto> findProductLabelsByCompany(@Param("company") String company);
    
    /**
     * 특정 abbr 목록에 해당하는 상품 조회
     */
    @Query("""
        SELECT new com.swna.server.product.dto.ProductLabelDto(
            p.id,
            p.barcode,
            COALESCE(s.company, ''),
            p.code,
            p.description,
            p.price
        )
        FROM Product p
        LEFT JOIN Supplier s ON p.abbr = s.abbr
        WHERE p.deleted = false AND p.abbr IN :abbrs
    """)
    List<ProductLabelDto> findProductLabelsByAbbrs(@Param("abbrs") List<String> abbrs);
    
    /**
     * active 상태인 Supplier의 상품만 조회
     */
    @Query("""
        SELECT new com.swna.server.product.dto.ProductLabelDto(
            p.id,
            p.barcode,
            COALESCE(s.company, ''),
            p.code,
            p.description,
            p.price
        )
        FROM Product p
        LEFT JOIN Supplier s ON p.abbr = s.abbr
        WHERE p.deleted = false AND s.active = true
        ORDER BY FUNCTION('RAND')
    """)
    List<ProductLabelDto> findRandomProductLabelsFromActiveSuppliers();
    
    /**
     * 바코드로 단일 상품 라벨 DTO 조회 (company 포함)
     */
    @Query("""
        SELECT new com.swna.server.product.dto.ProductLabelDto(
            p.id,
            p.barcode,
            COALESCE(s.company, ''),
            p.code,
            p.description,
            p.price
        )
        FROM Product p
        LEFT JOIN Supplier s ON p.abbr = s.abbr
        WHERE p.barcode = :barcode AND p.deleted = false
    """)
    Optional<ProductLabelDto> findProductLabelByBarcode(@Param("barcode") String barcode);
    
    /**
     * POS 바코드 상품 조회 (기존)
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
