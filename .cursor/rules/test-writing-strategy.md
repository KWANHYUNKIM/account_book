# 테스트 코드 작성 전략

## 📋 기본 원칙

### ✅ 반드시 테스트를 작성해야 하는 경우

1. **핵심 비즈니스 로직 (Domain Services)**
   - 모든 Domain Service는 100% 테스트 커버리지 목표
   - 예: `TransactionCalculationService`, `BudgetCalculationService`
   - 이유: 비즈니스 규칙이 변경되면 전체 시스템에 영향

2. **복잡한 로직이 있는 Application Services**
   - 여러 도메인 서비스를 조합하는 복잡한 플로우
   - 예: `TransactionApplicationService.createTransaction()`
   - 이유: 비즈니스 플로우 검증 필요

3. **보안 관련 기능**
   - 인증/인가 로직
   - 예: `AuthService`, `JwtUtil`
   - 이유: 보안 취약점 방지

4. **데이터 검증 로직**
   - Validator 클래스
   - DTO 유효성 검사
   - 이유: 잘못된 데이터로 인한 버그 방지

5. **외부 API 통합**
   - Open Banking API 호출
   - 예: `OpenBankingService`, `CardApiService`
   - 이유: 외부 의존성 모킹 및 통합 테스트

### ⚠️ 선택적으로 테스트를 작성하는 경우

1. **단순 CRUD 작업**
   - Repository의 기본 `save()`, `findById()` 등
   - 이유: Spring Data JPA가 이미 검증됨
   - 예외: 복잡한 쿼리나 커스텀 로직이 있는 경우

2. **단순 매핑 로직**
   - DTO ↔ Entity 변환
   - 이유: 로직이 단순하고 버그 가능성 낮음
   - 예외: 복잡한 변환 로직이 있는 경우

3. **View/UI 컴포넌트**
   - 프론트엔드 컴포넌트
   - 이유: E2E 테스트로 대체 가능

### ❌ 테스트를 작성하지 않는 경우

1. **Getter/Setter 메서드**
   - Lombok이 생성한 메서드
   - 이유: 자동 생성 코드

2. **Configuration 클래스**
   - `@Configuration` 클래스의 Bean 정의
   - 이유: Spring이 자동 검증

3. **단순 위임 메서드**
   - 다른 서비스의 메서드를 그대로 호출만 하는 경우
   - 이유: 중복 테스트

## 🎯 Clean Architecture별 테스트 우선순위

### 1순위: Domain Layer (필수)
```
domain/
  services/          ← 100% 테스트 커버리지 목표
  entities/          ← 복잡한 비즈니스 로직이 있는 경우만
  exceptions/        ← 선택적
```

**예시:**
- ✅ `TransactionCalculationService` - 필수
- ✅ `BudgetCalculationService` - 필수
- ⚠️ `Transaction` 엔티티의 `isIncome()`, `isExpense()` - 선택적

### 2순위: Application Layer (중요)
```
application/
  services/          ← 복잡한 플로우가 있는 경우 필수
  validators/        ← 필수
  dto/               ← 선택적 (복잡한 검증 로직이 있는 경우)
```

**예시:**
- ✅ `TransactionApplicationService` - 필수
- ✅ `AuthApplicationService` - 필수
- ✅ `TransactionValidator` - 필수
- ⚠️ 단순 CRUD만 하는 Application Service - 선택적

### 3순위: Infrastructure Layer (선택적)
```
infrastructure/
  database/
    jpa/             ← 복잡한 쿼리나 커스텀 로직이 있는 경우만
  external/
    api/             ← 필수 (외부 API 통합)
```

**예시:**
- ✅ `OpenBankingService` - 필수 (외부 API)
- ✅ `TransactionJpaRepository`의 복잡한 쿼리 - 선택적
- ❌ 단순 JPA Repository 메서드 - 불필요

### 4순위: Interfaces Layer (선택적)
```
interfaces/
  http/
    controller/      ← 핵심 엔드포인트만
  dto/               ← 선택적
```

**예시:**
- ✅ `AuthController` - 필수 (보안)
- ✅ `TransactionController` - 선택적 (핵심 기능)
- ⚠️ 단순 CRUD Controller - 선택적

## 📝 테스트 작성 가이드라인

### TDD (Test-Driven Development) vs 테스트 후 작성

#### TDD를 권장하는 경우:
1. **새로운 비즈니스 로직 구현**
   - Domain Service 작성 시
   - 복잡한 계산 로직
   - 예: "거래 생성 시 잔액 검증 로직"

2. **보안 기능**
   - 인증/인가 로직
   - 예: "JWT 토큰 검증"

#### 테스트 후 작성도 허용하는 경우:
1. **기존 코드 리팩토링**
   - 이미 동작하는 코드를 Clean Architecture로 이동
   - 리팩토링 후 테스트 작성

2. **프로토타입 단계**
   - 빠른 프로토타이핑이 필요한 경우
   - 안정화 후 테스트 추가

### 테스트 커버리지 목표

```
Domain Layer:     80-100% (목표: 100%)
Application:     60-80%  (목표: 80%)
Infrastructure:  40-60%  (목표: 60%)
Interfaces:       40-60%  (목표: 60%)
전체 평균:        60-70%  (목표: 70%)
```

## 🔄 실무 워크플로우

### 새 기능 개발 시

1. **Domain Service 작성**
   ```
   ✅ 테스트 먼저 작성 (TDD)
   ✅ 비즈니스 로직 구현
   ✅ 테스트 통과 확인
   ```

2. **Application Service 작성**
   ```
   ✅ Domain Service Mock으로 테스트 작성
   ✅ Application Service 구현
   ✅ 테스트 통과 확인
   ```

3. **Controller 작성**
   ```
   ⚠️ 핵심 엔드포인트만 테스트
   ✅ Application Service Mock으로 테스트
   ✅ Controller 구현
   ```

### 버그 수정 시

1. **버그 재현 테스트 작성**
   ```
   ✅ 실패하는 테스트 먼저 작성
   ✅ 버그 수정
   ✅ 테스트 통과 확인
   ```

### 리팩토링 시

1. **기존 테스트 확인**
   ```
   ✅ 기존 테스트가 있는지 확인
   ✅ 테스트가 통과하는지 확인
   ✅ 리팩토링 진행
   ✅ 테스트 통과 확인
   ```

## 📊 테스트 유형별 작성 기준

### Unit Test (단위 테스트)
- **언제**: 모든 Domain Service, Application Service
- **목표**: 개별 메서드의 로직 검증
- **예시**: `TransactionCalculationServiceTest`

### Integration Test (통합 테스트)
- **언제**: 
  - 전체 플로우 검증이 필요한 경우
  - 데이터베이스와의 통합
  - 외부 API 통합
- **예시**: `AuthServiceIntegrationTest`

### Controller Test (컨트롤러 테스트)
- **언제**: 
  - 핵심 API 엔드포인트
  - 보안이 중요한 엔드포인트
- **예시**: `AuthControllerTest`, `TransactionControllerTest`

## 🎓 실무 팁

### DO ✅
- 핵심 비즈니스 로직은 항상 테스트 작성
- 테스트 이름은 명확하게 (`should_ReturnError_When_InvalidInput`)
- Given-When-Then 패턴 사용
- Mock은 필요한 경우만 사용

### DON'T ❌
- 모든 코드에 100% 테스트 작성 시도 (비효율적)
- Getter/Setter 테스트
- 단순 위임 메서드 테스트
- 테스트를 위한 테스트 작성

## 📚 참고 문서

- `.cursor/rules/test-priority-strategy.md` - 테스트 우선순위 전략
- `.cursor/rules/clean-architecture-backend.md` - Clean Architecture 구조
- `.cursor/skills/test-*.md` - 각 테스트 유형별 가이드

