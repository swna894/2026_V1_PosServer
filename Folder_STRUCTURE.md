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