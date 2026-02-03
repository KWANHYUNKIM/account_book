# Unit Test - Service Layer

## 개요
Service 계층의 비즈니스 로직을 단위 테스트합니다. Repository는 Mock으로 처리합니다.

## 예시

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private BudgetSessionRepository sessionRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private TransactionService transactionService;
    
    private Transaction transaction;
    private BudgetSession session;
    private Category category;
    
    @BeforeEach
    void setUp() {
        session = BudgetSession.builder()
            .id(1L)
            .name("테스트 세션")
            .build();
            
        category = Category.builder()
            .id(1L)
            .name("식비")
            .build();
            
        transaction = Transaction.builder()
            .id(1L)
            .amount(10000)
            .type(TransactionType.EXPENSE)
            .description("점심 식사")
            .session(session)
            .category(category)
            .build();
    }
    
    @Test
    void should_CreateTransaction_When_ValidRequest() {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(10000)
            .type(TransactionType.EXPENSE)
            .description("점심 식사")
            .sessionId(1L)
            .categoryId(1L)
            .build();
            
        when(sessionRepository.findById(1L))
            .thenReturn(Optional.of(session));
        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(transaction);
        
        // When
        TransactionDto result = transactionService.createTransaction(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(10000);
        assertThat(result.getDescription()).isEqualTo("점심 식사");
        verify(transactionRepository).save(any(Transaction.class));
    }
    
    @Test
    void should_ThrowException_When_SessionNotFound() {
        // Given
        TransactionDto request = TransactionDto.builder()
            .sessionId(999L)
            .build();
            
        when(sessionRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Session not found");
    }
    
    @Test
    void should_CalculateTotal_When_GetTransactionsBySession() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(10000, TransactionType.INCOME),
            createTransaction(5000, TransactionType.EXPENSE),
            createTransaction(3000, TransactionType.EXPENSE)
        );
        
        when(transactionRepository.findBySessionId(1L))
            .thenReturn(transactions);
        
        // When
        TransactionSummary summary = transactionService.getSummary(1L);
        
        // Then
        assertThat(summary.getTotalIncome()).isEqualTo(10000);
        assertThat(summary.getTotalExpense()).isEqualTo(8000);
        assertThat(summary.getBalance()).isEqualTo(2000);
    }
    
    private Transaction createTransaction(int amount, TransactionType type) {
        return Transaction.builder()
            .amount(amount)
            .type(type)
            .session(session)
            .build();
    }
}
```

## 베스트 프랙티스
- `@ExtendWith(MockitoExtension.class)` 사용
- `@Mock`으로 의존성 모킹
- `@InjectMocks`로 테스트 대상 주입
- Given-When-Then 패턴 사용
- `verify()`로 메서드 호출 검증
- 예외 케이스도 테스트

