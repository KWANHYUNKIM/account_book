# Integration Test - Controller Layer

## 개요
Controller 계층을 통합 테스트합니다. MockMvc를 사용하여 HTTP 요청을 시뮬레이션합니다.

## 예시

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionService transactionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void should_ReturnTransactions_When_GetAll() throws Exception {
        // Given
        List<TransactionDto> transactions = List.of(
            TransactionDto.builder()
                .id(1L)
                .amount(10000)
                .description("점심 식사")
                .build()
        );
        
        when(transactionService.getAllTransactions())
            .thenReturn(transactions);
        
        // When & Then
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].amount").value(10000))
            .andExpect(jsonPath("$.data[0].description").value("점심 식사"));
    }
    
    @Test
    void should_CreateTransaction_When_ValidRequest() throws Exception {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(10000)
            .type(TransactionType.EXPENSE)
            .description("점심 식사")
            .sessionId(1L)
            .categoryId(1L)
            .build();
            
        TransactionDto response = TransactionDto.builder()
            .id(1L)
            .amount(10000)
            .description("점심 식사")
            .build();
            
        when(transactionService.createTransaction(any(TransactionDto.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.amount").value(10000));
    }
    
    @Test
    void should_Return400_When_InvalidRequest() throws Exception {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(-1000) // 음수는 유효하지 않음
            .build();
        
        // When & Then
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void should_Return404_When_TransactionNotFound() throws Exception {
        // Given
        when(transactionService.getTransactionById(999L))
            .thenThrow(new EntityNotFoundException("Transaction not found"));
        
        // When & Then
        mockMvc.perform(get("/api/transactions/999")
                .header("Authorization", "Bearer test-token"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Transaction not found"));
    }
}
```

## 베스트 프랙티스
- `@WebMvcTest`로 웹 레이어만 로드
- `@MockBean`으로 Service 모킹
- `MockMvc`로 HTTP 요청 시뮬레이션
- `ObjectMapper`로 JSON 변환
- HTTP 상태 코드와 응답 본문 검증
- 인증 헤더 포함 (JWT 테스트 시)

