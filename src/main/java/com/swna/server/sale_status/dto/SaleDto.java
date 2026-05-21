package com.swna.server.sale_status.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SaleDto {
    private String id;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal costAmount;
    private BigDecimal saleAmount;
    
    private BigDecimal creditAmount;
    private BigDecimal cashoutAmount;
    private BigDecimal cashAmount;
    private BigDecimal receivedAmount;
    private BigDecimal changeAmount;
    
    private String receiptNo;
    private String paymentType;
    private String cardNumber;
    private String cashier;
    private String paymentDateTime;
}
