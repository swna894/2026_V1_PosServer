package com.swna.server.unpack.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.unpack.dto.UnpackDto;
import com.swna.server.unpack.model.Unpack;
import com.swna.server.unpack.model.UnpackItem;
import com.swna.server.unpack.repository.UnpackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class UnpackService {

    private final ProductRepository productRepository;
    private final UnpackRepository unpackRepository;

    @Transactional
    public UnpackDto createUnpack(UnpackDto dto) {
        Unpack unpack = dto.toNewEntity();

        dto.items().forEach(itemDto -> {
            UnpackItem item = itemDto.toNewEntity();
            unpack.addItem(item);
            syncProductInfo(item, unpack.getSync());
        });

        Unpack saved = unpackRepository.save(unpack);
        return UnpackDto.fromEntity(saved);
    }

    @Transactional
    public List<UnpackDto> updateUnpacks(List<UnpackDto> dtos) {
        List<Unpack> unpacks = dtos.stream().map(dto -> {
            if (dto.id() == null) {
                throw ExceptionUtils.invalidInput("id", "수정 요청에는 id가 반드시 포함되어야 합니다.");
            }
            Unpack unpack = dto.toEntity();
            if (dto.items() != null) {
                dto.items().forEach(itemDto -> unpack.addItem(itemDto.toEntity()));
            }
            return unpack;
        }).collect(Collectors.toList());

        return unpackRepository.saveAll(unpacks).stream()
                .map(UnpackDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UnpackDto> getUnpacksByPeriod(Map<String, String> parameter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start;
        LocalDateTime end;
        try {
            start = LocalDate.parse(parameter.get("start"), formatter).atStartOfDay();
            end = LocalDate.parse(parameter.get("end"), formatter).atTime(23, 59, 59);
        } catch (Exception e) {
            throw ExceptionUtils.invalidInput("start/end", "yyyy-MM-dd 형식이어야 합니다.");
        }

        return unpackRepository.findByUpdatedBetweenOrderByUpdatedDesc(start, end)
                .stream()
                .map(UnpackDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUnpacks(List<UnpackDto> dtos) {
        for (UnpackDto dto : dtos) {
            if (dto.invoice() != null) {
                unpackRepository.deleteByInvoice(dto.invoice());
            }
        }
    }

    private void syncProductInfo(UnpackItem item, Boolean sync) {
        Product product = productRepository.findByBarcode(item.getBarcode()).orElse(null);

        if (product != null) {
            item.setPriceout(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
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
}