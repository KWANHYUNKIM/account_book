package com.household.budget.view;

import com.household.budget.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MVC 패턴 - View 계층
 * 거래 관련 응답 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private List<TransactionDto> transactions;
    private Long totalCount;
    private SummaryResponse summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private java.math.BigDecimal totalIncome;
        private java.math.BigDecimal totalExpense;
        private java.math.BigDecimal balance;
    }
}


