# Test Exception Handling

## 개요
예외 처리 로직을 테스트합니다.

## 예시

```java
import org.springframework.web.bind.MethodArgumentNotValidException;

class TransactionServiceTest {
    
    @Test
    void should_ThrowException_When_EntityNotFound() {
        // Given
        when(transactionRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactionById(999L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Transaction not found with id: 999");
    }
    
    @Test
    void should_ThrowException_When_InvalidAmount() {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(-1000) // 음수는 유효하지 않음
            .build();
        
        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Amount must be positive");
    }
    
    @Test
    void should_HandleValidationException() {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(0) // 0도 유효하지 않음
            .build();
        
        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Amount must be greater than 0");
    }
}

// Controller 예외 처리 테스트
@WebMvcTest(TransactionController.class)
class TransactionControllerExceptionTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionService transactionService;
    
    @Test
    void should_Return404_When_TransactionNotFound() throws Exception {
        // Given
        when(transactionService.getTransactionById(999L))
            .thenThrow(new EntityNotFoundException("Transaction not found"));
        
        // When & Then
        mockMvc.perform(get("/api/transactions/999")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Transaction not found"));
    }
    
    @Test
    void should_Return400_When_ValidationFails() throws Exception {
        // Given
        TransactionDto request = TransactionDto.builder()
            .amount(-1000)
            .build();
        
        // When & Then
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors").exists());
    }
    
    @Test
    void should_Return500_When_InternalError() throws Exception {
        // Given
        when(transactionService.getAllTransactions())
            .thenThrow(new RuntimeException("Database connection failed"));
        
        // When & Then
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}

// @ControllerAdvice 테스트
@WebMvcTest
class GlobalExceptionHandlerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void should_HandleEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/api/transactions/999")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void should_HandleMethodArgumentNotValidException() throws Exception {
        TransactionDto request = TransactionDto.builder()
            .amount(null) // @NotNull 위반
            .build();
        
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray());
    }
}
```

## 베스트 프랙티스
- `assertThatThrownBy()`로 예외 발생 검증
- 예외 타입과 메시지 모두 검증
- HTTP 상태 코드와 응답 본문 검증
- `@ControllerAdvice`의 예외 처리도 테스트
- Validation 예외도 테스트

