package com.household.budget.domain.repositories;

import com.household.budget.domain.entities.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Domain Repository 인터페이스
 * 구현체는 infrastructure에 위치
 */
public interface TransactionRepository {
    Optional<Transaction> findById(Long id);
    List<Transaction> findAll();
    Transaction save(Transaction transaction);
    void deleteById(Long id);
    
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndType(Long userId, String type);
    List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(Long userId, String type);
    List<Transaction> findByUserIdAndSessionId(Long userId, Long sessionId);
    List<Transaction> findByUserIdAndSessionIdAndType(Long userId, Long sessionId, String type);
    List<Transaction> findByUserIdAndSessionIdAndTypeOrderByTransactionDateDesc(Long userId, Long sessionId, String type);
    List<Transaction> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    BigDecimal getTotalByUserIdAndType(Long userId, String type);
    BigDecimal getTotalByUserIdAndTypeAndDateRange(Long userId, String type, LocalDateTime startDate, LocalDateTime endDate);
}

