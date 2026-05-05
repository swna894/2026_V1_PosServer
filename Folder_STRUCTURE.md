com.swna.server
│
├── sale
│   ├── controller
│   │   └── SaleController.java
│   │
│   ├── usecase
│   │   ├── CreateSaleUseCase.java
│   │   ├── ProcessSaleUseCase.java   ← (주문+결제 통합 POS 핵심)
│   │
│   ├── domain
│   │   ├── Sale.java
│   │   ├── SaleItem.java
│   │   ├── SaleStatus.java
│   │   └── Discount.java
│   │
│   ├── entity
│   │   ├── PaymentEntity.java
│   │   ├── CashPaymentEntity.java
│   │   └── CardPaymentEntity.java
│   │
│   ├── dto
│   │   ├── SaleRequest.java
│   │   ├── SaleResponse.java
│   │   ├── SaleItemRequest.java
│   │   ├── DiscountRequest.java
│   │   └── PaymentRequest.java
│   │
│   ├── factory
│   │   └── PaymentFactory.java
│   │
│   ├── mapper
│   │   └── PaymentMapper.java
│   │
│   ├── event
│   │   └── SalePaidEvent.java
│   │
│   └── repository
│       └── SaleRepository.java
│
├── payment
│   └── model
│       ├── PaymentMethod.java
│       ├── CashPayment.java
│       └── CardPayment.java
│
└── product
    └── repository
        └── ProductRepository.java

# 전체 실행 흐름       
    [Client]
    SaleRequest
    ↓
    [Controller]
    SaleController
    ↓
    [UseCase]
    ProcessSaleUseCase
    ↓
    1. Product 조회
    2. Sale 생성
    3. Discount 계산
    4. Payment 생성
    5. 검증
    6. 저장
    ↓
    [Repository]
    DB 저장
    ↓
    [Event]
    SalePaidEvent



com.swna.server
│
├── sale
│   ├── controller
│   │   └── SaleController.java
│   │
│   ├── usecase
│   │   ├── CreateSaleUseCase.java
│   │   ├── PaySaleUseCase.java
│   │
│   ├── domain
│   │   ├── Sale.java
│   │   ├── SaleItem.java
│   │   ├── SaleStatus.java
│   │
│   ├── repository
│   │   └── SaleRepository.java
│   │
│   └── event
│       └── SalePaidEvent.java
│
├── payment
│   ├── controller (optional)
│   │
│   ├── usecase
│   │   └── ProcessPaymentUseCase.java
│   │
│   ├── domain
│   │   ├── entity
│   │   │   ├── PaymentEntity.java
│   │   │   ├── CashPaymentEntity.java
│   │   │   └── CardPaymentEntity.java
│   │   │
│   │   ├── model
│   │   │   ├── PaymentMethod.java
│   │   │   ├── CashPayment.java
│   │   │   └── CardPayment.java
│   │
│   ├── factory
│   │   └── PaymentFactory.java
│   │
│   ├── mapper
│   │   └── PaymentMapper.java
│   │
│   └── repository
│       └── PaymentRepository.java
│
├── discount
│   └── domain
│       └── Discount.java
│
└── common
    └── BaseEntity.java