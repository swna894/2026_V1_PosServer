package com.swna.server.unpack;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.unpack.model.Unpack;
import com.swna.server.unpack.model.UnpackItem;
import com.swna.server.unpack.repository.UnpackRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unpack")
@RequiredArgsConstructor
public class RestUnpackController {

    private final ProductRepository productRepository;
    private final UnpackRepository unpackRepository;

    @PostMapping
    public Unpack post(@RequestBody Unpack unpack) {
        if (unpack.getItems() != null) {
            unpack.getItems().forEach(item -> {
                item.setUnpack(unpack);
                isNewProduct(item, unpack.getSync());       
            });
        }
        return unpackRepository.save(unpack);
    }

    private void isNewProduct(UnpackItem item, Boolean sync) {
        // Optional 처리
        Product product = productRepository.findByBarcode(item.getBarcode()).orElse(null);
        
        if (product != null) {
            // Product 엔티티 구조에 맞추어 price 사용 (BigDecimal -> Double 변환)
            item.setPriceout(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
            
            // 기본값 설정 (ProductStock 연동 전 fallback 수치)
            item.setMinOrderQty(12);
            item.setMinStock(6);
            
            if (sync == null || !sync) {
                item.setAbbr(product.getAbbr());
            }
            item.setIsNew(false);
        } else {
            item.setIsNew(true);
            item.setMinOrderQty(12);
            item.setMinStock(6);
        }
    }

    @PutMapping
    public List<Unpack> put(@RequestBody List<Unpack> unpacks) {
        unpacks.forEach(item -> {
            if (item.getItems() != null) {
                item.getItems().forEach(product -> product.setUnpack(item));
            }
        });
        return unpackRepository.saveAll(unpacks);
    }

    @GetMapping
    public List<Unpack> getLocalDate(@RequestParam Map<String, String> parameter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = LocalDate.parse(parameter.get("start"), formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(parameter.get("end"), formatter).atTime(23, 59, 59);

        // UnpackRepository에 선언된 올바른 메서드명 호출
        return unpackRepository.findByUpdatedBetweenOrderByUpdatedDesc(start, end);
    }

    @DeleteMapping
    @Transactional
    public void deletes(@RequestBody List<Unpack> unpacks) {
        for (Unpack unpack : unpacks) {
            if (unpack.getInvoice() != null) {
                unpackRepository.deleteByInvoice(unpack.getInvoice());
            }
        }
    }
}