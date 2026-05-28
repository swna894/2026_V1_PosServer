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

            case DELETE -> CashPaymentEntity.of(
                request.amount(),
                request.receivedAmount() != null ? request.receivedAmount() : request.amount()
            );
            
            // ✅ CARD 결제 처리 시 creditAmount, cashAmount를 함께 넘기도록 수정
            case CARD -> {
                BigDecimal creditAmount = request.receivedAmount() != null 
                    ? request.receivedAmount() 
                    : request.amount(); // 받은 금액이 없다면 전체 amount를 creditAmount로 산정

                BigDecimal cashAmount = request.cashoutAmount() != null // (필요 시 현금화 금액 매핑)
                    ? request.cashoutAmount() 
                    : BigDecimal.ZERO;

                String approvalNo = request.approvalNo() != null 
                    ? request.approvalNo() 
                    : generateApprovalNo();

                yield CardPaymentEntity.of(
                        request.amount(),
                        creditAmount,
                        cashAmount,
                        approvalNo,
                        maskCardNumber(request.cardNumber())
                    );  
            }
            
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
        //return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
        return cardNumber;
    }
    
    public List<PaymentResponse> toResponseList(List<PaymentEntity> entities) {
        return PaymentResponse.fromList(entities);
    }
    
    private String generateApprovalNo() {
        return "APR" + System.currentTimeMillis();
    }
}