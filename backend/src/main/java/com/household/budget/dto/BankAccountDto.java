package com.household.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {
    private Long id;
    private String accountName;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountType;
    private String connectionType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastSyncedAt;
}

