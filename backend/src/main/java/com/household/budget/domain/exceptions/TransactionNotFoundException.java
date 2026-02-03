package com.household.budget.domain.exceptions;

public class TransactionNotFoundException extends DomainException {
    public TransactionNotFoundException(Long id) {
        super("거래를 찾을 수 없습니다: " + id);
    }
}

