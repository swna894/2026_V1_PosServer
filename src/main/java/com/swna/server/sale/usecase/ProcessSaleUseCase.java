package com.swna.server.sale.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.sale.dto.request.DiscountRequest;
import com.swna.server.sale.dto.request.PaymentRequest;
import com.swna.server.sale.dto.request.SaleRequest;
import com.swna.server.sale.dto.response.SaleResponse;
import com.swna.server.sale.entity.CardPaymentEntity;
import com.swna.server.sale.entity.CashPaymentEntity;
import com.swna.server.sale.entity.CashoutPaymentEntity;
import com.swna.server.sale.entity.PaymentEntity;
import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.entity.SaleItem;
import com.swna.server.sale.event.SaleCompletedEvent;
import com.swna.server.sale.mapper.PaymentMapper;
import com.swna.server.sale.mapper.SaleItemMapper;
import com.swna.server.sale.mapper.SaleMapper;
import com.swna.server.sale.repository.SaleRepository;
import com.swna.server.sale_status.dto.SaleDto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessSaleUseCase {

    private final SaleItemMapper saleItemMapper;
    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher eventPublisher;

    private static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";
    private static final int RANDOM_SUFFIX_LENGTH = 4;

    @Transactional
    public SaleResponse execute(SaleRequest request) {
        validateRequest(request);
        logRequestInfo(request);
        
        // ✅ cardNumber 로깅 추가
        logCardNumbers(request);

        Sale sale = createSale(request);
        processPayments(sale, request.payments());
        completeSale(sale);

        Sale savedSale = saveAndPublishEvent(sale);
        
        logSaleCompletion(savedSale);
        return saleMapper.toResponse(savedSale);
    }

    // =========================
    // Core Business Methods
    // =========================

    private Sale createSale(SaleRequest request) {
        List<SaleItem> items = createSaleItems(request);
        Sale sale = Sale.create(items);
        
        applyDiscountsIfPresent(sale, request.discounts());
        sale.assignReceiptNo(generateReceiptNumber());
        
        return sale;
    }

    /**
     * ✅ 결제 처리 (cardNumber 포함)
     */
    private void processPayments(Sale sale, List<PaymentRequest> paymentRequests) {
        validatePaymentsExist(paymentRequests);
        
        List<PaymentEntity> payments = paymentRequests.stream()
                .map(paymentMapper::toEntity)  // PaymentMapper 사용
                .peek(payment -> logPaymentInfo(payment, paymentRequests))
                .toList();

        sale.addPayments(payments);
        log.debug("Processed {} payments for sale", payments.size());
    }

    private void completeSale(Sale sale) {
        sale.validatePayments();
        sale.complete();
        sale.recalculateAmounts();
        
        log.debug("Sale completed - ID: {}, Status: {}", sale.getId(), sale.getStatus());
    }

    // =========================
    // Helper Methods
    // =========================

    private List<SaleItem> createSaleItems(SaleRequest request) {
        return request.items().stream()
                .map(saleItemMapper::toEntity)
                .toList();
    }

    private void applyDiscountsIfPresent(Sale sale, List<DiscountRequest> discountRequests) {
        if (discountRequests == null || discountRequests.isEmpty()) {
            return;
        }

        var discounts = discountRequests.stream()
                .map(DiscountRequest::toDomain)
                .toList();
        
        sale.applyDiscounts(discounts);
        sale.recalculateAmounts();
        
        log.debug("Applied {} discounts to sale", discounts.size());
    }

    private Sale saveAndPublishEvent(@NonNull Sale sale) {
        Sale savedSale = saleRepository.save(sale);
        eventPublisher.publishEvent(new SaleCompletedEvent(savedSale.getId()));
        return savedSale;
    }

    private String generateReceiptNumber() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        String random = UUID.randomUUID()
                .toString()
                .substring(0, RANDOM_SUFFIX_LENGTH)
                .toUpperCase();
        
        return timestamp + "_" + random;
    }

    // =========================
    // Validation & Logging Methods
    // =========================

    private void validateRequest(SaleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Sale request cannot be null");
        }
        validatePaymentsExist(request.payments());
    }

    private void validatePaymentsExist(List<PaymentRequest> payments) {
        if (payments == null || payments.isEmpty()) {
            throw new IllegalArgumentException("At least one payment is required");
        }
    }

    /**
     * ✅ cardNumber 로깅 (마스킹 처리)
     */
    private void logCardNumbers(SaleRequest request) {
        request.payments().stream()
            .filter(p -> p.cardNumber() != null && !p.cardNumber().isBlank())
            .forEach(p -> {
                String masked = maskCardNumber(p.cardNumber());
                log.info("Payment with card number: {}", masked);
            });
    }
    
    private void logPaymentInfo(PaymentEntity payment, List<PaymentRequest> requests) {
        if (payment instanceof com.swna.server.sale.entity.CardPaymentEntity cardPayment) {
            log.info("Card payment saved - amount: {}, cardNumber: {}", 
                cardPayment.getAmount(),
                cardPayment.getCardNumber()  // 이미 마스킹된 상태
            );
        }
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        //return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
        return cardNumber;
    
    }

    private void logRequestInfo(SaleRequest request) {
        log.info("Processing sale request - Items: {}, Payments: {}, Discounts: {}",
            request.items().size(),
            request.payments().size(),
            request.discounts() != null ? request.discounts().size() : 0
        );
    }

    private void logSaleCompletion(Sale sale) {
        log.info("Sale completed successfully - ID: {}, ReceiptNo: {}, FinalAmount: {}, Status: {}",
            sale.getId(),
            sale.getReceiptNo(),
            sale.getSaleAmount(),
            sale.getStatus()
        );
    }

    // =========================
    // Sale Status (필요한 것만)
    // =========================
    /**
     * 기간별 판매 목록 조회 (결제 시간 기준)
     * 복합 결제를 감안하여 합산 정보 반환
     */
    public List<SaleDto> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = saleRepository.findByPaymentDateTimeBetweenWithPayments(startDate, endDate);
        return sales.stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Sale 엔티티를 SaleDto로 변환 (복합 결제 합산)
     * 여러 결제가 있을 경우 모든 결제 정보를 합산하여 하나의 DTO로 반환
     */
    private SaleDto convertToDto(Sale sale) {
        SaleDto dto = new SaleDto();
        
        // Sale 기본 정보
        dto.setId(String.valueOf(sale.getId()));
        dto.setReceiptNo(sale.getReceiptNo());

        dto.setOriginalAmount(sale.getOriginalAmount());
        dto.setDiscountAmount(sale.getDiscountAmount());
        dto.setCostAmount(sale.getCostAmount());
        dto.setSaleAmount(sale.getSaleAmount());
        //dto.setCashier(sale.getCashier());
        
        // 결제 정보가 없으면 빈값 반환
        if (sale.getPayments() == null || sale.getPayments().isEmpty()) {
            return dto;
        }
        
        // 복합 결제 정보 합산
        BigDecimal totalReceived = BigDecimal.ZERO;
        BigDecimal totalCashout = BigDecimal.ZERO;
        BigDecimal totalCash = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalChange = BigDecimal.ZERO;
        List<String> paymentTypes = new ArrayList<>();
        List<String> cardNumbers = new ArrayList<>();
        List<String> approvalNos = new ArrayList<>();
        LocalDateTime lastPaymentTime = null;
        
        for (PaymentEntity payment : sale.getPayments()) {
            // 받은 금액 합산
            if (payment instanceof CashPaymentEntity cash) {
                paymentTypes.add("CASH");
                totalReceived = totalReceived.add(cash.getReceivedAmount());
                totalChange = totalChange.add(cash.getChangeAmount());
                if (cash.getCashAmount() != null) {
                    totalCash = totalCash.add(cash.getCashAmount());
                }
                
            } else if (payment instanceof CardPaymentEntity card) {
                paymentTypes.add("CARD");
                totalReceived = totalReceived.add(payment.getAmount());
                if (card.getCreditAmount() != null) {
                    totalCredit = totalCredit.add(card.getCreditAmount());
                }
                if (card.getCardNumber() != null && !card.getCardNumber().isEmpty()) {
                    cardNumbers.add(card.getCardNumber());
                }
                if (card.getApprovalNo() != null && !card.getApprovalNo().isEmpty()) {
                    approvalNos.add(card.getApprovalNo());
                }
                
            } else if (payment instanceof CashoutPaymentEntity cashout) {
                paymentTypes.add("CASHOUT");
                totalReceived = totalReceived.add(cashout.getCreditAmount());
                totalCashout = totalCashout.add(cashout.getCashoutAmount());
                totalCredit = cashout.getCreditAmount();  
                if (cashout.getCardNumber() != null && !cashout.getCardNumber().isEmpty()) {
                    cardNumbers.add(cashout.getCardNumber());
                }
                if (cashout.getApprovalNo() != null && !cashout.getApprovalNo().isEmpty()) {
                    approvalNos.add(cashout.getApprovalNo());
                }
            }
            
            // 가장 늦은 결제 시간
            if (lastPaymentTime == null || payment.getPaymentDateTime().isAfter(lastPaymentTime)) {
                lastPaymentTime = payment.getPaymentDateTime();
            }
        }
        
        dto.setReceivedAmount(totalReceived);
        dto.setCashoutAmount(totalCashout);
        dto.setCreditAmount(totalCredit);
        dto.setChangeAmount(totalChange);
        dto.setPaymentType(paymentTypes.toString().replaceAll("[\\[\\]]", ""));
        dto.setCashAmount(totalCash);
        dto.setCardNumber("" + cardNumbers);
        if (lastPaymentTime != null) {
            dto.setPaymentDateTime(lastPaymentTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        }
        return dto;
    }
}