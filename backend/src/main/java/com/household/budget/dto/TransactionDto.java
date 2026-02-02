package com.household.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long sessionId;
    private String sessionName;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
}

