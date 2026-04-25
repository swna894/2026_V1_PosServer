package com.swna.server.shop_config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.shop_config.entity.ShopConfig;
import com.swna.server.shop_config.usecase.CreateShopConfigUseCase;
import com.swna.server.shop_config.usecase.GetShopConfigUseCase;
import com.swna.server.shop_config.usecase.UpdateShopConfigUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shops/{shopId}/config")
@RequiredArgsConstructor
public class ShopConfigController {

    private final CreateShopConfigUseCase createShopConfigUseCase;
    private final GetShopConfigUseCase getShopConfigUseCase;
    private final UpdateShopConfigUseCase updateShopConfigUseCase;

    // =========================
    // Create Config
    // =========================
    @PostMapping
    public Long create(@PathVariable Long shopId) {
        return createShopConfigUseCase.execute(shopId);
    }

    // =========================
    // Get Config
    // =========================
    @GetMapping
    public ShopConfig get(@PathVariable Long shopId) {
        return getShopConfigUseCase.execute(shopId);
    }

    // =========================
    // Print 설정
    // =========================
    @PatchMapping("/print")
    public void updatePrint(@PathVariable Long shopId,
                            @RequestParam boolean enabled,
                            @RequestParam boolean autoPrint) {

        updateShopConfigUseCase.updatePrint(shopId, enabled, autoPrint);
    }

    // =========================
    // 결제 설정
    // =========================
    @PatchMapping("/payment")
    public void updatePayment(@PathVariable Long shopId,
                               @RequestParam boolean cash,
                               @RequestParam boolean card) {

        updateShopConfigUseCase.updatePayment(shopId, cash, card);
    }

    // =========================
    // 카드 단말기 설정
    // =========================
    @PatchMapping("/terminal")
    public void updateTerminal(@PathVariable Long shopId,
                               @RequestParam String ip,
                               @RequestParam Integer port) {

        updateShopConfigUseCase.updateTerminal(shopId, ip, port);
    }
}