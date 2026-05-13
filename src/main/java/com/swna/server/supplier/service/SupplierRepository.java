package com.swna.server.supplier.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swna.server.supplier.entity.Supplier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    // ===== 기본 조회 메서드 =====
    
    /**
     * 약어로 거래처 조회
     */
    Optional<Supplier> findByAbbr(String abbr);
    
    /**
     * 약어 존재 여부 확인
     */
    boolean existsByAbbr(String abbr);
    
    /**
     * 약어 중복 체크 (자신 제외)
     */
    boolean existsByAbbrAndIdNot(String abbr, Long id);
    
    /**
     * 활성화된 거래처 목록 조회 (이름순 정렬)
     */
    List<Supplier> findByActiveTrueOrderByNameAsc();
    
    /**
     * 비활성화된 거래처 목록 조회
     */
    List<Supplier> findByActiveFalseOrderByNameAsc();
    
    /**
     * 전체 거래처 이름순 조회
     */
    List<Supplier> findAllByOrderByNameAsc();
    
    // ===== 검색 쿼리 =====
    
    /**
     * 키워드로 거래처 검색 (이름, 약어, 회사명)
     */
    @Query("SELECT s FROM Supplier s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.abbr) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.company) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supplier> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 활성화된 거래처만 검색
     */
    @Query("SELECT s FROM Supplier s WHERE s.active = true AND (" +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.abbr) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.company) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Supplier> searchActiveByKeyword(@Param("keyword") String keyword);
    
    /**
     * 회사명으로 거래처 조회
     */
    List<Supplier> findByCompanyContainingIgnoreCase(String company);
    
    /**
     * 담당자명으로 거래처 조회
     */
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    // ===== 통계 쿼리 =====
    
    /**
     * 활성화된 거래처 수
     */
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.active = true")
    long countActive();
    
    /**
     * 비활성화된 거래처 수
     */
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.active = false")
    long countInactive();
    
    /**
     * 거래처 전체 통계
     */
    @Query("SELECT COUNT(s), " +
           "SUM(CASE WHEN s.active = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN s.active = false THEN 1 ELSE 0 END) " +
           "FROM Supplier s")
    Object[] getStatistics();
}

// 또는 Custom Repository 추가 (복잡한 쿼리용)
interface SupplierCustomRepository {
    List<Supplier> findWithComplexCondition(String keyword, Boolean activeOnly);
}

@Repository
class SupplierCustomRepositoryImpl implements SupplierCustomRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<Supplier> findWithComplexCondition(String keyword, Boolean activeOnly) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM Supplier s WHERE 1=1");
        
        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND (LOWER(s.name) LIKE LOWER(:keyword) ");
            jpql.append(" OR LOWER(s.abbr) LIKE LOWER(:keyword) ");
            jpql.append(" OR LOWER(s.company) LIKE LOWER(:keyword))");
        }
        
        if (activeOnly != null && activeOnly) {
            jpql.append(" AND s.active = true");
        }
        
        jpql.append(" ORDER BY s.name ASC");
        
        TypedQuery<Supplier> query = em.createQuery(jpql.toString(), Supplier.class);
        
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        
        return query.getResultList();
    }
}