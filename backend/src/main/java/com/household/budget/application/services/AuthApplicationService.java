package com.household.budget.application.services;

import com.household.budget.service.AuthService;
import com.household.budget.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application Service - Auth 관련 Use Case
 * 기존 AuthService를 래핑 (점진적 마이그레이션)
 */
@Service
@RequiredArgsConstructor
public class AuthApplicationService {
    private final AuthService authService;
    
    public User getUserByEmail(String email) {
        return authService.getUserByEmail(email);
    }
    
    public User getUserById(Long id) {
        return authService.getUserById(id);
    }
}

