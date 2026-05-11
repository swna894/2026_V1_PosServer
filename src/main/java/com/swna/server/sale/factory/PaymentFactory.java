package com.swna.server.sale.factory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.swna.server.sale.dto.request.PaymentRequest;
import com.swna.server.sale.entity.CardPaymentEntity;
import com.swna.server.sale.entity.CashPaymentEntity;
import com.swna.server.sale.entity.CashoutPaymentEntity;
import com.swna.server.sale.entity.PaymentEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentFactory {

    public PaymentRequestWrapper create(PaymentRequest request) {
        log.debug("Creating payment entity from request: type={}, amount={}", 
            request.type(), request.amount());
        
        PaymentEntity entity = switch (request.type()) {
            case CASH -> createCashPayment(request);
            case CARD -> createCardPayment(request);
            case CASHOUT -> createCashoutPayment(request);  // ✅ 추가
        };
        
        return new PaymentRequestWrapper(entity, request);
    }
    
    private PaymentEntity createCashPayment(PaymentRequest request) {
        BigDecimal receivedAmount = request.receivedAmount() != null 
            ? request.receivedAmount() 
            : request.amount();
            
        return CashPaymentEntity.of(
            request.amount(),
            receivedAmount
        );
    }
    
    private PaymentEntity createCardPayment(PaymentRequest request) {
        return CardPaymentEntity.of(
            request.amount(),
            request.approvalNo() != null ? request.approvalNo() : generateApprovalNo(),
            request.cardNumber(),
            null
        );
    }
    
    // ✅ CASHOUT 결제 생성 메서드 추가
    private PaymentEntity createCashoutPayment(PaymentRequest request) {
        BigDecimal receivedAmount = request.receivedAmount() != null 
            ? request.receivedAmount() 
            : request.amount();
            
        BigDecimal cashoutAmount = request.cashoutAmount() != null
            ? request.cashoutAmount()
            : BigDecimal.ZERO;
        
        String approvalNo = request.approvalNo() != null 
            ? request.approvalNo() 
            : generateApprovalNo();
        
        // ✅ CardPaymentEntity 생성 방식과 동일하게 cardNumber 전달
        return CashoutPaymentEntity.of(
            request.amount(),
            receivedAmount,
            cashoutAmount,
            approvalNo,
            request.cardNumber()  // ✅ 여기서 cardNumber 전달
        );
    }
    private String generateApprovalNo() {
        return "APR" + System.currentTimeMillis();
    }
    
    public record PaymentRequestWrapper(PaymentEntity entity, PaymentRequest originalRequest) {}
}