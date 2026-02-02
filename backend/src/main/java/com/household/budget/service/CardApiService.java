package com.household.budget.service;

import com.household.budget.entity.BankAccount;
import com.household.budget.entity.Transaction;
import com.household.budget.repository.BankAccountRepository;
import com.household.budget.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 카드사 API 연동 서비스
 * 각 카드사별로 API가 다르므로 추상화된 구조 제공
 * 실제 연동 시 카드사별 구현 필요
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardApiService {
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    private boolean isAdmin() {
        String email = com.household.budget.config.UserContext.getCurrentUserEmail();
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

    /**
     * 카드사별 OAuth 인증 URL 생성
     */
    public String getAuthorizationUrl(String cardCompany) {
        // TODO: 카드사별 인증 URL 생성
        // 예: 신한카드, KB국민카드, 하나카드 등
        log.info("카드사 인증 URL 생성 요청: {}", cardCompany);
        return "https://api." + cardCompany.toLowerCase() + ".com/oauth/authorize";
    }

    /**
     * OAuth 콜백 처리 및 토큰 저장
     */
    @Transactional
    public void handleOAuthCallback(Long accountId, String authorizationCode) {
        log.info("카드사 OAuth 콜백 처리: accountId={}, code={}", accountId, authorizationCode);
        
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + accountId));
        
        if (!isAdmin()) {
            String email = com.household.budget.config.UserContext.getCurrentUserEmail();
            Long userId = authService.getUserByEmail(email).getId();
            if (!account.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        // Mock: 실제로는 카드사 API 호출하여 토큰 받아옴
        account.setAccessToken("mock_card_token_" + System.currentTimeMillis());
        account.setRefreshToken("mock_card_refresh_" + System.currentTimeMillis());
        account.setTokenExpiresAt(LocalDateTime.now().plusHours(2));
        account.setIsActive(true);
        
        bankAccountRepository.save(account);
    }

    /**
     * 카드 거래 내역 동기화
     */
    @Transactional
    public void syncTransactions(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + accountId));
        
        if (!isAdmin()) {
            String email = com.household.budget.config.UserContext.getCurrentUserEmail();
            Long currentUserId = authService.getUserByEmail(email).getId();
            if (!account.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }

        if (!account.getIsActive() || account.getAccessToken() == null) {
            throw new RuntimeException("카드가 활성화되지 않았거나 토큰이 없습니다.");
        }

        // TODO: 실제 카드사 API 호출
        // 예: GET /api/v1/transactions
        log.info("카드 거래 내역 동기화 시작: accountId={}", accountId);

        // Mock 데이터 생성
        List<Transaction> mockTransactions = createMockCardTransactions(account);
        
        // final 변수로 복사 (람다 표현식에서 사용하기 위해)
        final Long finalUserId = account.getUser().getId();
        
        for (Transaction transaction : mockTransactions) {
            // 중복 체크 (사용자별)
            if (transaction.getExternalTransactionId() != null) {
                final String transactionExternalId = transaction.getExternalTransactionId();
                boolean exists = transactionRepository.findByUserId(finalUserId).stream()
                        .anyMatch(t -> t.getExternalTransactionId() != null 
                                && t.getExternalTransactionId().equals(transactionExternalId)
                                && t.getUser().getId().equals(finalUserId));
                
                if (!exists) {
                    transactionRepository.save(transaction);
                }
            } else {
                transactionRepository.save(transaction);
            }
        }

        // 마지막 동기화 시간 업데이트
        account.setLastSyncedAt(LocalDateTime.now());
        bankAccountRepository.save(account);

        log.info("카드 거래 내역 동기화 완료: accountId={}, count={}", accountId, mockTransactions.size());
    }

    /**
     * Mock 카드 거래 내역 생성
     */
    private List<Transaction> createMockCardTransactions(BankAccount account) {
        return List.of(
                createMockTransaction(account, "EXPENSE", new BigDecimal("30000"), "마트 구매", "쇼핑"),
                createMockTransaction(account, "EXPENSE", new BigDecimal("25000"), "주유", "교통비"),
                createMockTransaction(account, "EXPENSE", new BigDecimal("12000"), "영화 관람", "문화생활")
        );
    }

    private Transaction createMockTransaction(BankAccount account, String type, 
                                             BigDecimal amount, String description, String categoryName) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setBankAccount(account);
        transaction.setUser(account.getUser()); // 사용자 설정
        transaction.setTransactionDate(LocalDateTime.now().minusDays((long)(Math.random() * 30)));
        transaction.setExternalTransactionId("CARD_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000));
        transaction.setSyncSource("CARD_API");
        return transaction;
    }
}

