# Test SQL Scripts

## 개요
`@Sql` 어노테이션으로 테스트 데이터를 준비하고 정리합니다.

## 예시

```java
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TransactionIntegrationTest {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BudgetSessionRepository sessionRepository;
    
    @Test
    void should_FindTransactions_When_UsingTestData() {
        // Given: setup.sql에서 데이터 준비됨
        
        // When
        List<Transaction> transactions = transactionRepository.findBySessionId(1L);
        
        // Then
        assertThat(transactions).hasSize(3);
    }
}

// setup.sql
-- src/test/resources/test-data/setup.sql
INSERT INTO users (id, email, name, password, created_at) VALUES
(1, 'test@example.com', 'Test User', '$2a$10$...', CURRENT_TIMESTAMP);

INSERT INTO budget_sessions (id, user_id, name, description, created_at) VALUES
(1, 1, '테스트 세션', '테스트용 세션', CURRENT_TIMESTAMP);

INSERT INTO categories (id, name, type, user_id) VALUES
(1, '식비', 'EXPENSE', 1),
(2, '교통비', 'EXPENSE', 1),
(3, '용돈', 'INCOME', 1);

INSERT INTO transactions (id, session_id, category_id, amount, type, description, transaction_date) VALUES
(1, 1, 1, 10000, 'EXPENSE', '점심 식사', '2024-01-15'),
(2, 1, 2, 5000, 'EXPENSE', '지하철', '2024-01-16'),
(3, 1, 3, 50000, 'INCOME', '용돈', '2024-01-17');

// cleanup.sql
-- src/test/resources/test-data/cleanup.sql
DELETE FROM transactions;
DELETE FROM categories;
DELETE FROM budget_sessions;
DELETE FROM users;

// 클래스 레벨에서 설정
@Sql(scripts = "/test-data/setup.sql")
class TransactionServiceIntegrationTest {
    
    @Test
    @Sql(scripts = "/test-data/additional-data.sql") // 메서드 레벨 오버라이드
    void should_UseAdditionalData() {
        // ...
    }
    
    @Test
    @Sql(scripts = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void should_CleanupAfterTest() {
        // ...
    }
}

// 조건부 SQL 실행
@Sql(scripts = "/test-data/setup.sql", 
     config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
class IsolatedTransactionTest {
    // 각 테스트가 독립적인 트랜잭션에서 실행
}

// 여러 스크립트 실행
@Sql(scripts = {
    "/test-data/users.sql",
    "/test-data/sessions.sql",
    "/test-data/transactions.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MultipleScriptsTest {
    // ...
}
```

## 베스트 프랙티스
- 테스트 데이터는 `src/test/resources/test-data/`에 저장
- `setup.sql`로 데이터 준비
- `cleanup.sql`로 데이터 정리
- 클래스 레벨과 메서드 레벨 모두 지원
- `executionPhase`로 실행 시점 제어
- 복잡한 관계 데이터는 SQL로 준비

