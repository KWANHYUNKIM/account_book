package com.household.budget.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MVC 패턴 - Model 계층
 * 거래 도메인 모델 (비즈니스 로직 처리용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String description;
    private Long categoryId;
    private Long sessionId;
    private Long userId;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;

    /**
     * 거래 유효성 검증
     */
    public boolean isValid() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0
                && type != null && (type.equals("INCOME") || type.equals("EXPENSE"));
    }

    /**
     * 수입 여부 확인
     */
    public boolean isIncome() {
        return "INCOME".equals(type);
    }

    /**
     * 지출 여부 확인
     */
    public boolean isExpense() {
        return "EXPENSE".equals(type);
    }
}


