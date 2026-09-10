package com.swna.server.sales_trasaction;

import com.swna.server.sale.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProductSalesTransactionRepository extends JpaRepository<SaleItem, Long> {

    // 주간 판매 수량 조회
    @Query(value = "SELECT si.barcode, DATE_FORMAT(s.created, '%Y-%u') AS week, SUM(si.quantity) " +
                   "FROM sale_items si " +
                   "JOIN sales s ON si.sale_id = s.id " +
                   "WHERE si.barcode = :barcode AND s.created >= :startDate AND s.status = 'COMPLETED' " +
                   "GROUP BY week, si.barcode", nativeQuery = true)
    List<Object[]> findWeeklySales(@Param("barcode") String barcode, @Param("startDate") LocalDate startDate);

    // 월간 판매 수량 조회
    @Query(value = "SELECT si.barcode, DATE_FORMAT(s.created, '%Y-%m') AS month, SUM(si.quantity) " +
                   "FROM sale_items si " +
                   "JOIN sales s ON si.sale_id = s.id " +
                   "WHERE si.barcode = :barcode AND s.created >= :startDate AND s.status = 'COMPLETED' " +
                   "GROUP BY month, si.barcode", nativeQuery = true)
    List<Object[]> findMonthlySales(@Param("barcode") String barcode, @Param("startDate") LocalDate startDate);

    // 분기별 판매 수량 조회
    @Query(value = "SELECT si.barcode, CONCAT(YEAR(s.created), '-', QUARTER(s.created)) AS quarter, SUM(si.quantity) " +
                   "FROM sale_items si " +
                   "JOIN sales s ON si.sale_id = s.id " +
                   "WHERE si.barcode = :barcode AND s.created >= :startDate AND s.status = 'COMPLETED' " +
                   "GROUP BY quarter, si.barcode", nativeQuery = true)
    List<Object[]> findQuarterlySales(@Param("barcode") String barcode, @Param("startDate") LocalDate startDate);
}
