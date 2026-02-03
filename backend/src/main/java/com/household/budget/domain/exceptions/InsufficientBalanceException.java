package com.household.budget.domain.exceptions;

import java.math.BigDecimal;

public class InsufficientBalanceException extends DomainException {
    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        super(String.format("잔액이 부족합니다. 현재: %s, 필요: %s", currentBalance, requiredAmount));
    }
}

