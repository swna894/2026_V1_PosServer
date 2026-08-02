package com.swna.server.unpack.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
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

    @Transactional
    public List<UnpackItemDto> updateUnpackItems(List<UnpackItemDto> dtos) {
        List<UnpackItem> items = dtos.stream()
                .map(UnpackItemDto::toEntity)
                .collect(Collectors.toList());

        List<Product> products = new ArrayList<>();

        for (UnpackItem item : items) {
            Product product = productRepository.findByBarcode(item.getBarcode()).orElse(null);

            if (product != null) {
                product = updateProduct(item, product);
            } else {
                BigDecimal price = item.getPriceout() != null ? BigDecimal.valueOf(item.getPriceout()) : BigDecimal.ZERO;
                BigDecimal cost = item.getPricein() != null ? BigDecimal.valueOf(item.getPricein()) : BigDecimal.ZERO;

                product = Product.create(
                    item.getCode() != null ? item.getCode() : "TEMP_CODE",
                    item.getDescription() != null ? item.getDescription() : "No Description",
                    price,
                    cost,
                    item.getBarcode(),
                    item.getCategory()
                );
            }
            products.add(product);
        }
        products = productRepository.saveAll(products);

        Unpack unpack = null;
        if (!items.isEmpty() && items.get(0).getInvoice() != null) {
            unpack = unpackRepository.findByInvoice(items.get(0).getInvoice());
        }

        for (Product product : products) {
            UnpackItem item = items.stream()
                    .filter(i -> i.getBarcode() != null && i.getBarcode().equals(product.getBarcode()))
                    .findAny()
                    .orElse(null);

            if (item != null) {
                item.setPriceout(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
                item.setDescription(product.getDescription());
                item.setAbbr(product.getAbbr());
                item.setIsSaved(true);
                if (unpack != null) {
                    item.setUnpack(unpack);
                }
            }
        }

        return unpackItemRepository.saveAll(items).stream()
                .map(UnpackItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    private Product updateProduct(UnpackItem item, Product product) {
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            product.changeName(item.getDescription());
        }
        if (item.getPriceout() != null) {
            product.changePrice(BigDecimal.valueOf(item.getPriceout()));
        }
        if (item.getCategory() != null) {
            product.changeCategory(item.getCategory());
        }
        return product;
    }
}