package com.swna.server.shop.service;

import com.swna.server.shop.dto.ShopDto;
import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.service.ShopRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    // C: Create
    @Transactional
    public Long createShop(ShopDto request) {
        Shop shop = Shop.create(
                request.company(),
                request.businessNo(),
                request.name(),
                request.email(),
                request.password(),
                request.ccEmail(),
                request.mobilePhone(),
                request.phone(),
                request.street(),
                request.suburb(),
                request.city(),
                request.comment(),
                request.backupFolder(),
                request.reportFolder()
        );

        Shop savedShop = shopRepository.save(shop);
        return Objects.requireNonNull(savedShop.getId(), "Shop ID should not be null after save");
    }

    // R: Read (단건)
    @Transactional(readOnly = true)
    public Shop getShop(@NonNull Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found with id: " + shopId));
    }

    // R: Read (전체 목록)
    @Transactional(readOnly = true)
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Shop findFirstShop() {
        return shopRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
    }

    // U: Update
    @Transactional
    public void updateShop(@NonNull Long shopId, ShopDto request) {
        Shop shop = getShop(shopId);
        shop.update(
                request.company(),
                request.businessNo(),
                request.name(),
                request.email(),
                request.password(),
                request.ccEmail(),
                request.mobilePhone(),
                request.phone(),
                request.street(),
                request.suburb(),
                request.city(),
                request.comment(),
                request.backupFolder(),
                request.reportFolder()
        );
    }

    // Status Toggle
    @Transactional
    public void toggleStatus(@NonNull Long shopId, boolean active) {
        Shop shop = getShop(shopId);
        if (active) {
            shop.activate();
        } else {
            shop.deactivate();
        }
    }

    // D: Delete
    @Transactional
    public void deleteShop(@NonNull Long shopId) {
        Shop shop = getShop(shopId);
        shopRepository.delete(shop);
    }
}
