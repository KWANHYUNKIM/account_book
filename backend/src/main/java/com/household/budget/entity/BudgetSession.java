package com.household.budget.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "budget_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 세션 이름 (예: "2024년 가계부", "여행 예산")

    @Column
    private String description; // 세션 설명

    @Column
    private String color; // 세션 색상 (카드 배경색)

    @Column
    private String icon; // 세션 아이콘

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 소유자

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastAccessedAt; // 마지막 접근 시간

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (lastAccessedAt == null) {
            lastAccessedAt = LocalDateTime.now();
        }
    }
}

