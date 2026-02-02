package com.household.budget.config;

import com.household.budget.service.CardApiService;
import com.household.budget.service.OpenBankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth 콜백 처리를 위한 컨트롤러
 * 실제 인증 완료 후 리다이렉트되는 URL
 */
@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3100")
public class OAuthCallbackController {
    private final OpenBankingService openBankingService;
    private final CardApiService cardApiService;

    /**
     * 오픈뱅킹 OAuth 콜백
     * 실제 구현 시 오픈뱅킹에서 리다이렉트되는 URL
     */
    @GetMapping("/openbanking/callback")
    public String handleOpenBankingCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Long accountId) {
        
        log.info("오픈뱅킹 OAuth 콜백 수신: code={}, state={}, accountId={}", code, state, accountId);
        
        try {
            if (accountId != null) {
                openBankingService.handleOAuthCallback(accountId, code);
                
                // 인증 완료 후 자동으로 거래 내역 동기화
                openBankingService.syncTransactions(accountId);
                
                // 성공 페이지로 리다이렉트 (프론트엔드)
                return "<html><body><script>window.opener.postMessage({type: 'OAUTH_SUCCESS', accountId: " + accountId + "}, '*'); window.close();</script><h2>인증이 완료되었습니다. 이 창을 닫아주세요.</h2></body></html>";
            }
        } catch (Exception e) {
            log.error("오픈뱅킹 OAuth 처리 실패", e);
            return "<html><body><script>window.opener.postMessage({type: 'OAUTH_ERROR', message: '" + e.getMessage() + "'}, '*'); window.close();</script><h2>인증 처리에 실패했습니다.</h2></body></html>";
        }
        
        return "<html><body><h2>인증 처리 중...</h2></body></html>";
    }

    /**
     * 카드사 OAuth 콜백
     */
    @GetMapping("/card/callback")
    public String handleCardCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Long accountId) {
        
        log.info("카드사 OAuth 콜백 수신: code={}, state={}, accountId={}", code, state, accountId);
        
        try {
            if (accountId != null) {
                cardApiService.handleOAuthCallback(accountId, code);
                
                // 인증 완료 후 자동으로 거래 내역 동기화
                cardApiService.syncTransactions(accountId);
                
                return "<html><body><script>window.opener.postMessage({type: 'OAUTH_SUCCESS', accountId: " + accountId + "}, '*'); window.close();</script><h2>인증이 완료되었습니다. 이 창을 닫아주세요.</h2></body></html>";
            }
        } catch (Exception e) {
            log.error("카드사 OAuth 처리 실패", e);
            return "<html><body><script>window.opener.postMessage({type: 'OAUTH_ERROR', message: '" + e.getMessage() + "'}, '*'); window.close();</script><h2>인증 처리에 실패했습니다.</h2></body></html>";
        }
        
        return "<html><body><h2>인증 처리 중...</h2></body></html>";
    }
}

