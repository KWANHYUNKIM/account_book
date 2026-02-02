package com.household.budget.service;

import com.household.budget.config.UserContext;
import com.household.budget.dto.TransactionDto;
import com.household.budget.entity.Category;
import com.household.budget.entity.Transaction;
import com.household.budget.entity.User;
import com.household.budget.model.TransactionModel;
import com.household.budget.repository.CategoryRepository;
import com.household.budget.repository.TransactionRepository;
import com.household.budget.repository.BudgetSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MVC 패턴 - Service 계층
 * 비즈니스 로직 처리 및 Model 변환
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetSessionRepository sessionRepository;
    private final AuthService authService;

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
        if (isAdmin()) {
            // Admin은 모든 거래 내역 조회 가능
            return transactionRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        Long userId = getCurrentUserId();
        return transactionRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByType(String type) {
        if (isAdmin()) {
            // Admin은 모든 거래 내역 조회 가능
            return transactionRepository.findAll().stream()
                    .filter(t -> t.getType().equals(type))
                    .sorted((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()))
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        Long userId = getCurrentUserId();
        return transactionRepository.findByUserIdAndTypeOrderByTransactionDateDesc(userId, type).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsBySession(Long sessionId) {
        Long userId = getCurrentUserId();
        if (isAdmin()) {
            return transactionRepository.findAll().stream()
                    .filter(t -> t.getSession() != null && t.getSession().getId().equals(sessionId))
                    .sorted((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()))
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        return transactionRepository.findByUserIdAndSessionId(userId, sessionId).stream()
                .sorted((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다: " + id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!transaction.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        return toDto(transaction);
    }

    /**
     * 거래 생성 (비즈니스 로직)
     */
    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        // Model로 변환하여 유효성 검증
        TransactionModel model = toModel(transactionDto);
        if (!model.isValid()) {
            throw new IllegalArgumentException("거래 정보가 유효하지 않습니다.");
        }

        Long userId = getCurrentUserId();
        User user = authService.getUserById(userId);
        
        Transaction transaction = new Transaction();
        transaction.setType(model.getType());
        transaction.setAmount(model.getAmount());
        transaction.setDescription(model.getDescription());
        transaction.setTransactionDate(model.getTransactionDate() != null 
                ? model.getTransactionDate() 
                : LocalDateTime.now());
        transaction.setUser(user);

        if (model.getCategoryId() != null) {
            Category category = categoryRepository.findById(model.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + model.getCategoryId()));
            transaction.setCategory(category);
        }

        if (model.getSessionId() != null) {
            com.household.budget.entity.BudgetSession session = sessionRepository.findByUserIdAndId(userId, model.getSessionId())
                    .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + model.getSessionId()));
            transaction.setSession(session);
        }

        return toDto(transactionRepository.save(transaction));
    }

    /**
     * DTO를 Model로 변환
     */
    private TransactionModel toModel(TransactionDto dto) {
        TransactionModel model = new TransactionModel();
        model.setId(dto.getId());
        model.setType(dto.getType());
        model.setAmount(dto.getAmount());
        model.setDescription(dto.getDescription());
        model.setCategoryId(dto.getCategoryId());
        model.setSessionId(dto.getSessionId());
        model.setTransactionDate(dto.getTransactionDate());
        model.setCreatedAt(dto.getCreatedAt());
        return model;
    }

    @Transactional
    public TransactionDto updateTransaction(Long id, TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다: " + id));
        
        Long userId = getCurrentUserId();
        if (!isAdmin()) {
            if (!transaction.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }

        transaction.setType(transactionDto.getType());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setTransactionDate(transactionDto.getTransactionDate());

        if (transactionDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(transactionDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + transactionDto.getCategoryId()));
            transaction.setCategory(category);
        }

        if (transactionDto.getSessionId() != null) {
            com.household.budget.entity.BudgetSession session = sessionRepository.findByUserIdAndId(userId, transactionDto.getSessionId())
                    .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + transactionDto.getSessionId()));
            transaction.setSession(session);
        }

        return toDto(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다: " + id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!transaction.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        transactionRepository.deleteById(id);
    }

    public BigDecimal getTotalIncome() {
        if (isAdmin()) {
            // Admin은 모든 사용자의 총 수입 합계
            BigDecimal total = transactionRepository.findAll().stream()
                    .filter(t -> "INCOME".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return total;
        }
        Long userId = getCurrentUserId();
        BigDecimal total = transactionRepository.getTotalByUserIdAndType(userId, "INCOME");
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpense() {
        if (isAdmin()) {
            // Admin은 모든 사용자의 총 지출 합계
            BigDecimal total = transactionRepository.findAll().stream()
                    .filter(t -> "EXPENSE".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return total;
        }
        Long userId = getCurrentUserId();
        BigDecimal total = transactionRepository.getTotalByUserIdAndType(userId, "EXPENSE");
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    private TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setCreatedAt(transaction.getCreatedAt());
        
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
            dto.setCategoryName(transaction.getCategory().getName());
        }
        
        if (transaction.getSession() != null) {
            dto.setSessionId(transaction.getSession().getId());
            dto.setSessionName(transaction.getSession().getName());
        }
        
        return dto;
    }
}
