package com.household.budget.service;

import com.household.budget.config.UserContext;
import com.household.budget.dto.BankAccountDto;
import com.household.budget.entity.BankAccount;
import com.household.budget.entity.User;
import com.household.budget.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final AuthService authService;

    private Long getCurrentUserId() {
        String email = UserContext.getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("인증이 필요합니다.");
        }
        return authService.getUserByEmail(email).getId();
    }

    private boolean isAdmin() {
        String email = UserContext.getCurrentUserEmail();
        if (email == null) {
            return false;
        }
        try {
            var user = authService.getUserByEmail(email);
            return "ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    public List<BankAccountDto> getAllAccounts() {
        if (isAdmin()) {
            // Admin은 모든 계좌 조회 가능
            return bankAccountRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        Long userId = getCurrentUserId();
        return bankAccountRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BankAccountDto> getActiveAccounts() {
        if (isAdmin()) {
            // Admin은 모든 활성 계좌 조회 가능
            return bankAccountRepository.findByIsActiveTrue().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        Long userId = getCurrentUserId();
        return bankAccountRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BankAccountDto getAccountById(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!account.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        return toDto(account);
    }

    @Transactional
    public BankAccountDto createAccount(BankAccountDto accountDto) {
        Long userId = getCurrentUserId();
        User user = authService.getUserById(userId);
        
        BankAccount account = new BankAccount();
        account.setAccountName(accountDto.getAccountName());
        account.setBankCode(accountDto.getBankCode());
        account.setBankName(accountDto.getBankName());
        account.setAccountNumber(accountDto.getAccountNumber());
        account.setAccountType(accountDto.getAccountType());
        account.setConnectionType(accountDto.getConnectionType());
        account.setIsActive(accountDto.getIsActive() != null ? accountDto.getIsActive() : true);
        account.setUser(user);
        
        return toDto(bankAccountRepository.save(account));
    }

    @Transactional
    public BankAccountDto updateAccount(Long id, BankAccountDto accountDto) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!account.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        account.setAccountName(accountDto.getAccountName());
        account.setBankCode(accountDto.getBankCode());
        account.setBankName(accountDto.getBankName());
        account.setAccountNumber(accountDto.getAccountNumber());
        account.setAccountType(accountDto.getAccountType());
        account.setConnectionType(accountDto.getConnectionType());
        account.setIsActive(accountDto.getIsActive());
        
        return toDto(bankAccountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!account.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        bankAccountRepository.deleteById(id);
    }

    @Transactional
    public void updateLastSyncedAt(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다: " + id));
        
        if (!isAdmin()) {
            Long userId = getCurrentUserId();
            if (!account.getUser().getId().equals(userId)) {
                throw new RuntimeException("권한이 없습니다.");
            }
        }
        
        account.setLastSyncedAt(java.time.LocalDateTime.now());
        bankAccountRepository.save(account);
    }

    private BankAccountDto toDto(BankAccount account) {
        return new BankAccountDto(
                account.getId(),
                account.getAccountName(),
                account.getBankCode(),
                account.getBankName(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getConnectionType(),
                account.getIsActive(),
                account.getCreatedAt(),
                account.getLastSyncedAt()
        );
    }
}
