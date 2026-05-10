package com.swna.server.sale.usecase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.sale.dto.request.SaleRequest;
import com.swna.server.sale.dto.request.DiscountRequest;
import com.swna.server.sale.dto.request.PaymentRequest;
import com.swna.server.sale.dto.response.SaleResponse;
import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.entity.SaleItem;
import com.swna.server.sale.entity.PaymentEntity;
import com.swna.server.sale.event.SaleCompletedEvent;
import com.swna.server.sale.factory.PaymentFactory;
import com.swna.server.sale.mapper.PaymentMapper;
import com.swna.server.sale.mapper.SaleItemMapper;
import com.swna.server.sale.mapper.SaleMapper;
import com.swna.server.sale.repository.SaleRepository;

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
    private final PaymentFactory paymentFactory;
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
            sale.getFinalAmount(),
            sale.getStatus()
        );
    }
}