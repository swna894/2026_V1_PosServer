package com.swna.server.sale.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.swna.server.sale.dto.request.PaymentRequest;
import com.swna.server.sale.dto.response.PaymentResponse;
import com.swna.server.sale.entity.CardPaymentEntity;
import com.swna.server.sale.entity.CashPaymentEntity;
import com.swna.server.sale.entity.CashoutPaymentEntity;
import com.swna.server.sale.entity.PaymentEntity;
import com.swna.server.sale.factory.PaymentFactory.PaymentRequestWrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMapper {

    public PaymentEntity toEntity(PaymentRequestWrapper wrapper) {
        return wrapper.entity();
    }
    
    public PaymentEntity toEntity(PaymentRequest request) {
        log.debug("Converting PaymentRequest to Entity: type={}, cardNumber={}", 
            request.type(), request.cardNumber());
        
        return switch (request.type()) {
            case CASH -> CashPaymentEntity.of(
                request.amount(),
                request.receivedAmount() != null ? request.receivedAmount() : request.amount()
            );
            
            case CARD -> CardPaymentEntity.of(
                request.amount(),
                request.approvalNo() != null ? request.approvalNo() : generateApprovalNo(),
                maskCardNumber(request.cardNumber()),
                null
            );
            
            case CASHOUT -> {
                BigDecimal receivedAmount = request.receivedAmount() != null 
                    ? request.receivedAmount() 
                    : request.amount();
                    
                BigDecimal cashoutAmount = request.cashoutAmount() != null
                    ? request.cashoutAmount()
                    : BigDecimal.ZERO;
                
                String approvalNo = request.approvalNo() != null 
                    ? request.approvalNo() 
                    : generateApprovalNo();
                
                yield CashoutPaymentEntity.of(
                    request.amount(),
                    receivedAmount,
                    cashoutAmount,
                    approvalNo,
                    maskCardNumber(request.cardNumber())  // ✅ cardNumber 전달
                );
            }
        };
    }

        
    // ✅ 마스킹 메서드 추가
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            return "****";
        }
        return cardNumber;
    }
    
    public List<PaymentResponse> toResponseList(List<PaymentEntity> entities) {
        return PaymentResponse.fromList(entities);
    }
    
    private String generateApprovalNo() {
        return "APR" + System.currentTimeMillis();
    }
}