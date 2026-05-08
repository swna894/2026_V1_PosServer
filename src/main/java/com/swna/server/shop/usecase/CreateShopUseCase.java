package com.swna.server.shop.usecase;

import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateShopUseCase {

    private final ShopRepository shopRepository;

    @Transactional
    @SuppressWarnings("null") 
    public Long execute(String name, String address, String phone, String businessNo) {

        Shop shop = Shop.create(name, address, phone, businessNo);

        Shop savedShop = shopRepository.save(shop);

        return Objects.requireNonNull(savedShop.getId(), "Shop ID should not be null after save");
    }
}
