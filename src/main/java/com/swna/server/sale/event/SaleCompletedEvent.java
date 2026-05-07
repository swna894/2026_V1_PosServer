package com.swna.server.sale.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaleCompletedEvent {
    private final Long saleId;
}