package com.household.budget.domain.exceptions;

/**
 * Domain 예외 기본 클래스
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

