package com.household.budget.application.services;

import com.household.budget.config.UserContext;
import com.household.budget.domain.entities.Transaction;
import com.household.budget.domain.exceptions.InsufficientBalanceException;
import com.household.budget.domain.exceptions.TransactionNotFoundException;
import com.household.budget.domain.repositories.TransactionRepository;
import com.household.budget.domain.services.TransactionCalculationService;
import com.household.budget.interfaces.http.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service - Use Case 구현
 * Domain Services를 조합하여 비즈니스 플로우 처리
 */
@Service
@RequiredArgsConstructor
public class TransactionApplicationService {
    private final TransactionRepository transactionRepository;
    private final TransactionCalculationService calculationService;
    private final AuthApplicationService authService;
    
    private Long getCurrentUserId() {
        String email = UserContext.getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("인증이 필요합니다.");
        }
        return authService.getUserByEmail(email).getId();
    }
    
    private boolean isAdmin() {
        String email = UserContext.getCurrentUserEmail();
        if (email == null) {
            return false;
        }
        try {
            var user = authService.getUserByEmail(email);
            return "ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions;
        if (isAdmin()) {
            transactions = transactionRepository.findAll();
        } else {
            Long userId = getCurrentUserId();
            transactions = transactionRepository.findByUserId(userId);
        }
        
        return transactions.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsByType(String type) {
        List<Transaction> transactions;
        if (isAdmin()) {
            transactions = transactionRepository.findAll().stream()
                .filter(t -> t.getType().equals(type))
                .collect(Collectors.toList());
        } else {
            Long userId = getCurrentUserId();
            transactions = transactionRepository.findByUserIdAndTypeOrderByTransactionDateDesc(userId, type);
        }
        
        return transactions.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsBySession(Long sessionId) {
        Long userId = getCurrentUserId();
        List<Transaction> transactions;
        
        if (isAdmin()) {
            transactions = transactionRepository.findAll().stream()
                .filter(t -> t.getSessionId() != null && t.getSessionId().equals(sessionId))
                .collect(Collectors.toList());
        } else {
            transactions = transactionRepository.findByUserIdAndSessionId(userId, sessionId);
        }
        
        return transactions.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException(id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!transaction.getUserId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        return toDto(transaction);
    }
    
    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        // Domain Entity로 변환
        Transaction transaction = toDomain(transactionDto);
        
        // Domain 비즈니스 로직 검증
        if (!transaction.isValid()) {
            throw new IllegalArgumentException("거래 정보가 유효하지 않습니다.");
        }
        
        Long userId = getCurrentUserId();
        transaction.setUserId(userId);
        transaction.setTransactionDate(transaction.getTransactionDate() != null 
            ? transaction.getTransactionDate() 
            : LocalDateTime.now());
        
        // 잔액 검증 (지출인 경우)
        if (transaction.isExpense() && transaction.getSessionId() != null) {
            // 세션 잔액 확인 로직 (간단화)
            // 실제로는 SessionRepository를 통해 잔액을 가져와야 함
        }
        
        Transaction saved = transactionRepository.save(transaction);
        return toDto(saved);
    }
    
    @Transactional
    public TransactionDto updateTransaction(Long id, TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException(id));
        
        Long userId = getCurrentUserId();
        if (!isAdmin() && !transaction.getUserId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        transaction.setType(transactionDto.getType());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setTransactionDate(transactionDto.getTransactionDate());
        transaction.setCategoryId(transactionDto.getCategoryId());
        transaction.setSessionId(transactionDto.getSessionId());
        
        Transaction saved = transactionRepository.save(transaction);
        return toDto(saved);
    }
    
    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException(id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!transaction.getUserId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        transactionRepository.deleteById(id);
    }
    
    public BigDecimal getTotalIncome() {
        if (isAdmin()) {
            List<Transaction> all = transactionRepository.findAll();
            return calculationService.calculateSummary(all).getTotalIncome();
        }
        Long userId = getCurrentUserId();
        BigDecimal total = transactionRepository.getTotalByUserIdAndType(userId, "INCOME");
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalExpense() {
        if (isAdmin()) {
            List<Transaction> all = transactionRepository.findAll();
            return calculationService.calculateSummary(all).getTotalExpense();
        }
        Long userId = getCurrentUserId();
        BigDecimal total = transactionRepository.getTotalByUserIdAndType(userId, "EXPENSE");
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }
    
    // DTO 변환
    private TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setCategoryId(transaction.getCategoryId());
        dto.setSessionId(transaction.getSessionId());
        return dto;
    }
    
    private Transaction toDomain(TransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setSessionId(dto.getSessionId());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setCreatedAt(dto.getCreatedAt());
        return transaction;
    }
}

