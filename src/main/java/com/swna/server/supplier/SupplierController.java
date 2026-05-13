package com.swna.server.supplier;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.supplier.dto.SupplierRequestRecord;
import com.swna.server.supplier.dto.SupplierResponseRecord;
import com.swna.server.supplier.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // =========================================================
    // GET Endpoints
    // =========================================================
    
    /**
     * 전체 거래처 목록 조회
     * GET /api/suppliers
     */
    @GetMapping
    public ApiResponse<List<SupplierResponseRecord>> getAllSuppliers() {
        log.info("[API] GET /api/suppliers - Fetching all suppliers");
        
        List<SupplierResponseRecord> suppliers = supplierService.getAll();
        
        return ApiResponse.success(
            "Suppliers retrieved successfully", 
            suppliers
        );
    }
    
    /**
     * 활성화된 거래처 목록 조회
     * GET /api/suppliers/active
     */
    @GetMapping("/active")
    public ApiResponse<List<SupplierResponseRecord>> getActiveSuppliers() {
        log.info("[API] GET /api/suppliers/active - Fetching active suppliers");
        
        List<SupplierResponseRecord> suppliers = supplierService.getActiveSuppliers();
        
        return ApiResponse.success(
            "Active suppliers retrieved successfully", 
            suppliers
        );
    }
    
    /**
     * 거래처 단건 조회
     * GET /api/suppliers/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<SupplierResponseRecord> getSupplier(@PathVariable Long id) {
        log.info("[API] GET /api/suppliers/{}", id);
        
        SupplierResponseRecord supplier = supplierService.getById(id);
        
        return ApiResponse.success(
            "Supplier retrieved successfully", 
            supplier
        );
    }
    
    /**
     * 약어로 거래처 조회
     * GET /api/suppliers/abbr/{abbr}
     */
    @GetMapping("/abbr/{abbr}")
    public ApiResponse<SupplierResponseRecord> getSupplierByAbbr(@PathVariable String abbr) {
        log.info("[API] GET /api/suppliers/abbr/{}", abbr);
        
        SupplierResponseRecord supplier = supplierService.getByAbbr(abbr);
        
        return ApiResponse.success(
            "Supplier retrieved successfully", 
            supplier
        );
    }
    
    /**
     * 키워드로 거래처 검색
     * GET /api/suppliers/search?keyword=삼성
     */
    @GetMapping("/search")
    public ApiResponse<List<SupplierResponseRecord>> searchSuppliers(
            @RequestParam(required = false) String keyword) {
        
        log.info("[API] GET /api/suppliers/search - keyword: {}", keyword);
        
        List<SupplierResponseRecord> suppliers = supplierService.searchByKeyword(keyword);
        
        return ApiResponse.success(
            "Search completed successfully", 
            suppliers
        );
    }
    
    /**
     * 활성화된 거래처만 검색
     * GET /api/suppliers/search/active?keyword=삼성
     */
    @GetMapping("/search/active")
    public ApiResponse<List<SupplierResponseRecord>> searchActiveSuppliers(
            @RequestParam(required = false) String keyword) {
        
        log.info("[API] GET /api/suppliers/search/active - keyword: {}", keyword);
        
        List<SupplierResponseRecord> suppliers = supplierService.searchActiveByKeyword(keyword);
        
        return ApiResponse.success(
            "Search completed successfully", 
            suppliers
        );
    }
    
    
    // =========================================================
    // POST Endpoints
    // =========================================================
    
    /**
     * 거래처 등록
     * POST /api/suppliers
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SupplierResponseRecord> createSupplier(
            @Valid @RequestBody SupplierRequestRecord request) {
        
        log.info("[API] POST /api/suppliers - Creating supplier with abbr: {}", request.abbr());
        
        SupplierResponseRecord supplier = supplierService.create(request);
        
        return ApiResponse.success(
            "Supplier created successfully", 
            supplier
        );
    }
    
    // =========================================================
    // PUT Endpoints
    // =========================================================
    
    /**
     * 거래처 수정
     * PUT /api/suppliers/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<SupplierResponseRecord> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestRecord request) {
        
        log.info("[API] PUT /api/suppliers/{} - Updating supplier", id);
        
        SupplierResponseRecord supplier = supplierService.update(id, request);
        
        return ApiResponse.success(
            "Supplier updated successfully", 
            supplier
        );
    }
    
    // =========================================================
    // PATCH Endpoints
    // =========================================================
    
    /**
     * 거래처 비활성화
     * PATCH /api/suppliers/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ApiResponse<SupplierResponseRecord> deactivateSupplier(@PathVariable Long id) {
        log.info("[API] PATCH /api/suppliers/{}/deactivate", id);
        
        SupplierResponseRecord supplier = supplierService.deactivate(id);
        
        return ApiResponse.success(
            "Supplier deactivated successfully", 
            supplier
        );
    }
    
    /**
     * 거래처 활성화
     * PATCH /api/suppliers/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ApiResponse<SupplierResponseRecord> activateSupplier(@PathVariable Long id) {
        log.info("[API] PATCH /api/suppliers/{}/activate", id);
        
        SupplierResponseRecord supplier = supplierService.activate(id);
        
        return ApiResponse.success(
            "Supplier activated successfully", 
            supplier
        );
    }
    
    // =========================================================
    // DELETE Endpoints
    // =========================================================
    
    /**
     * 거래처 삭제
     * DELETE /api/suppliers/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteSupplier(@PathVariable Long id) {
        log.info("[API] DELETE /api/suppliers/{}", id);
        
        supplierService.delete(id);
        
        return ApiResponse.success("Supplier deleted successfully", null);
    }
}