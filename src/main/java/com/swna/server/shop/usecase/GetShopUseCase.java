package com.swna.server.shop.usecase;

import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetShopUseCase {

    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public Shop execute(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
    }
}
