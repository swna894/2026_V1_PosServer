package com.swna.server.unpack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.unpack.model.Unpack;
import com.swna.server.unpack.model.UnpackItem;
import com.swna.server.unpack.repository.UnpackItemRepository;
import com.swna.server.unpack.repository.UnpackRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestUnpackItemController {

    private final ProductRepository productRepository;
    private final UnpackRepository inspectionRepository;
    private final UnpackItemRepository inspectionItemRepository;
    
    @PutMapping("/unpackItem")
    public UnpackItem put(@RequestBody UnpackItem item) {
        Optional<UnpackItem> optionalItem = inspectionItemRepository.findById(item.getId());
        if (optionalItem.isPresent()) {
            UnpackItem findedItem = optionalItem.get();
            findedItem.setQty(item.getQty());
            findedItem.setConfirm(item.getConfirm());
            findedItem.setComment(item.getComment());
            findedItem.setAmount(item.getAmount());
            findedItem.setPriceout(item.getPriceout());
            findedItem.setBarcode(item.getBarcode());
            findedItem.setDescription(item.getDescription());
            return inspectionItemRepository.save(findedItem);
        }
        return null;    
    }

    @PutMapping("/unpackItems")
    @Transactional
    public List<UnpackItem> puts(@RequestBody List<UnpackItem> items) {        
        List<Product> products = new ArrayList<>();
        
        for (UnpackItem item : items) {
            Product product = productRepository.findByBarcode(item.getBarcode()).orElse(null);
            
            if (product != null) {
                product = updateProduct(item, product);        
            } else {
                // Product 생성 팩토리 메서드 활용
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
        
        Unpack inspection = null;
        if (!items.isEmpty() && items.get(0).getInvoice() != null) {
            inspection = inspectionRepository.findByInvoice(items.get(0).getInvoice()); 
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
                if (inspection != null) {
                    item.setUnpack(inspection);
                }
            } 
        }
        
        return inspectionItemRepository.saveAll(items);
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