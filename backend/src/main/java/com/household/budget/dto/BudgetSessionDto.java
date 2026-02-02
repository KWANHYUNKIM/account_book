package com.household.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSessionDto {
    private Long id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private Long transactionCount;
    private java.math.BigDecimal totalIncome;
    private java.math.BigDecimal totalExpense;
    private java.math.BigDecimal balance;
}

