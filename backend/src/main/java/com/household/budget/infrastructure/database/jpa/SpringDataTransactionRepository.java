package com.household.budget.infrastructure.database.jpa;

import com.household.budget.infrastructure.database.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA Repository 인터페이스
 */
@Repository
public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByUserId(Long userId);
    List<TransactionEntity> findByUserIdAndType(Long userId, String type);
    List<TransactionEntity> findByUserIdAndTypeOrderByTransactionDateDesc(Long userId, String type);
    List<TransactionEntity> findByUserIdAndSessionId(Long userId, Long sessionId);
    List<TransactionEntity> findByUserIdAndSessionIdAndType(Long userId, Long sessionId, String type);
    List<TransactionEntity> findByUserIdAndSessionIdAndTypeOrderByTransactionDateDesc(Long userId, Long sessionId, String type);
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.userId = :userId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.userId = :userId AND t.type = :type")
    BigDecimal getTotalByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
    
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.userId = :userId AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalByUserIdAndTypeAndDateRange(@Param("userId") Long userId,
                                                    @Param("type") String type,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
}

