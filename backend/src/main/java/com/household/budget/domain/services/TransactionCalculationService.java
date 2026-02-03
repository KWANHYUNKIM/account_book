package com.household.budget.domain.services;

import com.household.budget.domain.entities.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Domain Service - 핵심 비즈니스 로직
 * 순수 Java, 외부 의존성 없음
 */
@Component
public class TransactionCalculationService {
    
    public TransactionSummary calculateSummary(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return TransactionSummary.empty();
        }
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.isExpense()) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        return TransactionSummary.builder()
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .balance(balance)
            .transactionCount(transactions.size())
            .build();
    }
    
    public boolean canCreateTransaction(BigDecimal currentBalance, BigDecimal amount, String type) {
        if (currentBalance == null || amount == null || type == null) {
            return false;
        }
        
        if ("INCOME".equals(type)) {
            return true; // 수입은 항상 가능
        } else {
            // 지출은 잔액이 충분해야 함
            return currentBalance.compareTo(amount) >= 0;
        }
    }
    
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, String type) {
        if (currentBalance == null || amount == null || type == null) {
            throw new IllegalArgumentException("필수 파라미터가 null입니다.");
        }
        
        return "INCOME".equals(type)
            ? currentBalance.add(amount)
            : currentBalance.subtract(amount);
    }
    
    public static class TransactionSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal balance;
        private int transactionCount;
        
        public static TransactionSummary empty() {
            return new TransactionSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }
        
        public TransactionSummary(BigDecimal totalIncome, BigDecimal totalExpense, 
                                 BigDecimal balance, int transactionCount) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.balance = balance;
            this.transactionCount = transactionCount;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private BigDecimal totalIncome = BigDecimal.ZERO;
            private BigDecimal totalExpense = BigDecimal.ZERO;
            private BigDecimal balance = BigDecimal.ZERO;
            private int transactionCount = 0;
            
            public Builder totalIncome(BigDecimal totalIncome) {
                this.totalIncome = totalIncome;
                return this;
            }
            
            public Builder totalExpense(BigDecimal totalExpense) {
                this.totalExpense = totalExpense;
                return this;
            }
            
            public Builder balance(BigDecimal balance) {
                this.balance = balance;
                return this;
            }
            
            public Builder transactionCount(int transactionCount) {
                this.transactionCount = transactionCount;
                return this;
            }
            
            public TransactionSummary build() {
                return new TransactionSummary(totalIncome, totalExpense, balance, transactionCount);
            }
        }
        
        // Getters
        public BigDecimal getTotalIncome() { return totalIncome; }
        public BigDecimal getTotalExpense() { return totalExpense; }
        public BigDecimal getBalance() { return balance; }
        public int getTransactionCount() { return transactionCount; }
    }
}

