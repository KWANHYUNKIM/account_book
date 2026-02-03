package com.household.budget.application.services;

import com.household.budget.config.UserContext;
import com.household.budget.domain.entities.Transaction;
import com.household.budget.domain.exceptions.TransactionNotFoundException;
import com.household.budget.domain.repositories.TransactionRepository;
import com.household.budget.domain.services.TransactionCalculationService;
import com.household.budget.entity.User;
import com.household.budget.interfaces.http.dto.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Application Service 테스트
 * Repository를 Mock으로 처리
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionApplicationService 테스트")
class TransactionApplicationServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private TransactionCalculationService calculationService;
    
    @Mock
    private AuthApplicationService authService;
    
    @InjectMocks
    private TransactionApplicationService service;
    
    private Transaction transaction;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType("EXPENSE");
        transaction.setAmount(new BigDecimal("10000"));
        transaction.setDescription("점심 식사");
        transaction.setUserId(1L);
        transaction.setTransactionDate(LocalDateTime.now());
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole("USER");
    }
    
    @Test
    @DisplayName("거래 목록 조회 성공")
    void should_GetAllTransactions_When_Valid() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            when(transactionRepository.findByUserId(1L))
                .thenReturn(List.of(transaction));
            
            // When
            List<TransactionDto> result = service.getAllTransactions();
            
            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("10000"));
            verify(transactionRepository).findByUserId(any());
        }
    }
    
    @Test
    @DisplayName("거래 ID로 조회 성공")
    void should_GetTransactionById_When_ValidId() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));
            
            // When
            TransactionDto result = service.getTransactionById(1L);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("10000"));
        }
    }
    
    @Test
    @DisplayName("거래를 찾을 수 없을 때 예외 발생")
    void should_ThrowException_When_TransactionNotFound() {
        // Given
        when(transactionRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.getTransactionById(999L))
            .isInstanceOf(TransactionNotFoundException.class);
    }
    
    @Test
    @DisplayName("거래 생성 성공")
    void should_CreateTransaction_When_ValidRequest() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            
            TransactionDto request = new TransactionDto();
            request.setType("EXPENSE");
            request.setAmount(new BigDecimal("10000"));
            request.setDescription("점심 식사");
            request.setSessionId(1L);
            
            Transaction saved = new Transaction();
            saved.setId(1L);
            saved.setType("EXPENSE");
            saved.setAmount(new BigDecimal("10000"));
            saved.setDescription("점심 식사");
            
            when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(saved);
            
            // When
            TransactionDto result = service.createTransaction(request);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(transactionRepository).save(any(Transaction.class));
        }
    }
    
    @Test
    @DisplayName("거래 수정 성공")
    void should_UpdateTransaction_When_ValidRequest() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            
            TransactionDto request = new TransactionDto();
            request.setType("INCOME");
            request.setAmount(new BigDecimal("20000"));
            request.setDescription("용돈");
            
            when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));
            when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);
            
            // When
            TransactionDto result = service.updateTransaction(1L, request);
            
            // Then
            assertThat(result).isNotNull();
            verify(transactionRepository).findById(1L);
            verify(transactionRepository).save(any(Transaction.class));
        }
    }
    
    @Test
    @DisplayName("거래 삭제 성공")
    void should_DeleteTransaction_When_ValidId() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            
            when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));
            doNothing().when(transactionRepository).deleteById(1L);
            
            // When
            service.deleteTransaction(1L);
            
            // Then
            verify(transactionRepository).findById(1L);
            verify(transactionRepository).deleteById(1L);
        }
    }
    
    @Test
    @DisplayName("총 수입 계산")
    void should_GetTotalIncome_When_Valid() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            when(transactionRepository.getTotalByUserIdAndType(1L, "INCOME"))
                .thenReturn(new BigDecimal("50000"));
            
            // When
            BigDecimal result = service.getTotalIncome();
            
            // Then
            assertThat(result).isEqualByComparingTo(new BigDecimal("50000"));
        }
    }
    
    @Test
    @DisplayName("총 지출 계산")
    void should_GetTotalExpense_When_Valid() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            when(transactionRepository.getTotalByUserIdAndType(1L, "EXPENSE"))
                .thenReturn(new BigDecimal("30000"));
            
            // When
            BigDecimal result = service.getTotalExpense();
            
            // Then
            assertThat(result).isEqualByComparingTo(new BigDecimal("30000"));
        }
    }
    
    @Test
    @DisplayName("잔액 계산")
    void should_GetBalance_When_Valid() {
        // Given
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getCurrentUserEmail).thenReturn("test@example.com");
            when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
            when(transactionRepository.getTotalByUserIdAndType(1L, "INCOME"))
                .thenReturn(new BigDecimal("50000"));
            when(transactionRepository.getTotalByUserIdAndType(1L, "EXPENSE"))
                .thenReturn(new BigDecimal("30000"));
            
            // When
            BigDecimal result = service.getBalance();
            
            // Then
            assertThat(result).isEqualByComparingTo(new BigDecimal("20000"));
        }
    }
}

