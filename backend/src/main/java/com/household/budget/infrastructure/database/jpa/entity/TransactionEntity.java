package com.household.budget.infrastructure.database.jpa.entity;

import com.household.budget.domain.entities.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity - Infrastructure 계층
 * Domain Entity와 매핑
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "bank_account_id")
    private Long bankAccountId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String externalTransactionId;

    @Column
    private String syncSource;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }
    
    // Domain Entity로 변환
    public Transaction toDomain() {
        Transaction domain = new Transaction();
        domain.setId(this.id);
        domain.setType(this.type);
        domain.setAmount(this.amount);
        domain.setDescription(this.description);
        domain.setCategoryId(this.categoryId);
        domain.setUserId(this.userId);
        domain.setSessionId(this.sessionId);
        domain.setTransactionDate(this.transactionDate);
        domain.setCreatedAt(this.createdAt);
        domain.setExternalTransactionId(this.externalTransactionId);
        domain.setSyncSource(this.syncSource);
        return domain;
    }
    
    // Domain Entity에서 생성
    public static TransactionEntity fromDomain(Transaction domain) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.getId());
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount());
        entity.setDescription(domain.getDescription());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExternalTransactionId(domain.getExternalTransactionId());
        entity.setSyncSource(domain.getSyncSource());
        return entity;
    }
}

