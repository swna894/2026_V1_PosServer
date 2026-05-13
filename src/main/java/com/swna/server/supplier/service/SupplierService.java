package com.swna.server.supplier.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.ErrorCode;
import com.swna.server.supplier.dto.SupplierRequestRecord;
import com.swna.server.supplier.dto.SupplierResponseRecord;
import com.swna.server.supplier.entity.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;

    // =========================================================
    // 조회 메서드 (Record 반환)
    // =========================================================
    
    /**
     * 전체 거래처 목록 조회 (Record 반환)
     */
    public List<SupplierResponseRecord> getAll() {
        log.info("[SupplierService] Fetching all suppliers");
        
        List<Supplier> suppliers = supplierRepository.findAllByOrderByNameAsc();
        
        if (suppliers.isEmpty()) {
            log.debug("[SupplierService] No suppliers found");
        }
        
        return SupplierResponseRecord.from(suppliers);
    }
    
    /**
     * 활성화된 거래처 목록 조회 (Record 반환)
     */
    public List<SupplierResponseRecord> getActiveSuppliers() {
        log.info("[SupplierService] Fetching active suppliers");
        
        List<Supplier> suppliers = supplierRepository.findByActiveTrueOrderByNameAsc();
        
        return SupplierResponseRecord.from(suppliers);
    }
    
    /**
     * 거래처 단건 조회 (Record 반환)
     */
    public SupplierResponseRecord getById(Long id) {
        log.info("[SupplierService] Fetching supplier by id: {}", id);
        
        Supplier supplier = findSupplierById(id);
        
        return SupplierResponseRecord.from(supplier);
    }
    
    /**
     * 약어로 거래처 조회 (Record 반환)
     */
    public SupplierResponseRecord getByAbbr(String abbr) {
        log.info("[SupplierService] Fetching supplier by abbr: {}", abbr);
        
        Supplier supplier = supplierRepository.findByAbbr(abbr)
                .orElseThrow(() -> ErrorCode.PRODUCT_NOT_FOUND.withDetails(Map.of(
                        "abbr", abbr,
                        "message", "Supplier not found with abbreviation: " + abbr
                )));
        
        return SupplierResponseRecord.from(supplier);
    }
    
    /**
     * 키워드로 거래처 검색 (Record 반환)
     */
    public List<SupplierResponseRecord> searchByKeyword(String keyword) {
        log.info("[SupplierService] Searching suppliers by keyword: {}", keyword);
        
        if (keyword == null || keyword.isBlank()) {
            log.debug("[SupplierService] Empty keyword provided, returning all suppliers");
            return getAll();
        }
        
        List<Supplier> suppliers = supplierRepository.searchByKeyword(keyword);
        
        return SupplierResponseRecord.from(suppliers);
    }
    
    /**
     * 활성화된 거래처만 검색 (Record 반환)
     */
    public List<SupplierResponseRecord> searchActiveByKeyword(String keyword) {
        log.info("[SupplierService] Searching active suppliers by keyword: {}", keyword);
        
        List<Supplier> suppliers = supplierRepository.searchActiveByKeyword(keyword);
        
        return SupplierResponseRecord.from(suppliers);
    }
    
    
    // =========================================================
    // Entity 반환 메서드 (내부 사용)
    // =========================================================
    
    /**
     * 거래처 Entity 조회 (내부 사용용)
     */
    private Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> ErrorCode.PRODUCT_NOT_FOUND.withDetails(Map.of(
                        "id", id,
                        "message", "Supplier not found with id: " + id
                )));
    }
    
    /**
     * 거래처 Entity 조회 (활성화 여부 체크 포함)
     */
    private Supplier findActiveSupplierById(Long id) {
        Supplier supplier = findSupplierById(id);
        
        if (!supplier.isActive()) {
            throw ErrorCode.STORE_INACTIVE.withDetails(Map.of(
                    "id", id,
                    "abbr", supplier.getAbbr(),
                    "name", supplier.getName(),
                    "message", "Supplier is inactive: " + supplier.getName()
            ));
        }
        
        return supplier;
    }
    
    // =========================================================
    // 등록/수정/삭제 메서드
    // =========================================================
    
    /**
     * 거래처 등록 (Record 요청 -> Record 응답)
     */
    @Transactional
    public SupplierResponseRecord create(SupplierRequestRecord request) {
        log.info("[SupplierService] Creating new supplier - abbr: {}, name: {}", 
                request.abbr(), request.name());
        
        // 약어 유효성 검증
        validateAbbr(request.abbr());
        
        // 약어 중복 체크
        if (supplierRepository.existsByAbbr(request.abbr())) {
            throw ErrorCode.PRODUCT_ALREADY_EXISTS.withDetails(Map.of(
                    "abbr", request.abbr(),
                    "message", "Supplier already exists with abbreviation: " + request.abbr()
            ));
        }
        
        // 이메일 유효성 검증 (선택적)
        if (request.email() != null && !request.email().isBlank() && !isValidEmail(request.email())) {
            throw ErrorCode.INVALID_INPUT.withDetails(Map.of(
                    "field", "email",
                    "value", request.email(),
                    "message", "Invalid email format"
            ));
        }
        
        try {
            // Entity 생성
            Supplier supplier = new Supplier(
                    request.abbr(),
                    request.name(),
                    request.company() != null ? request.company() : "",
                    request.email() != null ? request.email() : "",
                    request.phone() != null ? request.phone() : "",
                    request.address() != null ? request.address() : ""
            );
            
            // 휴대폰 번호 설정 (있는 경우)
            if (request.cellphone() != null && !request.cellphone().isBlank()) {
                supplier.updateCellphone(request.cellphone());
            }
            
            Supplier saved = supplierRepository.save(supplier);
            
            log.info("[SupplierService] Supplier created successfully - id: {}, abbr: {}", 
                    saved.getId(), saved.getAbbr());
            
            return SupplierResponseRecord.from(saved);
            
        } catch (Exception e) {
            log.error("[SupplierService] Failed to create supplier - abbr: {}, error: {}", 
                    request.abbr(), e.getMessage(), e);
            throw ErrorCode.DATABASE_ERROR.withDetails(Map.of(
                    "operation", "create",
                    "abbr", request.abbr(),
                    "message", "Failed to create supplier: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 거래처 수정 (Record 요청 -> Record 응답)
     */
    @Transactional
    public SupplierResponseRecord update(Long id, SupplierRequestRecord request) {
        log.info("[SupplierService] Updating supplier - id: {}, abbr: {}", id, request.abbr());
        
        Supplier supplier = findSupplierById(id);
        
        // 약어 중복 체크 (자신 제외)
        if (!supplier.getAbbr().equals(request.abbr()) && 
            supplierRepository.existsByAbbrAndIdNot(request.abbr(), id)) {
            throw ErrorCode.PRODUCT_ALREADY_EXISTS.withDetails(Map.of(
                    "abbr", request.abbr(),
                    "message", "Supplier already exists with abbreviation: " + request.abbr()
            ));
        }
        
        try {
            // 정보 업데이트
            supplier.updateInfo(
                    request.name(),
                    request.phone() != null ? request.phone() : "",
                    request.email() != null ? request.email() : "",
                    request.address() != null ? request.address() : ""
            );
            
            if (request.company() != null) {
                supplier.updateCompany(request.company());
            }
            
            if (request.cellphone() != null) {
                supplier.updateCellphone(request.cellphone());
            }
            
            Supplier updated = supplierRepository.save(supplier);
            
            log.info("[SupplierService] Supplier updated successfully - id: {}", id);
            
            return SupplierResponseRecord.from(updated);
            
        } catch (Exception e) {
            log.error("[SupplierService] Failed to update supplier - id: {}, error: {}", 
                    id, e.getMessage(), e);
            throw ErrorCode.DATABASE_ERROR.withDetails(Map.of(
                    "operation", "update",
                    "id", id,
                    "message", "Failed to update supplier: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 거래처 비활성화
     */
    @Transactional
    public SupplierResponseRecord deactivate(Long id) {
        log.info("[SupplierService] Deactivating supplier - id: {}", id);
        
        Supplier supplier = findSupplierById(id);
        
        if (!supplier.isActive()) {
            log.warn("[SupplierService] Supplier already inactive - id: {}, abbr: {}", 
                    id, supplier.getAbbr());
            return SupplierResponseRecord.from(supplier);
        }
        
        try {
            supplier.deactivate();
            Supplier updated = supplierRepository.save(supplier);
            
            log.info("[SupplierService] Supplier deactivated successfully - id: {}, abbr: {}", 
                    id, supplier.getAbbr());
            
            return SupplierResponseRecord.from(updated);
            
        } catch (Exception e) {
            log.error("[SupplierService] Failed to deactivate supplier - id: {}, error: {}", 
                    id, e.getMessage(), e);
            throw ErrorCode.DATABASE_ERROR.withDetails(Map.of(
                    "operation", "deactivate",
                    "id", id,
                    "message", "Failed to deactivate supplier: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 거래처 활성화
     */
    @Transactional
    public SupplierResponseRecord activate(Long id) {
        log.info("[SupplierService] Activating supplier - id: {}", id);
        
        Supplier supplier = findSupplierById(id);
        
        if (supplier.isActive()) {
            log.warn("[SupplierService] Supplier already active - id: {}, abbr: {}", 
                    id, supplier.getAbbr());
            return SupplierResponseRecord.from(supplier);
        }
        
        try {
            supplier.activate();
            Supplier updated = supplierRepository.save(supplier);
            
            log.info("[SupplierService] Supplier activated successfully - id: {}, abbr: {}", 
                    id, supplier.getAbbr());
            
            return SupplierResponseRecord.from(updated);
            
        } catch (Exception e) {
            log.error("[SupplierService] Failed to activate supplier - id: {}, error: {}", 
                    id, e.getMessage(), e);
            throw ErrorCode.DATABASE_ERROR.withDetails(Map.of(
                    "operation", "activate",
                    "id", id,
                    "message", "Failed to activate supplier: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 거래처 삭제 (실제 삭제 - 사용 주의)
     */
    @Transactional
    public void delete(Long id) {
        log.info("[SupplierService] Deleting supplier - id: {}", id);
        
        Supplier supplier = findSupplierById(id);
        
        try {
            supplierRepository.delete(supplier);
            
            log.info("[SupplierService] Supplier deleted successfully - id: {}, abbr: {}", 
                    id, supplier.getAbbr());
            
        } catch (Exception e) {
            log.error("[SupplierService] Failed to delete supplier - id: {}, error: {}", 
                    id, e.getMessage(), e);
            throw ErrorCode.DATABASE_ERROR.withDetails(Map.of(
                    "operation", "delete",
                    "id", id,
                    "message", "Failed to delete supplier: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 거래처 완전 삭제 (하드 삭제 - 연결된 데이터가 없는 경우에만)
     */
    @Transactional
    public void deleteHard(Long id) {
        log.warn("[SupplierService] Hard deleting supplier - id: {}", id);
        
        Supplier supplier = findSupplierById(id);
        
        // 연결된 상품이 있는지 확인해야 함 (필요시 구현)
        // if (productRepository.existsBySupplierId(id)) {
        //     throw ErrorCode.DATA_INTEGRITY_VIOLATION.withDetails(...);
        // }
        
        try {
            supplierRepository.delete(supplier);
            
            log.info("[SupplierService] Supplier hard deleted successfully - id: {}, abbr: {}", 
                    id, supplier.getAbbr());
            
        } catch (Exception e) {
            log.error("[SupplierService] Failed to hard delete supplier - id: {}, error: {}", 
                    id, e.getMessage(), e);
            throw ErrorCode.DATABASE_ERROR.withDetails(Map.of(
                    "operation", "hardDelete",
                    "id", id,
                    "message", "Failed to hard delete supplier: " + e.getMessage()
            ));
        }
    }
    
    // =========================================================
    // 유틸리티/검증 메서드
    // =========================================================
    
    /**
     * 약어 유효성 검증
     */
    private void validateAbbr(String abbr) {
        if (abbr == null || abbr.isBlank()) {
            throw ErrorCode.INVALID_INPUT.withDetails(Map.of(
                    "field", "abbr",
                    "message", "Supplier abbreviation is required"
            ));
        }
        
        if (abbr.length() < 2 || abbr.length() > 8) {
            throw ErrorCode.INVALID_INPUT.withDetails(Map.of(
                    "field", "abbr",
                    "value", abbr,
                    "message", "Supplier abbreviation must be between 2 and 8 characters"
            ));
        }
        
        if (!abbr.matches("^[A-Z0-9]+$")) {
            throw ErrorCode.INVALID_INPUT.withDetails(Map.of(
                    "field", "abbr",
                    "value", abbr,
                    "message", "Supplier abbreviation must contain only uppercase letters and numbers"
            ));
        }
    }
    
    /**
     * 이메일 유효성 검증
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return true; // 빈 값은 허용
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    /**
     * 거래처 존재 여부 확인
     */
    public boolean existsById(Long id) {
        return supplierRepository.existsById(id);
    }
    
    /**
     * 약어 존재 여부 확인
     */
    public boolean existsByAbbr(String abbr) {
        return supplierRepository.existsByAbbr(abbr);
    }
}