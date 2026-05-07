# API 에러 처리 시스템 명세 (Error Handling Specification)

이 문서는 시스템 전역에서 발생하는 예외를 체계적으로 관리하고 사용자에게 일관된 응답 규격을 제공하기 위한 에러 처리 아키텍처를 정의합니다.

---

## 1. 시스템 구성 요소

### 1.1 ErrorCode (에러 정의)
시스템에서 발생 가능한 모든 에러 상황을 도메인별로 분류하여 중앙 관리합니다.
* **구성:** 에러 코드(String), 메시지(String), HTTP 상태 코드(HttpStatus).
* **도메인 분류:** `COMMON(000)`, `AUTH(100)`, `USER(200)`, `PRODUCT(300)`, `ORDER(400)`, `SALE(500)`, `PAYMENT(600)`, `STORE(700)`, `FILE(800)`.
* **기능:** 팩토리 메서드(`withDetail`, `withMessage` 등)를 통해 예외 객체 생성을 지원합니다.

### 1.2 BusinessException (커스텀 예외)
비즈니스 로직 실행 중 발생하는 예외를 처리하기 위한 전용 클래스입니다.
* **특징:** `RuntimeException`을 상속하며, 에러 발생 당시의 시간(timestamp), 추적 ID(traceId), 상세 데이터(details)를 포함합니다.
* **생성 방식:** 유연한 객체 생성을 위해 Builder 패턴과 정적 팩토리 메서드(`of`)를 제공합니다.

### 1.3 GlobalExceptionHandler (중앙 컨트롤러)
`@RestControllerAdvice`를 사용하여 애플리케이션 전역에서 발생하는 예외를 포착하고 처리합니다.
* **로깅:** 에러의 심각도(4xx/5xx)에 따라 로그 레벨을 동적으로 결정하여 기록합니다.
* **처리 범위:** 비즈니스 예외, 데이터 유효성 검사 오류, 파라미터 누락, JSON 파싱 오류 등.

### 1.4 ErrorResponseFactory (응답 생성기)
클라이언트에게 반환할 표준 응답 객체(`ApiResponse`)를 생성하는 역할을 수행합니다.
* **기능:** `HttpServletRequest` 정보를 활용해 에러 발생 경로(path)와 메서드 정보를 자동으로 주입합니다.

---

## 2. 에러 처리 흐름 (Workflow)

1. **발생:** 비즈니스 로직에서 조건 미충족 시 예외를 던짐 (예: `throw ErrorCode.NOT_FOUND.exception()`).
2. **포착:** `GlobalExceptionHandler`가 해당 예외를 가로채 메서드와 매핑.
3. **가공:** `ErrorResponseFactory`를 호출하여 요청 정보와 결합된 응답 객체 구성.
4. **반환:** `ResponseEntity`에 HTTP 상태 코드와 메시지를 담아 JSON 형태로 클라이언트에게 응답.

---

## 3. 표준 응답 규격 (Example)

```json
{
  "success": false,
  "code": "PRODUCT_301",
  "message": "Product not found",
  "data": null,
  "details": {
    "productId": 1024,
    "path": "/api/products/1024",
    "method": "GET",
    "timestamp": "2026-05-07T15:03:00"
  }
}

Client ──▶ Controller ──▶ Service
                              │
                              │ 상품 없음 발견
                              ▼
                    throw BusinessException.of(
                        ErrorCode.PRODUCT_NOT_FOUND
                    ).detail("barcode", "123456")
                              │
                              ▼ (예외 전파)
                    ┌─────────────────────┐
                    │ GlobalExceptionHandler    │
                    │ handleBusinessException() │
                    └─────────────────────┘
                              │
                              ▼
              ErrorResponseFactory.createErrorResponse(e, request)
                              │
                              ▼
Client ◀── ResponseEntity.status(404).body(ApiResponse.error(...))






















                         예외 발생 (throw)
                              │
                              ▼
              ┌─────────────────────────────┐
              │  어떤 타입의 예외인가?         │
              └─────────────────────────────┘
                              │
         ┌────────────────────┼────────────────────┐
         │                    │                    │
         ▼                    ▼                    ▼
  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
  │ Business    │      │ Validation  │      │ Illegal     │
  │ Exception   │      │ Exception   │      │ Argument    │
  └──────┬──────┘      └──────┬──────┘      └──────┬──────┘
         │                    │                    │
         ▼                    ▼                    ▼
  ┌─────────────────────────────────────────────────────────────┐
  │              로깅 (log.warn / log.error)                     │
  │  - 에러 코드, 메시지, 경로, 상세 정보 기록                       │
  └─────────────────────────────────────────────────────────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              ▼
              ┌─────────────────────────────┐
              │  ErrorResponseFactory 호출   │
              │  - 적절한 ApiResponse 생성    │
              └─────────────────────────────┘
                              │
                              ▼
              ┌─────────────────────────────┐
              │  ResponseEntity 반환         │
              │  - HTTP 상태 코드 설정        │
              │  - ApiResponse body 설정     │
              └─────────────────────────────┘
                              │
                              ▼
                         클라이언트 응답