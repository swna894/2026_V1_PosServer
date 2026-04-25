package com.swna.server.shop_config.usecase;

import org.springframework.stereotype.Service;

import com.swna.server.shop_config.entity.ShopConfig;
import com.swna.server.shop_config.repository.ShopConfigRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateShopConfigUseCase {

    private final ShopConfigRepository shopConfigRepository;

    @Transactional
    public void updatePayment(Long shopId, boolean cashEnabled, boolean cardEnabled) {

        ShopConfig config = get(shopId);

        config.updatePaymentSetting(cashEnabled, cardEnabled);
    }

    @Transactional
    public void updateTerminal(Long shopId, String ip, int port) {

        ShopConfig config = get(shopId);

        config.updateCardTerminal(ip, port);
    }

    @Transactional
    public void updatePrint(Long shopId, boolean enabled, boolean autoPrint) {

        ShopConfig config = get(shopId);

        config.updatePrintSetting(enabled, autoPrint);
    }

    private ShopConfig get(Long shopId) {
        return shopConfigRepository.findByShopId(shopId)
                .orElseThrow(() -> new IllegalArgumentException("ShopConfig not found"));
    }
}