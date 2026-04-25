package com.swna.server.shop_config.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.shop_config.entity.ShopConfig;

public interface ShopConfigRepository extends JpaRepository<ShopConfig, Long> {

    Optional<ShopConfig> findByShopId(Long shopId);
}
