# Clean Architecture - Backend 구조

## 디렉토리 구조

```
backend/src/main/java/com/household/budget/
├── domain/              # 1. 비즈니스 규칙 (가장 순수한 로직)
│   ├── entities/        # 도메인 엔티티 (User, Transaction, BudgetSession 등)
│   ├── services/        # 핵심 비즈니스 로직 (입출금 계산, 회원가입 승인 등)
│   ├── repositories/   # 도메인 레포지토리 인터페이스 (구현체는 infrastructure에)
│   └── exceptions/      # 도메인 예외
├── infrastructure/      # 2. 외부 환경 (테스트 시 Mocking 대상)
│   ├── database/        # JPA 구현체, Repository 구현
│   │   ├── jpa/         # JPA Entity, Repository
│   │   └── config/      # JPA 설정
│   └── external-api/    # 외부 API 연동 (은행 API, 결제 등)
│       └── client/      # HTTP Client, Feign Client 등
├── interfaces/          # 3. 입구 (컨트롤러)
│   └── http/            # REST Controller, DTO, Request/Response
│       ├── controller/  # Controller 클래스
│       ├── dto/         # Request/Response DTO
│       └── validation/  # 유효성 검사
└── application/         # 4. 애플리케이션 서비스 (Use Case)
    └── services/        # 도메인 서비스를 조합하는 애플리케이션 서비스
```

## 계층별 역할

### 1. Domain (도메인 계층)
- **역할**: 비즈니스 규칙과 핵심 로직
- **특징**: 
  - 외부 의존성 없음 (순수 Java)
  - 테스트하기 쉬움
  - 비즈니스 가치가 가장 높음
- **예시**:
  ```java
  // domain/entities/Transaction.java
  public class Transaction {
      private Long id;
      private BigDecimal amount;
      private TransactionType type;
      
      // 비즈니스 로직
      public boolean isIncome() {
          return type == TransactionType.INCOME;
      }
      
      public BigDecimal calculateBalance(BigDecimal currentBalance) {
          return isIncome() 
              ? currentBalance.add(amount)
              : currentBalance.subtract(amount);
      }
  }
  
  // domain/services/TransactionCalculationService.java
  public class TransactionCalculationService {
      public TransactionSummary calculateSummary(List<Transaction> transactions) {
          // 핵심 계산 로직
      }
  }
  ```

### 2. Infrastructure (인프라 계층)
- **역할**: 외부 시스템과의 통신
- **특징**:
  - 데이터베이스, 외부 API 등 구현
  - Domain의 인터페이스를 구현
  - 테스트 시 Mocking 대상
- **예시**:
  ```java
  // infrastructure/database/jpa/TransactionJpaRepository.java
  @Repository
  public class TransactionJpaRepository implements TransactionRepository {
      // JPA 구현
  }
  
  // infrastructure/external-api/client/BankApiClient.java
  public class BankApiClient {
      // 외부 은행 API 호출
  }
  ```

### 3. Interfaces (인터페이스 계층)
- **역할**: 외부와의 통신 (HTTP, CLI 등)
- **특징**:
  - Controller, DTO
  - Domain을 호출
  - 유효성 검사
- **예시**:
  ```java
  // interfaces/http/controller/TransactionController.java
  @RestController
  public class TransactionController {
      private final TransactionApplicationService applicationService;
      
      @PostMapping("/api/transactions")
      public ResponseEntity<TransactionResponse> create(@RequestBody TransactionRequest request) {
          // DTO -> Domain 변환
          // Application Service 호출
      }
  }
  ```

### 4. Application (애플리케이션 계층)
- **역할**: Use Case 구현
- **특징**:
  - Domain 서비스를 조합
  - 트랜잭션 관리
  - 도메인 간 조율
- **예시**:
  ```java
  // application/services/TransactionApplicationService.java
  @Service
  public class TransactionApplicationService {
      private final TransactionRepository transactionRepository;
      private final TransactionCalculationService calculationService;
      
      @Transactional
      public TransactionDto createTransaction(CreateTransactionCommand command) {
          // 도메인 서비스 조합
          // 트랜잭션 관리
      }
  }
  ```

## 의존성 방향

```
interfaces → application → domain
                ↓
         infrastructure → domain
```

- **Domain**: 의존성 없음 (가장 안쪽)
- **Infrastructure**: Domain에 의존
- **Application**: Domain에 의존
- **Interfaces**: Application, Domain에 의존

## 베스트 프랙티스
- Domain은 순수 Java로 작성 (Spring 의존성 최소화)
- Repository는 인터페이스를 Domain에, 구현체를 Infrastructure에
- DTO는 Interfaces에만 존재
- 비즈니스 로직은 Domain에 집중
- Infrastructure는 테스트 시 쉽게 Mocking 가능하도록 설계

