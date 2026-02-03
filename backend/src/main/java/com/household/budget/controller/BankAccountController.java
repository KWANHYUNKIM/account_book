package com.household.budget.controller;

import com.household.budget.dto.BankAccountDto;
import com.household.budget.service.BankAccountService;
import com.household.budget.service.CardApiService;
import com.household.budget.service.OpenBankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3100")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final OpenBankingService openBankingService;
    private final CardApiService cardApiService;

    @GetMapping
    public ResponseEntity<List<BankAccountDto>> getAllAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllAccounts());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BankAccountDto>> getActiveAccounts() {
        return ResponseEntity.ok(bankAccountService.getActiveAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDto> getAccountById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bankAccountService.getAccountById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<BankAccountDto> createAccount(@RequestBody BankAccountDto accountDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bankAccountService.createAccount(accountDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccountDto> updateAccount(@PathVariable Long id, 
                                                         @RequestBody BankAccountDto accountDto) {
        try {
            return ResponseEntity.ok(bankAccountService.updateAccount(id, accountDto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            bankAccountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 오픈뱅킹 인증 URL 생성
     */
    @GetMapping("/{id}/openbanking/auth-url")
    public ResponseEntity<Map<String, String>> getOpenBankingAuthUrl(@PathVariable Long id,
                                                                     @RequestParam String bankCode) {
        try {
            String authUrl = openBankingService.getAuthorizationUrl(bankCode);
            return ResponseEntity.ok(Map.of("authUrl", authUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 오픈뱅킹 OAuth 콜백 처리
     */
    @PostMapping("/{id}/openbanking/callback")
    public ResponseEntity<Void> handleOpenBankingCallback(@PathVariable Long id,
                                                          @RequestParam String code) {
        try {
            openBankingService.handleOAuthCallback(id, code);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 오픈뱅킹 거래 내역 동기화
     */
    @PostMapping("/{id}/openbanking/sync")
    public ResponseEntity<Map<String, String>> syncOpenBankingTransactions(@PathVariable Long id) {
        try {
            openBankingService.syncTransactions(id);
            return ResponseEntity.ok(Map.of("message", "동기화가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 카드사 인증 URL 생성
     */
    @GetMapping("/{id}/card/auth-url")
    public ResponseEntity<Map<String, String>> getCardAuthUrl(@PathVariable Long id,
                                                               @RequestParam String cardCompany) {
        try {
            String authUrl = cardApiService.getAuthorizationUrl(cardCompany);
            return ResponseEntity.ok(Map.of("authUrl", authUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 카드사 OAuth 콜백 처리
     */
    @PostMapping("/{id}/card/callback")
    public ResponseEntity<Void> handleCardCallback(@PathVariable Long id,
                                                  @RequestParam String code) {
        try {
            cardApiService.handleOAuthCallback(id, code);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 카드 거래 내역 동기화
     */
    @PostMapping("/{id}/card/sync")
    public ResponseEntity<Map<String, String>> syncCardTransactions(@PathVariable Long id) {
        try {
            cardApiService.syncTransactions(id);
            return ResponseEntity.ok(Map.of("message", "동기화가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}


