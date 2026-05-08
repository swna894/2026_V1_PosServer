package com.swna.server.shop_config.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.repository.ShopRepository;
import com.swna.server.shop_config.entity.ShopConfig;
import com.swna.server.shop_config.repository.ShopConfigRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateShopConfigUseCase {

    private final ShopRepository shopRepository;
    private final ShopConfigRepository shopConfigRepository;

    @Transactional
    @SuppressWarnings("null")
    public Long execute(@NonNull Long shopId) {

        Shop shop = shopRepository.findById(shopId).orElseThrow();

        ShopConfig config = ShopConfig.createDefault(shop);

        shopConfigRepository.save(config);

        return config.getId();
    }
}
