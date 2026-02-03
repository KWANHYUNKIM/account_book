# Spring Boot Testing Best Practices

## 테스트 구조
- 단위 테스트: Service, Repository 계층
- 통합 테스트: Controller 계층
- 엔드투엔드 테스트: 전체 플로우

## 테스트 파일 위치
```
backend/src/test/java/com/household/budget/
├── controller/     # Controller 테스트
├── service/        # Service 테스트
├── repository/     # Repository 테스트
└── util/          # 테스트 유틸리티
```

## 테스트 네이밍 규칙
- 테스트 클래스: `{ClassName}Test` (예: `TransactionServiceTest`)
- 테스트 메서드: `should_{expectedBehavior}_when_{condition}` (예: `should_ReturnTransaction_When_ValidId`)
- 또는: `test_{methodName}_{scenario}` (예: `test_createTransaction_Success`)

## 테스트 어노테이션
- `@SpringBootTest`: 전체 컨텍스트 로드 (통합 테스트)
- `@WebMvcTest`: 웹 레이어만 로드 (Controller 테스트)
- `@DataJpaTest`: JPA 레이어만 로드 (Repository 테스트)
- `@MockBean`: Spring Bean 모킹
- `@Mock`: Mockito 모킹
- `@Testcontainers`: 테스트 컨테이너 사용

## 테스트 프로파일
- `@ActiveProfiles("test")`: 테스트 전용 프로파일 사용
- `application-test.properties`: 테스트 설정 분리

## 테스트 데이터 관리
- `@Sql`: SQL 스크립트로 테스트 데이터 준비
- Builder 패턴으로 테스트 데이터 생성
- `@DirtiesContext`: 컨텍스트 재로드 (필요시만 사용)

## 테스트 커버리지
- 최소 70% 이상 유지
- 핵심 비즈니스 로직은 100% 커버리지 목표
- JaCoCo로 커버리지 측정

