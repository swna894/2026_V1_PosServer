package com.swna.server.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByDeletedFalse();
    List<Product> findAllByBarcodeIn(List<String> barcodes);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.id IN :ids AND p.deleted = false")
    int deleteAllByIds(@Param("ids") List<Long> ids);

    List<Product> findAllByIdInAndDeletedFalse(List<Long> ids);

    @Query(value = "SELECT * FROM products WHERE deleted = false ORDER BY RAND() LIMIT 65", nativeQuery = true)
    List<Product> findRandomProducts();

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
    """)
    List<ProductLabelDto> findRandomProductLabelsWithSupplierLimit(Pageable pageable);

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

    @Query("""
        SELECT new com.swna.server.product.dto.ProductResponse(
            p.id,
            p.code,
            p.barcode,
            p.description,
            p.price,
            p.cost,
            p.priceOld,
            p.costOld,
            COALESCE(ps.quantity, 0),
            COALESCE(ps.minStock, 0),
            COALESCE(CASE WHEN ps.minOrderQuantity <= 0 THEN 12 ELSE ps.minOrderQuantity END, 12),
            ps.lastOrderedAt
        )
        FROM Product p
        LEFT JOIN ProductStock ps ON ps.product = p
        WHERE p.barcode = :barcode AND p.deleted = false
    """)
    Optional<ProductResponse> findProductResponseByBarcode(@Param("barcode") String barcode);

    @Query("""
        SELECT new com.swna.server.product.dto.ProductResponse(
            p.id,
            p.code,
            p.barcode,
            p.description,
            p.price,
            p.cost,
            p.priceOld,
            p.costOld,
            COALESCE(ps.quantity, 0),
            COALESCE(ps.minStock, 0),
            COALESCE(CASE WHEN ps.minOrderQuantity <= 0 THEN 12 ELSE ps.minOrderQuantity END, 12),
            ps.lastOrderedAt
        )
        FROM Product p
        LEFT JOIN ProductStock ps ON ps.product = p
        WHERE p.id = :id AND p.deleted = false
    """)
    Optional<ProductResponse> findProductResponseById(@Param("id") Long id);

    @Query("""
        SELECT new com.swna.server.product.dto.ProductResponse(
            p.id,
            p.code,
            p.barcode,
            p.description,
            p.price,
            p.cost,
            p.priceOld,
            p.costOld,
            COALESCE(ps.quantity, 0),
            COALESCE(ps.minStock, 0),
            COALESCE(CASE WHEN ps.minOrderQuantity <= 0 THEN 12 ELSE ps.minOrderQuantity END, 12),
            ps.lastOrderedAt
        )
        FROM Product p
        LEFT JOIN ProductStock ps ON ps.product = p
        WHERE p.deleted = false
    """)
    List<ProductResponse> findAllProductResponses();

    @Query("""
        SELECT new com.swna.server.product.dto.ProductResponse(
            p.id,
            p.code,
            p.barcode,
            p.description,
            p.price,
            p.cost,
            p.priceOld,
            p.costOld,
            COALESCE(ps.quantity, 0),
            COALESCE(ps.minStock, 0),
            COALESCE(CASE WHEN ps.minOrderQuantity <= 0 THEN 12 ELSE ps.minOrderQuantity END, 12),
            ps.lastOrderedAt
        )
        FROM Product p
        LEFT JOIN ProductStock ps ON ps.product = p
        WHERE p.abbr = :abbr AND p.deleted = false
    """)
    List<ProductResponse> findProductResponsesByAbbr(@Param("abbr") String abbr);
}