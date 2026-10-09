package com.swna.server.shop;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.shop.dto.ShopDto;
import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.service.ShopService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // C: Create
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(@RequestBody @Valid ShopDto request) {
        Long shopId = shopService.createShop(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("SHOP_CREATED", shopId));
    }

    // R: Read (전체 목록)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Shop>>> getAllShops() {
        List<Shop> shops = shopService.getAllShops();
        return ResponseEntity.ok(ApiResponse.success("SHOP_LIST_FOUND", shops));
    }

    // R: Read (첫 번째 샵)
    @GetMapping("/first")
    public ResponseEntity<ApiResponse<Shop>> getFirstShop() {
        Shop shop = shopService.findFirstShop();
        return ResponseEntity.ok(ApiResponse.success("SHOP_FOUND", shop));
    }

    // R: Read (단건)
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Shop>> get(@PathVariable("id") Long id) {
        Shop shop = shopService.getShop(id);
        return ResponseEntity.ok(ApiResponse.success("SHOP_FOUND", shop));
    }

    // U: Update
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid ShopDto request) {
        shopService.updateShop(id, request);
        return ResponseEntity.ok(ApiResponse.success("SHOP_UPDATED", null));
    }

    // U: Status Toggle
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggle(
            @PathVariable("id") Long id,
            @RequestParam("active") boolean active) {
        shopService.toggleStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("SHOP_STATUS_UPDATED", null));
    }

    // D: Delete
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.ok(ApiResponse.success("SHOP_DELETED", null));
    }
}