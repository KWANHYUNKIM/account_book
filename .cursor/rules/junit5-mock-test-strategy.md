# JUnit 5 & Mock 테스트 전략

## 테스트 구조 (Clean Architecture와 일치)

```
backend/src/test/java/com/household/budget/
├── domain/                    # Domain 계층 테스트 (Mock 불필요)
│   ├── services/             # TransactionCalculationServiceTest
│   └── entities/             # TransactionTest (도메인 로직)
├── application/              # Application 계층 테스트 (Repository Mock)
│   └── services/             # TransactionApplicationServiceTest
├── infrastructure/           # Infrastructure 계층 테스트
│   └── database/jpa/         # TransactionJpaRepositoryTest (@DataJpaTest)
└── interfaces/               # Interfaces 계층 테스트
    └── http/
        ├── controller/       # TransactionControllerTest (@WebMvcTest)
        └── validation/        # TransactionValidatorTest
```

## 테스트 전략별 접근법

### 1. Domain Services (순수 Java, Mock 불필요)
- **특징**: 외부 의존성 없음
- **전략**: 순수 단위 테스트
- **도구**: JUnit 5만 사용

```java
@ExtendWith(MockitoExtension.class) // 불필요하지만 일관성을 위해
class TransactionCalculationServiceTest {
    // Mock 없이 순수 테스트
}
```

### 2. Application Services (Repository Mock 필요)
- **특징**: Domain Repository에 의존
- **전략**: Repository를 Mock으로 처리
- **도구**: JUnit 5 + Mockito

```java
@ExtendWith(MockitoExtension.class)
class TransactionApplicationServiceTest {
    @Mock
    private TransactionRepository repository;
    
    @InjectMocks
    private TransactionApplicationService service;
}
```

### 3. Infrastructure (실제 DB 테스트)
- **특징**: JPA Repository 구현체
- **전략**: @DataJpaTest로 실제 DB 사용
- **도구**: JUnit 5 + H2 인메모리 DB

```java
@DataJpaTest
@ActiveProfiles("test")
class TransactionJpaRepositoryTest {
    @Autowired
    private SpringDataTransactionRepository repository;
}
```

### 4. Interfaces/Controller (MockMvc 사용)
- **특징**: HTTP 요청/응답 테스트
- **전략**: Application Service를 Mock
- **도구**: JUnit 5 + MockMvc

```java
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionApplicationService service;
}
```

## 의존성 확인

Spring Boot Starter Test에는 이미 포함:
- JUnit 5 (Jupiter)
- Mockito
- AssertJ
- Spring Test & Spring Boot Test

추가 필요 시:
```xml
<!-- Mockito 추가 기능 (이미 포함되어 있음) -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

## 테스트 실행 순서

1. **Domain Services** (가장 빠름, Mock 불필요)
2. **Application Services** (Repository Mock)
3. **Infrastructure** (실제 DB, 느림)
4. **Interfaces** (MockMvc, 중간 속도)

## 베스트 프랙티스
- Domain은 Mock 없이 순수 테스트
- Application은 Repository만 Mock
- Infrastructure는 실제 DB 사용
- Controller는 Application Service Mock
- 테스트는 독립적으로 실행 가능해야 함
- Given-When-Then 패턴 사용

