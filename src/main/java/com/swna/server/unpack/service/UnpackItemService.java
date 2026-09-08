package com.swna.server.unpack.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.product.entity.Product;
import com.swna.server.product.entity.ProductStock;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.product.repository.ProductStockRepository;
import com.swna.server.unpack.dto.UnpackItemDto;
import com.swna.server.unpack.model.Unpack;
import com.swna.server.unpack.model.UnpackItem;
import com.swna.server.unpack.repository.UnpackItemRepository;
import com.swna.server.unpack.repository.UnpackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class UnpackItemService {

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final UnpackRepository unpackRepository;
    private final UnpackItemRepository unpackItemRepository;

    @Transactional
    public UnpackItemDto updateUnpackItem(UnpackItemDto dto) {
        if (dto.id() == null) {
            throw ExceptionUtils.invalidInput("id", "수정 요청에는 id가 반드시 포함되어야 합니다.");
        }

        UnpackItem findedItem = unpackItemRepository.findById(dto.id())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("UnpackItem", String.valueOf(dto.id())));

        findedItem.setQty(dto.qty());
        findedItem.setConfirm(dto.confirm());
        findedItem.setComment(dto.comment());
        findedItem.setAmount(dto.amount());
        findedItem.setPriceout(dto.priceout());
        findedItem.setBarcode(dto.barcode());
        findedItem.setDescription(dto.description());

        UnpackItem saved = unpackItemRepository.save(findedItem);
        return UnpackItemDto.fromEntity(saved);
    }

    // =========================================================================
    // 🔥 [Refactored Main Method] 오케스트레이션 역할만 수행 (LOC: ~20, Complexity: 2)
    // =========================================================================
    @Transactional
    public List<UnpackItemDto> updateUnpackItems(List<UnpackItemDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();

        // 1. 필요한 데이터 일괄 조회 (지역 변수 4개)
        Map<Long, UnpackItem> existingItemMap = loadExistingItems(dtos);
        Map<String, Product> productMap = loadProducts(dtos);
        Map<Long, ProductStock> stockMap = loadStocks(productMap);
        Unpack unpack = findUnpackByDtos(dtos);

        // 2. 단일 아이템 파이프라인 처리 (지역 변수 1개)
        List<UnpackItem> updatedItems = dtos.stream()
                .map(dto -> processSingleItem(dto, existingItemMap, productMap, stockMap, unpack))
                .toList();

        // 3. 일괄 저장 및 변환 반환
        return unpackItemRepository.saveAll(updatedItems).stream()
                .map(UnpackItemDto::fromEntity)
                .toList();
    }

    // =========================================================================
    // 🛠️ Private Helper Methods (복잡도/중첩/변수 분산 처리)
    // =========================================================================

    private UnpackItem processSingleItem(
            UnpackItemDto dto,
            Map<Long, UnpackItem> existingItemMap,
            Map<String, Product> productMap,
            Map<Long, ProductStock> stockMap,
            Unpack unpack
    ) {
        UnpackItem item = resolveEntity(dto, existingItemMap);
        String barcode = item.getBarcode();
        int qty = item.getQty() != null ? item.getQty() : 0;

        Product product = (barcode != null && !barcode.isBlank())
                ? getOrCreateProductAndStock(item, qty, productMap, stockMap)
                : null;

        updateUnpackItemFields(item, product, unpack);
        return item;
    }

    private UnpackItem resolveEntity(UnpackItemDto dto, Map<Long, UnpackItem> existingItemMap) {
        if (dto.id() != null && existingItemMap.containsKey(dto.id())) {
            UnpackItem existing = existingItemMap.get(dto.id());
            existing.applyUpdate(dto.toEntity());
            return existing;
        }
        return dto.toEntity();
    }

    private Product getOrCreateProductAndStock(
            UnpackItem item,
            int qty,
            Map<String, Product> productMap,
            Map<Long, ProductStock> stockMap
    ) {
        String barcode = item.getBarcode();
        Product product = productMap.get(barcode);

        if (product != null) {
            updateProductInfo(item, product);
            updateExistingStock(product, item, qty, stockMap);
        } else {
            product = createNewProductAndStock(item, qty, productMap, stockMap);
        }
        return product;
    }

    private void updateExistingStock(Product product, UnpackItem item, int qty, Map<Long, ProductStock> stockMap) {
        ProductStock stock = stockMap.computeIfAbsent(product.getId(),
                id -> productStockRepository.save(
                        ProductStock.create(
                                product, 0,
                                item.getMinStock() != null ? item.getMinStock() : 6,
                                100,
                                item.getMinOrderQty() != null ? item.getMinOrderQty() : 12
                        )
                ));
        if (qty > 0) {
            stock.increase(qty);
            stock.touchLastOrderedAt();
        }
    }

    private Product createNewProductAndStock(
            UnpackItem item,
            int qty,
            Map<String, Product> productMap,
            Map<Long, ProductStock> stockMap
    ) {
        BigDecimal price = item.getPriceout() != null ? BigDecimal.valueOf(item.getPriceout()) : BigDecimal.ZERO;
        BigDecimal cost = item.getPricein() != null ? BigDecimal.valueOf(item.getPricein()) : BigDecimal.ZERO;

        Product product = Product.create(
                item.getCode() != null && !item.getCode().isBlank() ? item.getCode() : "TEMP_" + item.getBarcode(),
                item.getDescription() != null && !item.getDescription().isBlank() ? item.getDescription() : "No Description",
                price,
                cost,
                item.getBarcode(),
                item.getCategory()
        );
        product = productRepository.save(product);
        productMap.put(item.getBarcode(), product);

        ProductStock newStock = ProductStock.create(
                product,
                qty,
                item.getMinStock() != null ? item.getMinStock() : 6,
                100,
                item.getMinOrderQty() != null ? item.getMinOrderQty() : 12
        );
        productStockRepository.save(newStock);
        stockMap.put(product.getId(), newStock);

        return product;
    }

    private void updateUnpackItemFields(UnpackItem item, Product product, Unpack unpack) {
        if (product != null) {
            item.setPriceout(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
            if (product.getDescription() != null) item.setDescription(product.getDescription());
            if (product.getAbbr() != null) item.setAbbr(product.getAbbr());
        }

        // isSaved = true 설정
        item.setIsSaved(true);

        if (unpack != null) {
            item.setUnpack(unpack);
        }
    }

    private void updateProductInfo(UnpackItem item, Product product) {
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            product.changeName(item.getDescription());
        }
        if (item.getPriceout() != null) {
            product.changePrice(BigDecimal.valueOf(item.getPriceout()));
        }
        if (item.getCategory() != null) {
            product.changeCategory(item.getCategory());
        }
    }

    // =========================================================================
    // 📦 Bulk Data Loader Methods
    // =========================================================================

    private Map<Long, UnpackItem> loadExistingItems(List<UnpackItemDto> dtos) {
        List<Long> itemIds = dtos.stream()
                .map(UnpackItemDto::id)
                .filter(Objects::nonNull)
                .toList();

        return unpackItemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(UnpackItem::getId, item -> item));
    }

    private Map<String, Product> loadProducts(List<UnpackItemDto> dtos) {
        List<String> barcodes = dtos.stream()
                .map(UnpackItemDto::barcode)
                .filter(b -> b != null && !b.isBlank())
                .distinct()
                .toList();

        return productRepository.findAllByBarcodeIn(barcodes).stream()
                .collect(Collectors.toMap(Product::getBarcode, p -> p, (a, b) -> a));
    }

    private Map<Long, ProductStock> loadStocks(Map<String, Product> productMap) {
        List<Long> productIds = productMap.values().stream().map(Product::getId).toList();
        return productStockRepository.findAllByProductIdIn(productIds).stream()
                .collect(Collectors.toMap(s -> s.getProduct().getId(), s -> s));
    }

    private Unpack findUnpackByDtos(List<UnpackItemDto> dtos) {
        return dtos.stream()
                .map(UnpackItemDto::invoice)
                .filter(Objects::nonNull)
                .findFirst()
                .map(unpackRepository::findByInvoice)
                .orElse(null);
    }
}