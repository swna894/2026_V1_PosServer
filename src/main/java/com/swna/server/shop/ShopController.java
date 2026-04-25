package com.swna.server.shop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.shop.dto.CreateShopRequest;
import com.swna.server.shop.entity.Shop;
import com.swna.server.shop.usecase.CreateShopUseCase;
import com.swna.server.shop.usecase.GetShopUseCase;
import com.swna.server.shop.usecase.ToggleShopStatusUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final CreateShopUseCase createShopUseCase;
    private final GetShopUseCase getShopUseCase;
    private final ToggleShopStatusUseCase toggleShopStatusUseCase;

    @PostMapping
    public Long create(@RequestBody CreateShopRequest request) {

        return createShopUseCase.execute(
                request.name(),
                request.address(),
                request.phone(),
                request.businessNo()
        );
    }

    @GetMapping("/{id}")
    public Shop get(@PathVariable Long id) {
        return getShopUseCase.execute(id);
    }

    @PatchMapping("/{id}/status")
    public void toggle(@PathVariable Long id,
                       @RequestParam boolean active) {

        toggleShopStatusUseCase.execute(id, active);
    }
}
