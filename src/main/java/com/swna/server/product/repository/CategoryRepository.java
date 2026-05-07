package com.swna.server.product.repository;

import com.swna.server.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 Repository
 * 
 * @author SWNA
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // ===================================================
    // 기본 조회 메서드
    // ===================================================
    
    /**
     * 카테고리 이름으로 조회 (정확히 일치)
     * 
     * @param name 카테고리명 (예: "temp", "ELECTRONICS")
     * @return Optional<Category>
     */
    Optional<Category> findByName(String name);
    
    /**
     * 카테고리 이름이 특정 문자열을 포함하는지 조회 (Like 검색)
     * 
     * @param name 부분 카테고리명
     * @return List<Category>
     */
    List<Category> findByNameContaining(String name);
    
    /**
     * 카테고리 이름으로 존재 여부 확인
     * 
     * @param name 카테고리명
     * @return 존재하면 true
     */
    boolean existsByName(String name);
    
    // ===================================================
    // 정렬 및 페이징
    // ===================================================
    
    /**
     * 모든 카테고리를 이름순으로 정렬하여 조회
     * 
     * @return 정렬된 카테고리 목록
     */
    List<Category> findAllByOrderByNameAsc();
    
    /**
     * ID 목록으로 카테고리 조회
     * 
     * @param ids 카테고리 ID 목록
     * @return 카테고리 목록
     */
    List<Category> findByIdIn(List<Long> ids);
    
    // ===================================================
    // JPQL / Native Query
    // ===================================================
    
    /**
     * 카테고리 이름으로 조회 (대소문자 무시)
     * 
     * @param name 카테고리명
     * @return Optional<Category>
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * 특정 패턴의 카테고리명 조회 (Native Query 예시)
     * 
     * @param pattern 검색 패턴
     * @return 카테고리 목록
     */
    @Query(value = "SELECT * FROM category WHERE name LIKE CONCAT('%', :pattern, '%')", 
           nativeQuery = true)
    List<Category> findByNamePattern(@Param("pattern") String pattern);
    
    // ===================================================
    // 삭제 메서드
    // ===================================================
    
    /**
     * 카테고리 이름으로 삭제
     * 
     * @param name 카테고리명
     * @return 삭제된 행 수
     */
    Long deleteByName(String name);
}
