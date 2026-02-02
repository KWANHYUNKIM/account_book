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
 * 오픈뱅킹 API 연동 서비스
 * 실제 연동을 위해서는 한국금융결제원 오픈뱅킹 API 사용
 * 현재는 Mock 구현으로 구조만 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenBankingService {
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
     * 오픈뱅킹 OAuth 인증 URL 생성
     * 실제 구현 시 오픈뱅킹 API의 인증 URL 반환
     */
    public String getAuthorizationUrl(String bankCode) {
        // TODO: 실제 오픈뱅킹 API 인증 URL 생성
        // 예: https://openapi.openbanking.or.kr/oauth/2.0/authorize?...
        log.info("오픈뱅킹 인증 URL 생성 요청: {}", bankCode);
        return "https://openapi.openbanking.or.kr/oauth/2.0/authorize?bank_code=" + bankCode;
    }

    /**
     * OAuth 콜백 처리 및 토큰 저장
     */
    @Transactional
    public void handleOAuthCallback(Long accountId, String authorizationCode) {
        // TODO: 실제 오픈뱅킹 API로 토큰 교환
        log.info("OAuth 콜백 처리: accountId={}, code={}", accountId, authorizationCode);
        
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + accountId));
        
        if (!isAdmin()) {
            String email = com.household.budget.config.UserContext.getCurrentUserEmail();
            Long userId = authService.getUserByEmail(email).getId();
            if (!account.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        // Mock: 실제로는 API 호출하여 토큰 받아옴
        account.setAccessToken("mock_access_token_" + System.currentTimeMillis());
        account.setRefreshToken("mock_refresh_token_" + System.currentTimeMillis());
        account.setTokenExpiresAt(LocalDateTime.now().plusHours(2));
        account.setIsActive(true);
        
        bankAccountRepository.save(account);
    }

    /**
     * 계좌 거래 내역 동기화
     */
    @Transactional
    public void syncTransactions(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + accountId));
        
        Long userId = account.getUser().getId();
        if (!isAdmin()) {
            String email = com.household.budget.config.UserContext.getCurrentUserEmail();
            Long currentUserId = authService.getUserByEmail(email).getId();
            if (!account.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
            userId = currentUserId;
        }

        if (!account.getIsActive() || account.getAccessToken() == null) {
            throw new RuntimeException("계좌가 활성화되지 않았거나 토큰이 없습니다.");
        }

        // TODO: 실제 오픈뱅킹 API 호출
        // 예: GET /v2.0/account/transaction_list
        log.info("거래 내역 동기화 시작: accountId={}", accountId);

        // Mock 데이터 생성 (실제로는 API 응답 파싱)
        List<Transaction> mockTransactions = createMockTransactions(account);
        
        // final 변수로 복사 (람다 표현식에서 사용하기 위해)
        final Long finalUserId = userId;
        
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

        log.info("거래 내역 동기화 완료: accountId={}, count={}", accountId, mockTransactions.size());
    }

    /**
     * Mock 거래 내역 생성 (실제로는 API 응답 파싱)
     */
    private List<Transaction> createMockTransactions(BankAccount account) {
        // 실제 구현 시 API 응답을 파싱하여 Transaction 객체 생성
        return List.of(
                createMockTransaction(account, "EXPENSE", new BigDecimal("5000"), "커피", "식비"),
                createMockTransaction(account, "EXPENSE", new BigDecimal("15000"), "점심 식사", "식비"),
                createMockTransaction(account, "INCOME", new BigDecimal("2000000"), "월급", "급여")
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
        transaction.setExternalTransactionId("EXT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000));
        transaction.setSyncSource("OPENBANKING");
        return transaction;
    }
}

