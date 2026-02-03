package com.household.budget.interfaces.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.household.budget.application.services.TransactionApplicationService;
import com.household.budget.config.JwtAuthenticationFilter;
import com.household.budget.config.JwtUtil;
import com.household.budget.interfaces.http.dto.TransactionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller 테스트
 * Application Service를 Mock으로 처리
 */
@WebMvcTest(TransactionController.class)
@DisplayName("TransactionController 테스트")
class TransactionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionApplicationService transactionService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("모든 거래 조회 성공")
    void should_GetAllTransactions_When_Valid() throws Exception {
        // Given
        TransactionDto transaction = new TransactionDto();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("10000"));
        transaction.setDescription("점심 식사");
        
        when(transactionService.getAllTransactions())
            .thenReturn(List.of(transaction));
        
        // When & Then
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].amount").value(10000));
    }
    
    @Test
    @DisplayName("거래 ID로 조회 성공")
    void should_GetTransactionById_When_ValidId() throws Exception {
        // Given
        TransactionDto transaction = new TransactionDto();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("10000"));
        
        when(transactionService.getTransactionById(1L))
            .thenReturn(transaction);
        
        // When & Then
        mockMvc.perform(get("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.amount").value(10000));
    }
    
    @Test
    @DisplayName("거래 생성 성공")
    void should_CreateTransaction_When_ValidRequest() throws Exception {
        // Given
        TransactionDto request = new TransactionDto();
        request.setAmount(new BigDecimal("10000"));
        request.setType("EXPENSE");
        request.setDescription("점심 식사");
        request.setSessionId(1L);
        
        TransactionDto response = new TransactionDto();
        response.setId(1L);
        response.setAmount(new BigDecimal("10000"));
        
        when(transactionService.createTransaction(any(TransactionDto.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.amount").value(10000));
    }
    
    @Test
    @DisplayName("거래 수정 성공")
    void should_UpdateTransaction_When_ValidRequest() throws Exception {
        // Given
        TransactionDto request = new TransactionDto();
        request.setAmount(new BigDecimal("20000"));
        request.setType("INCOME");
        
        TransactionDto response = new TransactionDto();
        response.setId(1L);
        response.setAmount(new BigDecimal("20000"));
        
        when(transactionService.updateTransaction(any(Long.class), any(TransactionDto.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.amount").value(20000));
    }
    
    @Test
    @DisplayName("거래 삭제 성공")
    void should_DeleteTransaction_When_ValidId() throws Exception {
        // Given
        doNothing().when(transactionService).deleteTransaction(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @DisplayName("요약 정보 조회 성공")
    void should_GetSummary_When_Valid() throws Exception {
        // Given
        when(transactionService.getTotalIncome())
            .thenReturn(new BigDecimal("50000"));
        when(transactionService.getTotalExpense())
            .thenReturn(new BigDecimal("30000"));
        when(transactionService.getBalance())
            .thenReturn(new BigDecimal("20000"));
        
        // When & Then
        mockMvc.perform(get("/api/transactions/summary")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalIncome").value(50000))
            .andExpect(jsonPath("$.data.totalExpense").value(30000))
            .andExpect(jsonPath("$.data.balance").value(20000));
    }
}

