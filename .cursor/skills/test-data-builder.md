# Test Data Builder Pattern

## 개요
테스트 데이터를 쉽게 생성하기 위해 Builder 패턴을 사용합니다.

## 예시

```java
// 테스트용 Builder 클래스
public class TransactionTestBuilder {
    private Long id = 1L;
    private int amount = 10000;
    private TransactionType type = TransactionType.EXPENSE;
    private String description = "테스트 거래";
    private LocalDate transactionDate = LocalDate.now();
    private BudgetSession session;
    private Category category;
    
    public static TransactionTestBuilder aTransaction() {
        return new TransactionTestBuilder();
    }
    
    public TransactionTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public TransactionTestBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public TransactionTestBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }
    
    public TransactionTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public TransactionTestBuilder withDate(LocalDate date) {
        this.transactionDate = date;
        return this;
    }
    
    public TransactionTestBuilder withSession(BudgetSession session) {
        this.session = session;
        return this;
    }
    
    public TransactionTestBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }
    
    public TransactionTestBuilder asIncome() {
        this.type = TransactionType.INCOME;
        return this;
    }
    
    public TransactionTestBuilder asExpense() {
        this.type = TransactionType.EXPENSE;
        return this;
    }
    
    public Transaction build() {
        return Transaction.builder()
            .id(id)
            .amount(amount)
            .type(type)
            .description(description)
            .transactionDate(transactionDate)
            .session(session)
            .category(category)
            .build();
    }
}

// 사용 예시
class TransactionServiceTest {
    
    @Test
    void should_CalculateTotal_When_MultipleTransactions() {
        // Given
        BudgetSession session = BudgetSessionTestBuilder.aSession().build();
        
        List<Transaction> transactions = List.of(
            TransactionTestBuilder.aTransaction()
                .withAmount(10000)
                .asIncome()
                .withSession(session)
                .build(),
            TransactionTestBuilder.aTransaction()
                .withAmount(5000)
                .asExpense()
                .withSession(session)
                .build(),
            TransactionTestBuilder.aTransaction()
                .withAmount(3000)
                .asExpense()
                .withSession(session)
                .build()
        );
        
        when(transactionRepository.findBySessionId(session.getId()))
            .thenReturn(transactions);
        
        // When
        TransactionSummary summary = transactionService.getSummary(session.getId());
        
        // Then
        assertThat(summary.getTotalIncome()).isEqualTo(10000);
        assertThat(summary.getTotalExpense()).isEqualTo(8000);
    }
    
    @Test
    void should_FilterByDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        List<Transaction> transactions = List.of(
            TransactionTestBuilder.aTransaction()
                .withDate(LocalDate.of(2024, 1, 15))
                .build(),
            TransactionTestBuilder.aTransaction()
                .withDate(LocalDate.of(2024, 2, 1)) // 범위 밖
                .build()
        );
        
        // When & Then
        // ...
    }
}

// DTO Builder도 동일하게
public class TransactionDtoTestBuilder {
    private Long id = 1L;
    private int amount = 10000;
    private TransactionType type = TransactionType.EXPENSE;
    private String description = "테스트 거래";
    private Long sessionId = 1L;
    private Long categoryId = 1L;
    
    public static TransactionDtoTestBuilder aTransactionDto() {
        return new TransactionDtoTestBuilder();
    }
    
    // ... builder methods
    
    public TransactionDto build() {
        return TransactionDto.builder()
            .id(id)
            .amount(amount)
            .type(type)
            .description(description)
            .sessionId(sessionId)
            .categoryId(categoryId)
            .build();
    }
}
```

## 베스트 프랙티스
- 기본값을 가진 Builder 생성
- 메서드 체이닝으로 가독성 향상
- 자주 사용하는 조합은 헬퍼 메서드로 제공
- `aTransaction()`, `aSession()` 같은 네이밍 사용
- 테스트별로 필요한 필드만 설정

