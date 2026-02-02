package com.household.budget.repository;

import com.household.budget.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByUserIdAndType(Long userId, String type);
    
    List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(Long userId, String type);
    
    List<Transaction> findByUserIdAndSessionId(Long userId, Long sessionId);
    
    List<Transaction> findByUserIdAndSessionIdAndType(Long userId, Long sessionId, String type);
    
    List<Transaction> findByUserIdAndSessionIdAndTypeOrderByTransactionDateDesc(Long userId, Long sessionId, String type);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    java.math.BigDecimal getTotalByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalByUserIdAndTypeAndDateRange(@Param("userId") Long userId,
                                                              @Param("type") String type,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);
}

