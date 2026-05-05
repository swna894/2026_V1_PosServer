# pos에서 결재 3가지
  - 현금 : 받은돈, 거스름돈, 원제품가격, 판매가격
  - 카드 + 현금 : 카드 결재금액, 현금, 원제품각겨, 판매가격
  - 카드  + cashout : 카드 결재금액, cashout 금액, 원제품가격, 판매가격,

  - 제품 판매내역 List<SaleItem>

  - 상기를 서버에 결재를 요청하고 저장이 성공하면 영수증 번호를 받아서 성공알림, 실패하면 재 요청 

# 용어 정의
  - Sale : 판매에 대한 정보
  - Order : 판매를 위한 주문정보
  - Product : 판매 제품

   public class PaymentRequest {
      private String type;            // "CARD" 또는 "CASH"
      private BigDecimal amount;      // 물건 가격 또는 결제 대상 금액
      private BigDecimal receivedAmount; // (현금 시) 고객에게 받은 돈
      private BigDecimal cashOutAmount;  // (카드 시) 캐시아웃 요청 금액
      private String approvalNo;      // 카드 승인 번호
   }