# DTO 구조
   @Getter
   @Setter
   public class PaymentRequest {
      private String type;            // "CARD" 또는 "CASH"
      private BigDecimal amount;      // 물건 가격 또는 결제 대상 금액
      private BigDecimal receivedAmount; // (현금 시) 고객에게 받은 돈
      private BigDecimal cashOutAmount;  // (카드 시) 캐시아웃 요청 금액
      private String approvalNo;      // 카드 승인 번호
   }