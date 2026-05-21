package com.swna.server.sale.repository;

import com.swna.server.sale.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    
    // sale_id 외래키를 기반으로 모든 SaleItem을 조회합니다.
    List<SaleItem> findBySaleId(Long saleId);
}
