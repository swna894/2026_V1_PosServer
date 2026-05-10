package com.swna.server.shop.repository;

import com.swna.server.shop.entity.Shop;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
 
}