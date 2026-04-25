com.swna.server
│
├── order
│   ├── controller
│   │   └── OrderController.java
│   │
│   ├── usecase
│   │   ├── CreateOrderUseCase.java
│   │   ├── ProcessOrderUseCase.java   ← (주문+결제 통합 POS 핵심)
│   │
│   ├── domain
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── OrderStatus.java
│   │   └── Discount.java
│   │
│   ├── entity
│   │   ├── PaymentEntity.java
│   │   ├── CashPaymentEntity.java
│   │   └── CardPaymentEntity.java
│   │
│   ├── dto
│   │   ├── OrderRequest.java
│   │   ├── OrderResponse.java
│   │   ├── OrderItemRequest.java
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
│   │   └── OrderPaidEvent.java
│   │
│   └── repository
│       └── OrderRepository.java
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
    OrderRequest
    ↓
    [Controller]
    OrderController
    ↓
    [UseCase]
    ProcessOrderUseCase
    ↓
    1. Product 조회
    2. Order 생성
    3. Discount 계산
    4. Payment 생성
    5. 검증
    6. 저장
    ↓
    [Repository]
    DB 저장
    ↓
    [Event]
    OrderPaidEvent



com.swna.server
│
├── order
│   ├── controller
│   │   └── OrderController.java
│   │
│   ├── usecase
│   │   ├── CreateOrderUseCase.java
│   │   ├── PayOrderUseCase.java
│   │
│   ├── domain
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── OrderStatus.java
│   │
│   ├── repository
│   │   └── OrderRepository.java
│   │
│   └── event
│       └── OrderPaidEvent.java
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