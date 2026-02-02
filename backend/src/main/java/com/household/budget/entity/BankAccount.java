package com.household.budget.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName; // 계좌 별칭

    @Column(nullable = false)
    private String bankCode; // 은행 코드 (예: "088", "004", "020")

    @Column(nullable = false)
    private String bankName; // 은행명 (예: "신한은행", "KB국민은행")

    @Column(nullable = false)
    private String accountNumber; // 계좌번호 (마스킹 처리)

    @Column(nullable = false)
    private String accountType; // 계좌 유형 ("CHECKING", "SAVINGS", "CARD")

    @Column(nullable = false)
    private String connectionType; // 연동 유형 ("OPENBANKING", "CARD_API", "MANUAL")

    @Column
    private String accessToken; // OAuth 액세스 토큰 (암호화 저장 필요)

    @Column
    private String refreshToken; // OAuth 리프레시 토큰

    @Column
    private LocalDateTime tokenExpiresAt; // 토큰 만료 시간

    @Column(nullable = false)
    private Boolean isActive = true; // 활성화 여부

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastSyncedAt; // 마지막 동기화 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 소유자

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (lastSyncedAt == null) {
            lastSyncedAt = LocalDateTime.now();
        }
    }
}

