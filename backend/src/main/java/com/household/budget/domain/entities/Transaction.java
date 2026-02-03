package com.household.budget.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain Entity - 비즈니스 로직 포함
 * JPA 어노테이션 없음 (순수 도메인 객체)
 */
public class Transaction {
    private Long id;
    private String type; // "INCOME" or "EXPENSE"
    private BigDecimal amount;
    private String description;
    private Long categoryId;
    private Long userId;
    private Long sessionId;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private String externalTransactionId;
    private String syncSource;

    public Transaction() {
    }

    public Transaction(Long id, String type, BigDecimal amount, String description, 
                      Long categoryId, Long userId, Long sessionId, 
                      LocalDateTime transactionDate, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.transactionDate = transactionDate != null ? transactionDate : LocalDateTime.now();
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // 비즈니스 로직
    public boolean isIncome() {
        return "INCOME".equals(type);
    }

    public boolean isExpense() {
        return "EXPENSE".equals(type);
    }

    public BigDecimal calculateBalance(BigDecimal currentBalance) {
        if (isIncome()) {
            return currentBalance.add(amount);
        } else {
            return currentBalance.subtract(amount);
        }
    }

    public boolean isValid() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0
                && type != null && (isIncome() || isExpense())
                && description != null && !description.trim().isEmpty();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getExternalTransactionId() { return externalTransactionId; }
    public void setExternalTransactionId(String externalTransactionId) { this.externalTransactionId = externalTransactionId; }
    public String getSyncSource() { return syncSource; }
    public void setSyncSource(String syncSource) { this.syncSource = syncSource; }
}

