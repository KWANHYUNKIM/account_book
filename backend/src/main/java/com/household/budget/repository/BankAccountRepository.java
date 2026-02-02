package com.household.budget.repository;

import com.household.budget.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUserId(Long userId);
    List<BankAccount> findByUserIdAndIsActiveTrue(Long userId);
    List<BankAccount> findByUserIdAndConnectionType(Long userId, String connectionType);
    Optional<BankAccount> findByUserIdAndId(Long userId, Long id);
    List<BankAccount> findByIsActiveTrue();
}

