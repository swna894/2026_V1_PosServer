package com.swna.server.shop.usecase;

import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateShopUseCase {

    private final ShopRepository shopRepository;

    @Transactional
    public Long execute(String name, String address, String phone, String businessNo) {

        Shop shop = Shop.create(name, address, phone, businessNo);

        shopRepository.save(shop);

        return shop.getId();
    }
}
