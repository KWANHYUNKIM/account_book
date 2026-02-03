# Test Integration E2E (4순위)

## 개요
통합 시나리오 테스트는 핵심 플로우만 테스트합니다. **70% 커버리지**를 목표로 합니다.

## 예시: 전체 플로우 테스트

```java
// __tests__/integration/TransactionFlowTest.java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TransactionFlowTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BudgetSessionRepository sessionRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private String authToken;
    private Long userId;
    private Long sessionId;
    
    @BeforeEach
    void setUp() throws Exception {
        // 1. 사용자 생성 및 로그인
        userId = createUser("test@example.com", "password123");
        authToken = login("test@example.com", "password123");
        
        // 2. 세션 생성
        sessionId = createSession(authToken, "테스트 세션");
    }
    
    @Test
    void should_CreateTransaction_And_UpdateBalance() throws Exception {
        // Given: 세션이 있고 인증된 상태
        
        // When: 거래 생성 (지출)
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("10000"))
            .type(TransactionType.EXPENSE)
            .description("점심 식사")
            .sessionId(sessionId)
            .categoryId(1L)
            .build();
        
        // Then: 거래가 생성됨
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.amount").value(10000))
            .andExpect(jsonPath("$.data.description").value("점심 식사"));
        
        // 세션 잔액이 업데이트됨
        mockMvc.perform(get("/api/sessions/" + sessionId)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.balance").value(-10000));
    }
    
    @Test
    void should_CompleteTransactionFlow() throws Exception {
        // 시나리오: 로그인 → 세션 생성 → 수입 추가 → 지출 추가 → 잔액 확인
        
        // 1. 수입 추가
        CreateTransactionRequest incomeRequest = CreateTransactionRequest.builder()
            .amount(new BigDecimal("50000"))
            .type(TransactionType.INCOME)
            .description("월급")
            .sessionId(sessionId)
            .build();
        
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incomeRequest)))
            .andExpect(status().isCreated());
        
        // 2. 지출 추가
        CreateTransactionRequest expenseRequest = CreateTransactionRequest.builder()
            .amount(new BigDecimal("30000"))
            .type(TransactionType.EXPENSE)
            .description("생활비")
            .sessionId(sessionId)
            .build();
        
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
            .andExpect(status().isCreated());
        
        // 3. 거래 목록 조회
        mockMvc.perform(get("/api/transactions?sessionId=" + sessionId)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2));
        
        // 4. 세션 잔액 확인 (50000 - 30000 = 20000)
        mockMvc.perform(get("/api/sessions/" + sessionId)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.balance").value(20000));
    }
    
    @Test
    void should_HandleUnauthorizedAccess() throws Exception {
        // Given: 인증 토큰 없음
        
        // When & Then: 인증되지 않은 요청은 거부됨
        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    void should_NotAllowExpense_When_BalanceInsufficient() throws Exception {
        // Given: 잔액이 0인 세션
        
        // When: 잔액보다 큰 지출 시도
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(new BigDecimal("100000"))
            .type(TransactionType.EXPENSE)
            .sessionId(sessionId)
            .build();
        
        // Then: 거부됨
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("잔액이 부족합니다")));
    }
    
    // Helper methods
    
    private Long createUser(String email, String password) {
        User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name("Test User")
            .build();
        return userRepository.save(user).getId();
    }
    
    private String login(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest(email, password);
        
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();
        
        AuthResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthResponse.class
        );
        
        return response.getToken();
    }
    
    private Long createSession(String token, String name) throws Exception {
        CreateSessionRequest request = CreateSessionRequest.builder()
            .name(name)
            .description("테스트용 세션")
            .build();
        
        MvcResult result = mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();
        
        ApiResponse<BudgetSessionDto> response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<ApiResponse<BudgetSessionDto>>() {}
        );
        
        return response.getData().getId();
    }
}
```

## 베스트 프랙티스
- **핵심 시나리오만 테스트**: 주요 사용자 플로우
- **실제 데이터베이스 사용**: H2 인메모리 DB
- **트랜잭션 롤백**: `@Transactional`로 테스트 간 격리
- **인증 플로우 포함**: 로그인부터 시작하는 전체 플로우
- **에러 케이스**: 인증 실패, 권한 없음 등
- **성능 고려**: 통합 테스트는 느리므로 최소화

