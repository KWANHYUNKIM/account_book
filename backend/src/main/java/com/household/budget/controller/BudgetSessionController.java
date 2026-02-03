package com.household.budget.controller;

import com.household.budget.dto.BudgetSessionDto;
import com.household.budget.service.BudgetSessionService;
import com.household.budget.view.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MVC 패턴 - Controller 계층
 * 가계부 세션 관련 HTTP 요청 처리
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3100")
public class BudgetSessionController {
    private final BudgetSessionService sessionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetSessionDto>>> getAllSessions() {
        try {
            List<BudgetSessionDto> sessions = sessionService.getAllSessions();
            return ResponseEntity.ok(ApiResponse.success(sessions));
        } catch (RuntimeException e) {
            // 인증 관련 에러는 401로 반환
            if (e.getMessage() != null && e.getMessage().contains("인증")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("세션 목록 조회 실패: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("세션 목록 조회 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetSessionDto>> getSessionById(@PathVariable Long id) {
        try {
            BudgetSessionDto session = sessionService.getSessionById(id);
            return ResponseEntity.ok(ApiResponse.success(session));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("세션을 찾을 수 없습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("세션 조회 실패: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetSessionDto>> createSession(@RequestBody BudgetSessionDto sessionDto) {
        try {
            BudgetSessionDto created = sessionService.createSession(sessionDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("세션이 생성되었습니다", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("잘못된 요청: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("세션 생성 실패: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetSessionDto>> updateSession(@PathVariable Long id, 
                                                           @RequestBody BudgetSessionDto sessionDto) {
        try {
            BudgetSessionDto updated = sessionService.updateSession(id, sessionDto);
            return ResponseEntity.ok(ApiResponse.success("세션이 수정되었습니다", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("세션을 찾을 수 없습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("세션 수정 실패: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        try {
            sessionService.deleteSession(id);
            return ResponseEntity.ok(ApiResponse.success("세션이 삭제되었습니다", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("세션을 찾을 수 없습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("세션 삭제 실패: " + e.getMessage()));
        }
    }
}

