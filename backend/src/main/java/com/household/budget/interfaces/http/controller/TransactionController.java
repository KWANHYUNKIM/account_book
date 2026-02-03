package com.household.budget.interfaces.http.controller;

import com.household.budget.application.services.TransactionApplicationService;
import com.household.budget.interfaces.http.dto.TransactionDto;
import com.household.budget.interfaces.http.dto.ApiResponse;
import com.household.budget.interfaces.http.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaces 계층 - HTTP Controller
 * HTTP 요청을 받아 Application Service에 위임
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3100")
public class TransactionController {
    private final TransactionApplicationService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getAllTransactions() {
        try {
            List<TransactionDto> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 내역 조회 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionById(@PathVariable Long id) {
        try {
            TransactionDto transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(ApiResponse.success(transaction));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("거래를 찾을 수 없습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 조회 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByType(@PathVariable String type) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByType(type);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 내역 조회 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsBySession(@PathVariable Long sessionId) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsBySession(sessionId);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 내역 조회 실패: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            TransactionDto created = transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("거래가 생성되었습니다", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("잘못된 요청: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("거래 생성 실패: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 생성 실패: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> updateTransaction(@PathVariable Long id, 
                                                             @RequestBody TransactionDto transactionDto) {
        try {
            TransactionDto updated = transactionService.updateTransaction(id, transactionDto);
            return ResponseEntity.ok(ApiResponse.success("거래가 수정되었습니다", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("거래를 찾을 수 없습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 수정 실패: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok(ApiResponse.success("거래가 삭제되었습니다", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("거래를 찾을 수 없습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 삭제 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<TransactionResponse.SummaryResponse>> getSummary() {
        try {
            TransactionResponse.SummaryResponse summary = new TransactionResponse.SummaryResponse(
                    transactionService.getTotalIncome(),
                    transactionService.getTotalExpense(),
                    transactionService.getBalance()
            );
            return ResponseEntity.ok(ApiResponse.success(summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("요약 정보 조회 실패: " + e.getMessage()));
        }
    }
}

