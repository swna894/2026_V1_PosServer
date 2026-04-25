package com.swna.server.shop_config.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.shop_config.entity.ShopConfig;
import com.swna.server.shop_config.repository.ShopConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetShopConfigUseCase {

    private final ShopConfigRepository shopConfigRepository;

    @Transactional(readOnly = true)
    public ShopConfig execute(Long shopId) {

        return shopConfigRepository.findByShopId(shopId)
                .orElseThrow(() -> new IllegalArgumentException("ShopConfig not found"));
    }
}
