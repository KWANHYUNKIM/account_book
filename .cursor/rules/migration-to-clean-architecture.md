# MVC에서 Clean Architecture로 마이그레이션 가이드

## 현재 구조 (MVC)

```
backend/src/main/java/com/household/budget/
├── controller/     # Controller 계층
├── service/        # Service 계층
├── repository/     # Repository 계층
├── entity/         # Entity
├── dto/            # DTO
├── model/          # Model
└── view/           # View
```

## 목표 구조 (Clean Architecture)

```
backend/src/main/java/com/household/budget/
├── domain/              # 비즈니스 규칙
│   ├── entities/       # 도메인 엔티티
│   ├── services/       # 핵심 비즈니스 로직
│   ├── repositories/   # 도메인 레포지토리 인터페이스
│   └── exceptions/     # 도메인 예외
├── infrastructure/     # 외부 환경
│   ├── database/       # JPA 구현
│   └── external-api/   # 외부 API
├── interfaces/         # 입구
│   └── http/          # Controller, DTO
└── application/        # 애플리케이션 서비스
    └── services/       # Use Case
```

## 마이그레이션 단계

### 1단계: Domain 계층 생성

```bash
# 디렉토리 생성
mkdir -p src/main/java/com/household/budget/domain/{entities,services,repositories,exceptions}
```

**이동할 파일:**
- `entity/` → `domain/entities/`
- `model/` → `domain/entities/` (도메인 모델로 변환)

**생성할 파일:**
- `domain/repositories/TransactionRepository.java` (인터페이스)
- `domain/services/TransactionCalculationService.java` (비즈니스 로직)

### 2단계: Infrastructure 계층 생성

```bash
mkdir -p src/main/java/com/household/budget/infrastructure/{database,external-api}
```

**이동할 파일:**
- `repository/` → `infrastructure/database/jpa/`
- JPA Entity는 `infrastructure/database/jpa/entity/`에 별도 생성

**변경 사항:**
- Repository 인터페이스는 Domain에, 구현체는 Infrastructure에

### 3단계: Interfaces 계층 생성

```bash
mkdir -p src/main/java/com/household/budget/interfaces/http/{controller,dto,validation}
```

**이동할 파일:**
- `controller/` → `interfaces/http/controller/`
- `dto/` → `interfaces/http/dto/`
- `view/` → `interfaces/http/dto/` (Response DTO)

**생성할 파일:**
- `interfaces/http/validation/TransactionValidator.java`

### 4단계: Application 계층 생성

```bash
mkdir -p src/main/java/com/household/budget/application/services
```

**변경 사항:**
- 기존 `service/`를 `application/services/`로 이동
- Domain Services를 호출하도록 리팩토링

## 코드 변환 예시

### Before (MVC)

```java
// service/TransactionService.java
@Service
public class TransactionService {
    private final TransactionRepository repository;
    
    public TransactionDto createTransaction(TransactionDto dto) {
        Transaction entity = new Transaction();
        entity.setAmount(dto.getAmount());
        // ... 비즈니스 로직과 데이터 접근이 섞여 있음
        return toDto(repository.save(entity));
    }
}
```

### After (Clean Architecture)

```java
// domain/services/TransactionCalculationService.java
public class TransactionCalculationService {
    public BigDecimal calculateNewBalance(BigDecimal current, BigDecimal amount, TransactionType type) {
        // 순수 비즈니스 로직
        return type == TransactionType.INCOME 
            ? current.add(amount) 
            : current.subtract(amount);
    }
}

// application/services/TransactionApplicationService.java
@Service
public class TransactionApplicationService {
    private final TransactionRepository repository; // Domain 인터페이스
    private final TransactionCalculationService calculationService;
    
    @Transactional
    public TransactionDto createTransaction(CreateTransactionCommand command) {
        // Domain Service 호출
        BigDecimal newBalance = calculationService.calculateNewBalance(
            command.getSession().getBalance(),
            command.getAmount(),
            command.getType()
        );
        // ...
    }
}

// infrastructure/database/jpa/TransactionJpaRepository.java
@Repository
public class TransactionJpaRepository implements TransactionRepository {
    // JPA 구현
}
```

## 테스트 구조

```
backend/src/test/java/com/household/budget/
├── __tests__/
│   ├── unit/
│   │   ├── domain/services/     # 1순위: Domain Services
│   │   ├── interfaces/http/validation/  # 2순위: Validators
│   │   └── domain/utils/        # 3순위: Utils
│   └── integration/
│       └── TransactionFlowTest.java  # 4순위: E2E
```

## 마이그레이션 체크리스트

- [ ] Domain 계층 생성 (entities, services, repositories)
- [ ] Infrastructure 계층 생성 (database, external-api)
- [ ] Interfaces 계층 생성 (http/controller, dto, validation)
- [ ] Application 계층 생성 (services)
- [ ] Repository 인터페이스를 Domain으로 이동
- [ ] Repository 구현체를 Infrastructure로 이동
- [ ] 비즈니스 로직을 Domain Services로 추출
- [ ] Controller를 Interfaces로 이동
- [ ] DTO를 Interfaces로 이동
- [ ] 테스트 구조 재구성
- [ ] 의존성 방향 확인 (Domain은 의존성 없음)

## 베스트 프랙티스
- 점진적 마이그레이션: 한 번에 모든 것을 바꾸지 말고 단계적으로
- 테스트 먼저: 리팩토링 전에 테스트 작성
- Domain 우선: Domain 계층부터 시작
- 의존성 방향 준수: Domain은 외부 의존성 없음

