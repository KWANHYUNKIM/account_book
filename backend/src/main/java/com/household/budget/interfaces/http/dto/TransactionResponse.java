package com.household.budget.interfaces.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class TransactionResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal balance;
    }
}

