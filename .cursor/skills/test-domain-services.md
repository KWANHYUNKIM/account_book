# Test Domain Services (1순위)

## 개요
비즈니스 로직이 있는 Domain Services는 **100% 커버리지**를 목표로 합니다.

## 예시: TransactionCalculationService

```java
// domain/services/TransactionCalculationService.java
public class TransactionCalculationService {
    
    public TransactionSummary calculateSummary(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return TransactionSummary.empty();
        }
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.isExpense()) {
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
    
    public boolean canCreateTransaction(BudgetSession session, BigDecimal amount, TransactionType type) {
        if (session == null || amount == null || type == null) {
            return false;
        }
        
        BigDecimal currentBalance = session.getBalance();
        
        if (type == TransactionType.INCOME) {
            return true; // 수입은 항상 가능
        } else {
            // 지출은 잔액이 충분해야 함
            return currentBalance.compareTo(amount) >= 0;
        }
    }
    
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, TransactionType type) {
        if (currentBalance == null || amount == null || type == null) {
            throw new IllegalArgumentException("필수 파라미터가 null입니다.");
        }
        
        return type == TransactionType.INCOME
            ? currentBalance.add(amount)
            : currentBalance.subtract(amount);
    }
}
```

## 테스트: 모든 분기 커버

```java
// __tests__/unit/domain/services/TransactionCalculationServiceTest.java
@ExtendWith(MockitoExtension.class)
class TransactionCalculationServiceTest {
    
    private TransactionCalculationService service;
    
    @BeforeEach
    void setUp() {
        service = new TransactionCalculationService();
    }
    
    // calculateSummary 테스트
    
    @Test
    void should_ReturnEmptySummary_When_TransactionsIsNull() {
        // When
        TransactionSummary summary = service.calculateSummary(null);
        
        // Then
        assertThat(summary.getTotalIncome()).isZero();
        assertThat(summary.getTotalExpense()).isZero();
        assertThat(summary.getTransactionCount()).isZero();
    }
    
    @Test
    void should_ReturnEmptySummary_When_TransactionsIsEmpty() {
        // When
        TransactionSummary summary = service.calculateSummary(Collections.emptyList());
        
        // Then
        assertThat(summary.getTotalIncome()).isZero();
        assertThat(summary.getTotalExpense()).isZero();
    }
    
    @Test
    void should_CalculateSummary_When_OnlyIncome() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(10000, TransactionType.INCOME),
            createTransaction(5000, TransactionType.INCOME)
        );
        
        // When
        TransactionSummary summary = service.calculateSummary(transactions);
        
        // Then
        assertThat(summary.getTotalIncome()).isEqualByComparingTo(new BigDecimal("15000"));
        assertThat(summary.getTotalExpense()).isZero();
        assertThat(summary.getBalance()).isEqualByComparingTo(new BigDecimal("15000"));
    }
    
    @Test
    void should_CalculateSummary_When_OnlyExpense() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(5000, TransactionType.EXPENSE),
            createTransaction(3000, TransactionType.EXPENSE)
        );
        
        // When
        TransactionSummary summary = service.calculateSummary(transactions);
        
        // Then
        assertThat(summary.getTotalIncome()).isZero();
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(new BigDecimal("8000"));
        assertThat(summary.getBalance()).isEqualByComparingTo(new BigDecimal("-8000"));
    }
    
    @Test
    void should_CalculateSummary_When_MixedTransactions() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(10000, TransactionType.INCOME),
            createTransaction(5000, TransactionType.EXPENSE),
            createTransaction(3000, TransactionType.EXPENSE),
            createTransaction(2000, TransactionType.INCOME)
        );
        
        // When
        TransactionSummary summary = service.calculateSummary(transactions);
        
        // Then
        assertThat(summary.getTotalIncome()).isEqualByComparingTo(new BigDecimal("12000"));
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(new BigDecimal("8000"));
        assertThat(summary.getBalance()).isEqualByComparingTo(new BigDecimal("4000"));
        assertThat(summary.getTransactionCount()).isEqualTo(4);
    }
    
    // canCreateTransaction 테스트
    
    @Test
    void should_ReturnFalse_When_SessionIsNull() {
        // When
        boolean result = service.canCreateTransaction(null, new BigDecimal("1000"), TransactionType.EXPENSE);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void should_ReturnTrue_When_IncomeType() {
        // Given
        BudgetSession session = BudgetSession.builder()
            .balance(new BigDecimal("1000"))
            .build();
        
        // When
        boolean result = service.canCreateTransaction(session, new BigDecimal("10000"), TransactionType.INCOME);
        
        // Then
        assertThat(result).isTrue(); // 수입은 항상 가능
    }
    
    @Test
    void should_ReturnTrue_When_BalanceIsSufficient() {
        // Given
        BudgetSession session = BudgetSession.builder()
            .balance(new BigDecimal("10000"))
            .build();
        
        // When
        boolean result = service.canCreateTransaction(session, new BigDecimal("5000"), TransactionType.EXPENSE);
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    void should_ReturnTrue_When_BalanceEqualsAmount() {
        // Given
        BudgetSession session = BudgetSession.builder()
            .balance(new BigDecimal("5000"))
            .build();
        
        // When
        boolean result = service.canCreateTransaction(session, new BigDecimal("5000"), TransactionType.EXPENSE);
        
        // Then
        assertThat(result).isTrue(); // 경계값: 잔액과 동일한 금액
    }
    
    @Test
    void should_ReturnFalse_When_BalanceIsInsufficient() {
        // Given
        BudgetSession session = BudgetSession.builder()
            .balance(new BigDecimal("1000"))
            .build();
        
        // When
        boolean result = service.canCreateTransaction(session, new BigDecimal("5000"), TransactionType.EXPENSE);
        
        // Then
        assertThat(result).isFalse();
    }
    
    // calculateNewBalance 테스트
    
    @Test
    void should_AddAmount_When_Income() {
        // Given
        BigDecimal currentBalance = new BigDecimal("10000");
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        BigDecimal newBalance = service.calculateNewBalance(currentBalance, amount, TransactionType.INCOME);
        
        // Then
        assertThat(newBalance).isEqualByComparingTo(new BigDecimal("15000"));
    }
    
    @Test
    void should_SubtractAmount_When_Expense() {
        // Given
        BigDecimal currentBalance = new BigDecimal("10000");
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        BigDecimal newBalance = service.calculateNewBalance(currentBalance, amount, TransactionType.EXPENSE);
        
        // Then
        assertThat(newBalance).isEqualByComparingTo(new BigDecimal("5000"));
    }
    
    @Test
    void should_ThrowException_When_CurrentBalanceIsNull() {
        // When & Then
        assertThatThrownBy(() -> service.calculateNewBalance(null, new BigDecimal("1000"), TransactionType.INCOME))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("필수 파라미터가 null입니다");
    }
    
    @Test
    void should_HandleZeroAmount() {
        // Given
        BigDecimal currentBalance = new BigDecimal("10000");
        BigDecimal amount = BigDecimal.ZERO;
        
        // When
        BigDecimal newBalance = service.calculateNewBalance(currentBalance, amount, TransactionType.INCOME);
        
        // Then
        assertThat(newBalance).isEqualByComparingTo(new BigDecimal("10000"));
    }
    
    // Helper
    private Transaction createTransaction(int amount, TransactionType type) {
        return Transaction.builder()
            .amount(new BigDecimal(amount))
            .type(type)
            .build();
    }
}
```

## 베스트 프랙티스
- **모든 분기 테스트**: if-else, switch-case 모든 케이스
- **경계값 테스트**: 0, 음수, 최대값 등
- **Null 안전성**: null 체크 로직 테스트
- **예외 케이스**: 잘못된 입력에 대한 예외 처리
- **엣지 케이스**: 빈 리스트, 단일 항목 등
- **100% 커버리지 목표**: 핵심 비즈니스 로직은 반드시

