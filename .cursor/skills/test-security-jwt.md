# Test Security & JWT

## 개요
Spring Security와 JWT 인증을 테스트합니다.

## 예시

```java
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@WebMvcTest(TransactionController.class)
class TransactionControllerSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionService transactionService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // @WithMockUser 사용
    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void should_ReturnTransactions_When_Authenticated() throws Exception {
        // Given
        when(transactionService.getAllTransactions())
            .thenReturn(List.of());
        
        // When & Then
        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isOk());
    }
    
    @Test
    void should_Return401_When_NotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isUnauthorized());
    }
    
    // JWT 토큰 직접 사용
    @Test
    void should_ReturnTransactions_When_ValidJwtToken() throws Exception {
        // Given
        String token = "Bearer " + generateTestToken("test@example.com", 1L);
        
        when(transactionService.getAllTransactions())
            .thenReturn(List.of());
        
        // When & Then
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", token))
            .andExpect(status().isOk());
    }
    
    // SecurityMockMvcRequestPostProcessors 사용
    @Test
    void should_CreateTransaction_When_Authenticated() throws Exception {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(10000)
            .build();
            
        TransactionDto response = TransactionDto.builder()
            .id(1L)
            .amount(10000)
            .build();
            
        when(transactionService.createTransaction(any()))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/transactions")
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")
                    .roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }
    
    // UserContext 테스트
    @Test
    @WithMockUser(username = "test@example.com")
    void should_UseCurrentUser_When_CreatingTransaction() throws Exception {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(10000)
            .sessionId(1L)
            .build();
        
        // When
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        
        // Then
        ArgumentCaptor<TransactionDto> captor = ArgumentCaptor.forClass(TransactionDto.class);
        verify(transactionService).createTransaction(captor.capture());
        // UserContext에서 가져온 사용자 ID가 포함되었는지 검증
    }
    
    // 테스트용 JWT 토큰 생성 유틸리티
    private String generateTestToken(String email, Long userId) {
        return Jwts.builder()
            .setSubject(email)
            .claim("userId", userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(SignatureAlgorithm.HS256, "test-secret-key")
            .compact();
    }
}

// SecurityConfig 테스트
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class SecurityConfigTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void should_AllowPublicAccess_To_LoginEndpoint() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
            .andExpect(status().isOk());
    }
    
    @Test
    void should_RequireAuthentication_For_ProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isUnauthorized());
    }
}
```

## 베스트 프랙티스
- `@WithMockUser`로 인증된 사용자 시뮬레이션
- JWT 토큰 직접 생성하여 테스트
- `SecurityMockMvcRequestPostProcessors`로 세밀한 제어
- 인증되지 않은 요청도 테스트
- 역할 기반 접근 제어 테스트

