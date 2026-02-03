package com.household.budget.domain.services;

import com.household.budget.domain.entities.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Domain Service 테스트 (1순위)
 * Mock 불필요 - 순수 Java 테스트
 */
@DisplayName("TransactionCalculationService 테스트")
class TransactionCalculationServiceTest {
    
    private TransactionCalculationService service;
    
    @BeforeEach
    void setUp() {
        service = new TransactionCalculationService();
    }
    
    @Test
    @DisplayName("빈 리스트일 때 빈 요약 반환")
    void should_ReturnEmptySummary_When_TransactionsIsNull() {
        // When
        TransactionCalculationService.TransactionSummary summary = 
            service.calculateSummary(null);
        
        // Then
        assertThat(summary.getTotalIncome()).isZero();
        assertThat(summary.getTotalExpense()).isZero();
        assertThat(summary.getTransactionCount()).isZero();
    }
    
    @Test
    @DisplayName("빈 리스트일 때 빈 요약 반환")
    void should_ReturnEmptySummary_When_TransactionsIsEmpty() {
        // When
        TransactionCalculationService.TransactionSummary summary = 
            service.calculateSummary(Collections.emptyList());
        
        // Then
        assertThat(summary.getTotalIncome()).isZero();
        assertThat(summary.getTotalExpense()).isZero();
    }
    
    @Test
    @DisplayName("수입만 있을 때 요약 계산")
    void should_CalculateSummary_When_OnlyIncome() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(10000, "INCOME"),
            createTransaction(5000, "INCOME")
        );
        
        // When
        TransactionCalculationService.TransactionSummary summary = 
            service.calculateSummary(transactions);
        
        // Then
        assertThat(summary.getTotalIncome()).isEqualByComparingTo(new BigDecimal("15000"));
        assertThat(summary.getTotalExpense()).isZero();
        assertThat(summary.getBalance()).isEqualByComparingTo(new BigDecimal("15000"));
    }
    
    @Test
    @DisplayName("지출만 있을 때 요약 계산")
    void should_CalculateSummary_When_OnlyExpense() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(5000, "EXPENSE"),
            createTransaction(3000, "EXPENSE")
        );
        
        // When
        TransactionCalculationService.TransactionSummary summary = 
            service.calculateSummary(transactions);
        
        // Then
        assertThat(summary.getTotalIncome()).isZero();
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(new BigDecimal("8000"));
        assertThat(summary.getBalance()).isEqualByComparingTo(new BigDecimal("-8000"));
    }
    
    @Test
    @DisplayName("수입과 지출이 섞여 있을 때 요약 계산")
    void should_CalculateSummary_When_MixedTransactions() {
        // Given
        List<Transaction> transactions = List.of(
            createTransaction(10000, "INCOME"),
            createTransaction(5000, "EXPENSE"),
            createTransaction(3000, "EXPENSE"),
            createTransaction(2000, "INCOME")
        );
        
        // When
        TransactionCalculationService.TransactionSummary summary = 
            service.calculateSummary(transactions);
        
        // Then
        assertThat(summary.getTotalIncome()).isEqualByComparingTo(new BigDecimal("12000"));
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(new BigDecimal("8000"));
        assertThat(summary.getBalance()).isEqualByComparingTo(new BigDecimal("4000"));
        assertThat(summary.getTransactionCount()).isEqualTo(4);
    }
    
    @Test
    @DisplayName("수입은 항상 생성 가능")
    void should_ReturnTrue_When_IncomeType() {
        // Given
        BigDecimal currentBalance = new BigDecimal("1000");
        BigDecimal amount = new BigDecimal("10000");
        
        // When
        boolean canCreate = service.canCreateTransaction(currentBalance, amount, "INCOME");
        
        // Then
        assertThat(canCreate).isTrue();
    }
    
    @Test
    @DisplayName("잔액이 충분할 때 지출 생성 가능")
    void should_ReturnTrue_When_BalanceIsSufficient() {
        // Given
        BigDecimal currentBalance = new BigDecimal("10000");
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        boolean canCreate = service.canCreateTransaction(currentBalance, amount, "EXPENSE");
        
        // Then
        assertThat(canCreate).isTrue();
    }
    
    @Test
    @DisplayName("잔액과 동일한 금액일 때 지출 생성 가능")
    void should_ReturnTrue_When_BalanceEqualsAmount() {
        // Given
        BigDecimal currentBalance = new BigDecimal("5000");
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        boolean canCreate = service.canCreateTransaction(currentBalance, amount, "EXPENSE");
        
        // Then
        assertThat(canCreate).isTrue();
    }
    
    @Test
    @DisplayName("잔액이 부족할 때 지출 생성 불가")
    void should_ReturnFalse_When_BalanceIsInsufficient() {
        // Given
        BigDecimal currentBalance = new BigDecimal("1000");
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        boolean canCreate = service.canCreateTransaction(currentBalance, amount, "EXPENSE");
        
        // Then
        assertThat(canCreate).isFalse();
    }
    
    @Test
    @DisplayName("null 파라미터일 때 false 반환")
    void should_ReturnFalse_When_ParametersAreNull() {
        // When & Then
        assertThat(service.canCreateTransaction(null, new BigDecimal("1000"), "EXPENSE")).isFalse();
        assertThat(service.canCreateTransaction(new BigDecimal("1000"), null, "EXPENSE")).isFalse();
        assertThat(service.canCreateTransaction(new BigDecimal("1000"), new BigDecimal("1000"), null)).isFalse();
    }
    
    @Test
    @DisplayName("수입일 때 잔액 계산")
    void should_AddAmount_When_Income() {
        // Given
        BigDecimal currentBalance = new BigDecimal("10000");
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        BigDecimal newBalance = service.calculateNewBalance(currentBalance, amount, "INCOME");
        
        // Then
        assertThat(newBalance).isEqualByComparingTo(new BigDecimal("15000"));
    }
    
    @Test
    @DisplayName("지출일 때 잔액 계산")
    void should_SubtractAmount_When_Expense() {
        // Given
        BigDecimal currentBalance = new BigDecimal("10000");
        BigDecimal amount = new BigDecimal("5000");
        
        // When
        BigDecimal newBalance = service.calculateNewBalance(currentBalance, amount, "EXPENSE");
        
        // Then
        assertThat(newBalance).isEqualByComparingTo(new BigDecimal("5000"));
    }
    
    @Test
    @DisplayName("null 파라미터일 때 예외 발생")
    void should_ThrowException_When_ParametersAreNull() {
        // When & Then
        assertThatThrownBy(() -> service.calculateNewBalance(null, new BigDecimal("1000"), "INCOME"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("필수 파라미터가 null입니다");
    }
    
    @Test
    @DisplayName("0원일 때도 정상 처리")
    void should_HandleZeroAmount() {
        // Given
        BigDecimal currentBalance = new BigDecimal("10000");
        BigDecimal amount = BigDecimal.ZERO;
        
        // When
        BigDecimal newBalance = service.calculateNewBalance(currentBalance, amount, "INCOME");
        
        // Then
        assertThat(newBalance).isEqualByComparingTo(new BigDecimal("10000"));
    }
    
    // Helper
    private Transaction createTransaction(int amount, String type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(amount));
        transaction.setType(type);
        transaction.setTransactionDate(LocalDateTime.now());
        return transaction;
    }
}

