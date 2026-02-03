# Repository Test - JPA Layer

## 개요
Repository 계층을 테스트합니다. 실제 데이터베이스(H2)를 사용하여 JPA 쿼리를 검증합니다.

## 예시

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BudgetSessionRepository sessionRepository;
    
    private BudgetSession session;
    
    @BeforeEach
    void setUp() {
        session = BudgetSession.builder()
            .name("테스트 세션")
            .description("테스트용 세션")
            .build();
        session = entityManager.persistAndFlush(session);
    }
    
    @Test
    void should_FindTransactions_When_BySessionId() {
        // Given
        Transaction transaction1 = Transaction.builder()
            .amount(10000)
            .type(TransactionType.EXPENSE)
            .description("점심 식사")
            .session(session)
            .build();
            
        Transaction transaction2 = Transaction.builder()
            .amount(5000)
            .type(TransactionType.INCOME)
            .description("용돈")
            .session(session)
            .build();
            
        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);
        
        // When
        List<Transaction> transactions = transactionRepository.findBySessionId(session.getId());
        
        // Then
        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getAmount)
            .containsExactlyInAnyOrder(10000, 5000);
    }
    
    @Test
    void should_FindTransactions_When_ByDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        Transaction transaction1 = Transaction.builder()
            .amount(10000)
            .transactionDate(LocalDate.of(2024, 1, 15))
            .session(session)
            .build();
            
        Transaction transaction2 = Transaction.builder()
            .amount(5000)
            .transactionDate(LocalDate.of(2024, 2, 1)) // 범위 밖
            .session(session)
            .build();
            
        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);
        
        // When
        List<Transaction> transactions = transactionRepository
            .findBySessionIdAndTransactionDateBetween(
                session.getId(), startDate, endDate
            );
        
        // Then
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAmount()).isEqualTo(10000);
    }
    
    @Test
    void should_CalculateSum_When_ByType() {
        // Given
        entityManager.persistAndFlush(Transaction.builder()
            .amount(10000)
            .type(TransactionType.INCOME)
            .session(session)
            .build());
            
        entityManager.persistAndFlush(Transaction.builder()
            .amount(5000)
            .type(TransactionType.INCOME)
            .session(session)
            .build());
            
        entityManager.persistAndFlush(Transaction.builder()
            .amount(3000)
            .type(TransactionType.EXPENSE)
            .session(session)
            .build());
        
        // When
        Long totalIncome = transactionRepository
            .sumAmountBySessionIdAndType(session.getId(), TransactionType.INCOME);
        Long totalExpense = transactionRepository
            .sumAmountBySessionIdAndType(session.getId(), TransactionType.EXPENSE);
        
        // Then
        assertThat(totalIncome).isEqualTo(15000);
        assertThat(totalExpense).isEqualTo(3000);
    }
    
    @Test
    void should_DeleteTransactions_When_BySessionId() {
        // Given
        Transaction transaction = Transaction.builder()
            .amount(10000)
            .session(session)
            .build();
        entityManager.persistAndFlush(transaction);
        
        // When
        transactionRepository.deleteBySessionId(session.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        List<Transaction> transactions = transactionRepository.findBySessionId(session.getId());
        assertThat(transactions).isEmpty();
    }
}
```

## 베스트 프랙티스
- `@DataJpaTest`로 JPA 레이어만 로드
- `TestEntityManager`로 테스트 데이터 관리
- `@ActiveProfiles("test")`로 테스트 프로파일 사용
- 실제 쿼리 동작 검증
- 트랜잭션은 자동 롤백 (기본 동작)

