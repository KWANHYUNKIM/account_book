package com.household.budget.service;

import com.household.budget.config.UserContext;
import com.household.budget.dto.BudgetSessionDto;
import com.household.budget.entity.BudgetSession;
import com.household.budget.entity.User;
import com.household.budget.repository.BudgetSessionRepository;
import com.household.budget.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetSessionService {
    private final BudgetSessionRepository sessionRepository;
    private final TransactionRepository transactionRepository;
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

    public List<BudgetSessionDto> getAllSessions() {
        Long userId = getCurrentUserId();
        List<BudgetSession> sessions = sessionRepository.findByUserIdOrderByLastAccessedAtDesc(userId);
        return sessions.stream()
                .map(session -> toDto(session))
                .collect(Collectors.toList());
    }

    public BudgetSessionDto getSessionById(Long id) {
        Long userId = getCurrentUserId();
        BudgetSession session = sessionRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + id));
        
        // 마지막 접근 시간 업데이트
        session.setLastAccessedAt(LocalDateTime.now());
        sessionRepository.save(session);
        
        return toDto(session);
    }

    @Transactional
    public BudgetSessionDto createSession(BudgetSessionDto sessionDto) {
        Long userId = getCurrentUserId();
        User user = authService.getUserById(userId);

        BudgetSession session = new BudgetSession();
        session.setName(sessionDto.getName());
        session.setDescription(sessionDto.getDescription());
        session.setColor(sessionDto.getColor() != null ? sessionDto.getColor() : "#0070f3");
        session.setIcon(sessionDto.getIcon() != null ? sessionDto.getIcon() : "💰");
        session.setUser(user);

        return toDto(sessionRepository.save(session));
    }

    @Transactional
    public BudgetSessionDto updateSession(Long id, BudgetSessionDto sessionDto) {
        Long userId = getCurrentUserId();
        BudgetSession session = sessionRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + id));

        session.setName(sessionDto.getName());
        session.setDescription(sessionDto.getDescription());
        session.setColor(sessionDto.getColor());
        session.setIcon(sessionDto.getIcon());

        return toDto(sessionRepository.save(session));
    }

    @Transactional
    public void deleteSession(Long id) {
        Long userId = getCurrentUserId();
        BudgetSession session = sessionRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + id));
        sessionRepository.deleteById(id);
    }

    private BudgetSessionDto toDto(BudgetSession session) {
        BudgetSessionDto dto = new BudgetSessionDto();
        dto.setId(session.getId());
        dto.setName(session.getName());
        dto.setDescription(session.getDescription());
        dto.setColor(session.getColor());
        dto.setIcon(session.getIcon());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setLastAccessedAt(session.getLastAccessedAt());

        // 세션별 통계 계산
        Long sessionId = session.getId();
        List<com.household.budget.entity.Transaction> transactions = 
            transactionRepository.findAll().stream()
                .filter(t -> t.getSession() != null && t.getSession().getId().equals(sessionId))
                .collect(Collectors.toList());

        dto.setTransactionCount((long) transactions.size());
        
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .map(com.household.budget.entity.Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalExpense = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(com.household.budget.entity.Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalIncome(totalIncome);
        dto.setTotalExpense(totalExpense);
        dto.setBalance(totalIncome.subtract(totalExpense));

        return dto;
    }
}

