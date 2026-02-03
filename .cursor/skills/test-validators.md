# Test Validators (2순위)

## 개요
유효성 검사 로직은 **90% 커버리지**를 목표로 합니다. 모든 검증 규칙을 테스트합니다.

## 예시: TransactionValidator

```java
// interfaces/http/validation/TransactionValidator.java
@Component
public class TransactionValidator {
    
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999");
    
    public void validateCreateRequest(CreateTransactionRequest request) {
        if (request == null) {
            throw new ValidationException("요청 데이터가 없습니다.");
        }
        
        validateAmount(request.getAmount());
        validateType(request.getType());
        validateSessionId(request.getSessionId());
        validateDescription(request.getDescription());
    }
    
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new ValidationException("금액은 필수입니다.");
        }
        
        if (amount.compareTo(MIN_AMOUNT) < 0) {
            throw new ValidationException("금액은 1원 이상이어야 합니다.");
        }
        
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new ValidationException("금액은 999,999,999원을 초과할 수 없습니다.");
        }
    }
    
    private void validateType(TransactionType type) {
        if (type == null) {
            throw new ValidationException("거래 유형은 필수입니다.");
        }
    }
    
    private void validateSessionId(Long sessionId) {
        if (sessionId == null) {
            throw new ValidationException("세션 ID는 필수입니다.");
        }
        
        if (sessionId <= 0) {
            throw new ValidationException("세션 ID는 양수여야 합니다.");
        }
    }
    
    private void validateDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new ValidationException("설명은 200자 이하여야 합니다.");
        }
    }
}
```

## 테스트: 모든 검증 규칙

```java
// __tests__/unit/interfaces/http/validation/TransactionValidatorTest.java
@ExtendWith(MockitoExtension.class)
class TransactionValidatorTest {
    
    private TransactionValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new TransactionValidator();
    }
    
    // validateCreateRequest 테스트
    
    @Test
    void should_Pass_When_ValidRequest() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .description("점심 식사")
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    @Test
    void should_ThrowException_When_RequestIsNull() {
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(null))
            .isInstanceOf(ValidationException.class)
            .hasMessage("요청 데이터가 없습니다.");
    }
    
    // Amount 검증 테스트
    
    @Test
    void should_ThrowException_When_AmountIsNull() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(null)
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("금액은 필수입니다");
    }
    
    @Test
    void should_ThrowException_When_AmountIsZero() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(BigDecimal.ZERO)
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("금액은 1원 이상이어야 합니다");
    }
    
    @Test
    void should_ThrowException_When_AmountIsNegative() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("-1000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class);
    }
    
    @Test
    void should_Pass_When_AmountIsMinimum() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("1")) // 최소값
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    @Test
    void should_ThrowException_When_AmountExceedsMaximum() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("1000000000")) // 최대값 초과
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("999,999,999원을 초과할 수 없습니다");
    }
    
    @Test
    void should_Pass_When_AmountIsMaximum() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("999999999")) // 최대값
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    // Type 검증 테스트
    
    @Test
    void should_ThrowException_When_TypeIsNull() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(null)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("거래 유형은 필수입니다");
    }
    
    @Test
    void should_Pass_When_TypeIsIncome() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.INCOME)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    @Test
    void should_Pass_When_TypeIsExpense() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    // SessionId 검증 테스트
    
    @Test
    void should_ThrowException_When_SessionIdIsNull() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(null)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("세션 ID는 필수입니다");
    }
    
    @Test
    void should_ThrowException_When_SessionIdIsZero() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(0L)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("세션 ID는 양수여야 합니다");
    }
    
    @Test
    void should_ThrowException_When_SessionIdIsNegative() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(-1L)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class);
    }
    
    @Test
    void should_Pass_When_SessionIdIsPositive() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    // Description 검증 테스트
    
    @Test
    void should_Pass_When_DescriptionIsNull() {
        // Given
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .description(null)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    @Test
    void should_Pass_When_DescriptionIsWithinLimit() {
        // Given
        String description = "a".repeat(200); // 정확히 200자
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .description(description)
            .build();
        
        // When & Then
        assertThatNoException().isThrownBy(() -> validator.validateCreateRequest(request));
    }
    
    @Test
    void should_ThrowException_When_DescriptionExceedsLimit() {
        // Given
        String description = "a".repeat(201); // 201자
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .sessionId(1L)
            .description(description)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> validator.validateCreateRequest(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("설명은 200자 이하여야 합니다");
    }
}
```

## 베스트 프랙티스
- **모든 검증 규칙 테스트**: 각 필드의 모든 검증 로직
- **경계값 테스트**: 최소값, 최대값, 경계값
- **Null 안전성**: null 체크 로직
- **에러 메시지 검증**: 정확한 메시지 확인
- **조합 테스트**: 여러 필드가 동시에 잘못된 경우

