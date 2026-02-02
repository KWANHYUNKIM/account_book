# MVC 패턴 구조 가이드

이 프로젝트는 **MVC (Model-View-Controller)** 패턴을 따릅니다.

## 📁 디렉토리 구조

```
backend/src/main/java/com/household/budget/
├── controller/     # Controller 계층 - HTTP 요청 처리
├── service/        # Service 계층 - 비즈니스 로직
├── repository/     # Repository 계층 - 데이터 접근
├── entity/         # Entity - 데이터베이스 엔티티
├── dto/            # DTO - 데이터 전송 객체
├── model/           # Model - 비즈니스 도메인 모델
└── view/            # View - 응답 객체 (API Response)
```

## 🏗️ 계층별 역할

### 1. **Controller 계층** (`controller/`)
- **역할**: HTTP 요청을 받아 Service 계층에 위임하고 View(Response)를 반환
- **책임**:
  - HTTP 요청/응답 처리
  - 요청 데이터 검증 (기본)
  - 예외 처리 및 HTTP 상태 코드 반환
  - Service 계층 호출
- **예시**: `TransactionController`, `AuthController`, `BudgetSessionController`

```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getAllTransactions() {
        // Service 계층 호출
        List<TransactionDto> transactions = transactionService.getAllTransactions();
        // View(Response) 반환
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}
```

### 2. **Service 계층** (`service/`)
- **역할**: 비즈니스 로직 처리 및 Model 변환
- **책임**:
  - 비즈니스 로직 구현
  - Entity와 DTO 간 변환
  - Repository 계층 호출
  - 트랜잭션 관리
- **예시**: `TransactionService`, `AuthService`, `BudgetSessionService`

```java
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    
    @Transactional
    public TransactionDto createTransaction(TransactionDto dto) {
        // Model로 변환하여 유효성 검증
        TransactionModel model = toModel(dto);
        if (!model.isValid()) {
            throw new IllegalArgumentException("거래 정보가 유효하지 않습니다.");
        }
        
        // 비즈니스 로직 처리
        Transaction entity = new Transaction();
        // ... 엔티티 설정
        
        // Repository 계층 호출
        return toDto(transactionRepository.save(entity));
    }
}
```

### 3. **Repository 계층** (`repository/`)
- **역할**: 데이터베이스 접근 및 CRUD 작업
- **책임**:
  - 데이터베이스 쿼리 실행
  - Entity 저장/조회/수정/삭제
- **예시**: `TransactionRepository`, `UserRepository`, `BudgetSessionRepository`

```java
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndType(Long userId, String type);
}
```

### 4. **Model 계층** (`model/`)
- **역할**: 비즈니스 도메인 모델 및 비즈니스 로직
- **책임**:
  - 도메인 모델 정의
  - 비즈니스 규칙 검증
  - 도메인 로직 처리
- **예시**: `TransactionModel`

```java
public class TransactionModel {
    private String type;
    private BigDecimal amount;
    
    // 비즈니스 로직
    public boolean isValid() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0
                && type != null && (type.equals("INCOME") || type.equals("EXPENSE"));
    }
    
    public boolean isIncome() {
        return "INCOME".equals(type);
    }
}
```

### 5. **View 계층** (`view/`)
- **역할**: API 응답 표준화 및 클라이언트에 반환할 데이터 구조 정의
- **책임**:
  - 응답 데이터 구조 정의
  - 성공/실패 응답 표준화
- **예시**: `ApiResponse`, `TransactionResponse`

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data, null);
    }
}
```

### 6. **Entity 계층** (`entity/`)
- **역할**: 데이터베이스 테이블과 매핑되는 엔티티
- **책임**:
  - 데이터베이스 스키마 정의
  - JPA 어노테이션으로 ORM 매핑
- **예시**: `Transaction`, `User`, `BudgetSession`

```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private BigDecimal amount;
}
```

### 7. **DTO 계층** (`dto/`)
- **역할**: 계층 간 데이터 전송 객체
- **책임**:
  - Controller와 Service 간 데이터 전달
  - 클라이언트와 서버 간 데이터 전달
- **예시**: `TransactionDto`, `BudgetSessionDto`

```java
@Data
public class TransactionDto {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String description;
}
```

## 🔄 데이터 흐름

```
Client Request
    ↓
Controller (요청 처리)
    ↓
Service (비즈니스 로직)
    ↓
Model (도메인 모델 검증)
    ↓
Repository (데이터 접근)
    ↓
Entity (데이터베이스)
    ↓
Repository (결과 반환)
    ↓
Service (DTO 변환)
    ↓
Controller (View 생성)
    ↓
View (ApiResponse)
    ↓
Client Response
```

## ✅ MVC 패턴 원칙

1. **관심사 분리 (Separation of Concerns)**
   - 각 계층은 명확한 책임을 가짐
   - Controller는 요청 처리만, Service는 비즈니스 로직만

2. **단일 책임 원칙 (Single Responsibility Principle)**
   - 각 클래스는 하나의 책임만 가짐

3. **의존성 역전 원칙 (Dependency Inversion Principle)**
   - 상위 계층이 하위 계층에 의존
   - 인터페이스를 통한 느슨한 결합

4. **계층 간 독립성**
   - 각 계층은 다른 계층의 구현 세부사항을 몰라도 됨

## 📝 예시: 거래 생성 플로우

1. **Client** → `POST /api/transactions` 요청
2. **Controller** → 요청 받아 `TransactionService.createTransaction()` 호출
3. **Service** → DTO를 Model로 변환하여 유효성 검증
4. **Service** → Entity 생성 및 Repository에 저장 요청
5. **Repository** → 데이터베이스에 저장
6. **Service** → Entity를 DTO로 변환하여 반환
7. **Controller** → DTO를 `ApiResponse`로 래핑하여 반환
8. **Client** → JSON 응답 수신

## 🎯 장점

- **유지보수성**: 각 계층이 독립적이어서 수정이 용이
- **테스트 용이성**: 각 계층을 독립적으로 테스트 가능
- **확장성**: 새로운 기능 추가가 쉬움
- **재사용성**: Service 로직을 여러 Controller에서 재사용 가능
- **표준화**: 일관된 코드 구조로 가독성 향상

