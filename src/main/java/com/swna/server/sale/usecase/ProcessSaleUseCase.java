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

    private static final String RECEIPT_PREFIX = "RCP";
    private static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";
    private static final int RANDOM_SUFFIX_LENGTH = 4;

    /**
     * 전체 주문 + 결제 통합 처리
     */
    @Transactional
    public SaleResponse execute(SaleRequest request) {
        validateRequest(request);
        logRequestInfo(request);

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

    /**
     * 주문 생성
     */
    private Sale createSale(SaleRequest request) {
        List<SaleItem> items = createSaleItems(request);
        Sale sale = Sale.create(items);
        
        applyDiscountsIfPresent(sale, request.discounts());
        sale.assignReceiptNo(generateReceiptNumber());
        
        return sale;
    }

    /**
     * 결제 처리
     */
    private void processPayments(Sale sale, List<PaymentRequest> paymentRequests) {
        validatePaymentsExist(paymentRequests);
        
        List<PaymentEntity> payments = paymentRequests.stream()
                .map(paymentFactory::create)
                .map(paymentMapper::toEntity)
                .toList();

        sale.addPayments(payments);
        log.debug("Processed {} payments for sale ID: {}", payments.size(), sale.getId());
    }

    /**
     * 주문 완료 처리
     */
    private void completeSale(Sale sale) {
        sale.validatePayments();
        sale.complete();
        sale.recalculateAmounts();
        
        log.debug("Sale completed - ID: {}, Status: {}", sale.getId(), sale.getStatus());
    }

    // =========================
    // Helper Methods
    // =========================

    /**
     * 판매 아이템 생성
     */
    private List<SaleItem> createSaleItems(SaleRequest request) {

        System.err.println("SaleRequest = " + request);
        return request.items().stream()
                .map(saleItemMapper::toEntity)
                .toList();
    }

    /**
     * 할인 적용 (있는 경우에만)
     */
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

    /**
     * 저장 및 이벤트 발행
     */
    private Sale saveAndPublishEvent(Sale sale) {
        System.err.println("Sale = " + sale);
        Sale savedSale = saleRepository.save(sale);
        eventPublisher.publishEvent(new SaleCompletedEvent(savedSale.getId()));
        return savedSale;
    }

    /**
     * 영수증 번호 생성
     */
    private String generateReceiptNumber() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        String random = UUID.randomUUID()
                .toString()
                .substring(0, RANDOM_SUFFIX_LENGTH)
                .toUpperCase();
        
        return RECEIPT_PREFIX + timestamp + random;
    }

    // =========================
    // Validation Methods
    // =========================

    /**
     * 요청 검증
     */
    private void validateRequest(SaleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Sale request cannot be null");
        }
        
        validatePaymentsExist(request.payments());
    }

    /**
     * 결제 내역 존재 검증
     */
    private void validatePaymentsExist(List<com.swna.server.sale.dto.request.PaymentRequest> payments) {
        if (payments == null || payments.isEmpty()) {
            throw new IllegalArgumentException("At least one payment is required");
        }
    }

    // =========================
    // Logging Methods
    // =========================

    /**
     * 요청 정보 로깅
     */
    private void logRequestInfo(SaleRequest request) {
        log.info("Processing sale request - Items: {}, Payments: {}, Discounts: {}",
            request.items().size(),
            request.payments().size(),
            request.discounts() != null ? request.discounts().size() : 0
        );
    }

    /**
     * 주문 완료 로깅
     */
    private void logSaleCompletion(Sale sale) {
        log.info("Sale completed successfully - ID: {}, ReceiptNo: {}, PayableAmount: {}, Status: {}",
            sale.getId(),
            sale.getReceiptNo(),
            sale.getFinalAmount(),
            sale.getStatus()
        );
    }
}