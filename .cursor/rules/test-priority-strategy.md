# 테스트 우선순위 전략

## 테스트가 꼭 필요한 코드 (우선순위)

모든 코드를 다 테스트할 필요는 없습니다. **"비즈니스 가치"**가 높은 곳에 집중하세요.

### 우선순위별 테스트 대상

| 우선순위 | 대상 코드 | 이유 | 커버리지 목표 |
|---------|---------|------|--------------|
| **1순위 (필수)** | 비즈니스 로직 (Domain Services) | 돈 계산, 권한 체크 등 복잡한 조건문이 들어가는 핵심 로직 | **100%** |
| **2순위** | 유효성 검사 (Validators) | 데이터가 들어올 때 형식이 맞는지 체크하는 로직 | **90%** |
| **3순위** | 유틸리티 함수 (Utils) | 날짜 변환, 금액 포맷팅 등 재사용되는 순수 함수 | **80%** |
| **4순위** | 통합 시나리오 (API E2E) | 로그인 → 계좌 생성 → 입금으로 이어지는 전체 흐름 | **70%** |

## 1순위: 비즈니스 로직 (Domain Services)

### 예시: TransactionCalculationService

```java
// domain/services/TransactionCalculationService.java
public class TransactionCalculationService {
    
    public TransactionSummary calculateSummary(List<Transaction> transactions) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        return TransactionSummary.builder()
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .balance(balance)
            .transactionCount(transactions.size())
            .build();
    }
    
    public boolean canCreateTransaction(BudgetSession session, BigDecimal amount) {
        // 비즈니스 규칙: 세션 잔액이 음수가 되면 안 됨
        BigDecimal currentBalance = session.getBalance();
        return currentBalance.add(amount).compareTo(BigDecimal.ZERO) >= 0;
    }
}
```

### 테스트 예시

```java
// __tests__/unit/domain/services/TransactionCalculationServiceTest.java
class TransactionCalculationServiceTest {
    
    private TransactionCalculationService service;
    
    @BeforeEach
    void setUp() {
        service = new TransactionCalculationService();
    }
    
    @Test
    void should_CalculateSummary_When_MixedTransactions() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(10000, TransactionType.INCOME),
            createTransaction(5000, TransactionType.EXPENSE),
            createTransaction(3000, TransactionType.EXPENSE)
        );
        
        // When
        TransactionSummary summary = service.calculateSummary(transactions);
        
        // Then
        assertThat(summary.getTotalIncome()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(new BigDecimal("8000"));
        assertThat(summary.getBalance()).isEqualByComparingTo(new BigDecimal("2000"));
        assertThat(summary.getTransactionCount()).isEqualTo(3);
    }
    
    @Test
    void should_ReturnTrue_When_BalanceIsSufficient() {
        // Given
        BudgetSession session = BudgetSession.builder()
            .balance(new BigDecimal("10000"))
            .build();
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        boolean canCreate = service.canCreateTransaction(session, amount);
        
        // Then
        assertThat(canCreate).isTrue();
    }
    
    @Test
    void should_ReturnFalse_When_BalanceIsInsufficient() {
        // Given
        BudgetSession session = BudgetSession.builder()
            .balance(new BigDecimal("1000"))
            .build();
        BigDecimal amount = new BigDecimal("5000"); // 잔액 부족
        
        // When
        boolean canCreate = service.canCreateTransaction(session, amount);
        
        // Then
        assertThat(canCreate).isFalse();
    }
}
```

## 2순위: 유효성 검사 (Validators)

### 예시: TransactionValidator

```java
// interfaces/http/validation/TransactionValidator.java
@Component
public class TransactionValidator {
    
    public void validateCreateRequest(CreateTransactionRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("금액은 0보다 커야 합니다.");
        }
        
        if (request.getType() == null) {
            throw new ValidationException("거래 유형은 필수입니다.");
        }
        
        if (request.getSessionId() == null) {
            throw new ValidationException("세션 ID는 필수입니다.");
        }
    }
}
```

### 테스트 예시

```java
// __tests__/unit/interfaces/http/validation/TransactionValidatorTest.java
class TransactionValidatorTest {
    
    private TransactionValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new TransactionValidator();
    }
    
    @Test
    void should_Pass_When_ValidRequest() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    @Test
    void should_ThrowException_When_AmountIsZero() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(BigDecimal.ZERO)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("금액은 0보다 커야 합니다");
    }
    
    @Test
    void should_ThrowException_When_AmountIsNegative() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("-1000"))
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class);
    }
}
```

## 3순위: 유틸리티 함수 (Utils)

### 예시: CurrencyFormatter

```java
// domain/utils/CurrencyFormatter.java
public class CurrencyFormatter {
    
    public static String format(BigDecimal amount) {
        return new DecimalFormat("#,###원").format(amount);
    }
    
    public static BigDecimal parse(String formattedAmount) {
        String numeric = formattedAmount.replaceAll("[^0-9]", "");
        return new BigDecimal(numeric);
    }
}
```

### 테스트 예시

```java
// __tests__/unit/domain/utils/CurrencyFormatterTest.java
class CurrencyFormatterTest {
    
    @Test
    void should_Format_When_ValidAmount() {
        // Given
        BigDecimal amount = new BigDecimal("10000");
        
        // When
        String formatted = CurrencyFormatter.format(amount);
        
        // Then
        assertThat(formatted).isEqualTo("10,000원");
    }
    
    @Test
    void should_Parse_When_ValidFormat() {
        // Given
        String formatted = "10,000원";
        
        // When
        BigDecimal amount = CurrencyFormatter.parse(formatted);
        
        // Then
        assertThat(amount).isEqualByComparingTo(new BigDecimal("10000"));
    }
}
```

## 4순위: 통합 시나리오 (API E2E)

### 예시: 전체 플로우 테스트

```java
// __tests__/integration/TransactionFlowTest.java
@SpringBootTest
@AutoConfigureMockMvc
class TransactionFlowTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BudgetSessionRepository sessionRepository;
    
    private String authToken;
    private Long sessionId;
    
    @BeforeEach
    void setUp() throws Exception {
        // 1. 사용자 생성 및 로그인
        User user = createUser("test@example.com", "password123");
        authToken = login("test@example.com", "password123");
        
        // 2. 세션 생성
        sessionId = createSession(authToken, "테스트 세션");
    }
    
    @Test
    void should_CreateTransactionFlow() throws Exception {
        // Given: 세션이 있고 인증된 상태
        
        // When: 거래 생성
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .description("점심 식사")
            .sessionId(sessionId)
            .build();
        
        // Then: 거래가 생성되고 잔액이 업데이트됨
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.amount").value(10000));
        
        // 세션 잔액 확인
        mockMvc.perform(get("/api/sessions/" + sessionId)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.balance").value(-10000));
    }
}
```

## 테스트 커버리지 목표

```java
// 전체 프로젝트
- Domain Services: 100%
- Validators: 90%
- Utils: 80%
- Controllers: 70%
- Repositories: 60% (쿼리 위주)
- Infrastructure: 50% (외부 의존성)
```

## 베스트 프랙티스
- 비즈니스 로직은 반드시 100% 커버리지
- 복잡한 조건문은 모든 분기 테스트
- 경계값 테스트 (0, 음수, 최대값 등)
- 통합 테스트는 핵심 시나리오만
- Infrastructure는 Mock으로 대체 가능하므로 낮은 커버리지 허용

