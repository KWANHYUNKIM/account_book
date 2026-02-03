package com.household.budget.infrastructure.database.jpa;

import com.household.budget.domain.entities.Transaction;
import com.household.budget.domain.repositories.TransactionRepository;
import com.household.budget.infrastructure.database.jpa.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Infrastructure - JPA Repository 구현
 * Domain Repository 인터페이스를 구현
 */
@Repository
public class TransactionJpaRepository implements TransactionRepository {
    
    private final SpringDataTransactionRepository springDataRepository;
    
    public TransactionJpaRepository(SpringDataTransactionRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }
    
    @Override
    public Optional<Transaction> findById(Long id) {
        return springDataRepository.findById(id)
            .map(TransactionEntity::toDomain);
    }
    
    @Override
    public List<Transaction> findAll() {
        return springDataRepository.findAll().stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionEntity.fromDomain(transaction);
        TransactionEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
    
    @Override
    public List<Transaction> findByUserId(Long userId) {
        return springDataRepository.findByUserId(userId).stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByUserIdAndType(Long userId, String type) {
        return springDataRepository.findByUserIdAndType(userId, type).stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(Long userId, String type) {
        return springDataRepository.findByUserIdAndTypeOrderByTransactionDateDesc(userId, type).stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByUserIdAndSessionId(Long userId, Long sessionId) {
        return springDataRepository.findByUserIdAndSessionId(userId, sessionId).stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByUserIdAndSessionIdAndType(Long userId, Long sessionId, String type) {
        return springDataRepository.findByUserIdAndSessionIdAndType(userId, sessionId, type).stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByUserIdAndSessionIdAndTypeOrderByTransactionDateDesc(Long userId, Long sessionId, String type) {
        return springDataRepository.findByUserIdAndSessionIdAndTypeOrderByTransactionDateDesc(userId, sessionId, type).stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return springDataRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal getTotalByUserIdAndType(Long userId, String type) {
        return springDataRepository.getTotalByUserIdAndType(userId, type);
    }
    
    @Override
    public BigDecimal getTotalByUserIdAndTypeAndDateRange(Long userId, String type, LocalDateTime startDate, LocalDateTime endDate) {
        return springDataRepository.getTotalByUserIdAndTypeAndDateRange(userId, type, startDate, endDate);
    }
    
}

