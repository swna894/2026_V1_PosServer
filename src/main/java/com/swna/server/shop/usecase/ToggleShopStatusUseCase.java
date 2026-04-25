package com.swna.server.shop.usecase;

import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ToggleShopStatusUseCase {

    private final ShopRepository shopRepository;

    @Transactional
    public void execute(Long shopId, boolean active) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow();

        if (active) {
            shop.activate();
        } else {
            shop.deactivate();
        }
    }
}
