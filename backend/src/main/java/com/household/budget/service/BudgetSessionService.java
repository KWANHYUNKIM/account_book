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
            throw new RuntimeException("인증이 필요합니다. JWT 토큰이 유효하지 않거나 만료되었습니다.");
        }
        try {
            var user = authService.getUserByEmail(email);
            if (user == null) {
                throw new RuntimeException("사용자를 찾을 수 없습니다: " + email);
            }
            return user.getId();
        } catch (Exception e) {
            throw new RuntimeException("사용자 조회 실패: " + e.getMessage(), e);
        }
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
        try {
            Long userId = getCurrentUserId();
            List<BudgetSession> sessions = sessionRepository.findByUserIdOrderByLastAccessedAtDesc(userId);
            return sessions.stream()
                    .map(session -> toDto(session))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("세션 목록 조회 중 오류 발생: " + e.getMessage(), e);
        }
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
        try {
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
            Long userId = session.getUser() != null ? session.getUser().getId() : getCurrentUserId();
            
            List<com.household.budget.entity.Transaction> transactions = 
                transactionRepository.findByUserIdAndSessionId(userId, sessionId);

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
        } catch (Exception e) {
            // 통계 계산 실패 시 기본값 반환
            BudgetSessionDto dto = new BudgetSessionDto();
            dto.setId(session.getId());
            dto.setName(session.getName());
            dto.setDescription(session.getDescription());
            dto.setColor(session.getColor());
            dto.setIcon(session.getIcon());
            dto.setCreatedAt(session.getCreatedAt());
            dto.setLastAccessedAt(session.getLastAccessedAt());
            dto.setTransactionCount(0L);
            dto.setTotalIncome(BigDecimal.ZERO);
            dto.setTotalExpense(BigDecimal.ZERO);
            dto.setBalance(BigDecimal.ZERO);
            return dto;
        }
    }
}


