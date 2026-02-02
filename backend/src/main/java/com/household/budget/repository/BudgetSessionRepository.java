package com.household.budget.repository;

import com.household.budget.entity.BudgetSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetSessionRepository extends JpaRepository<BudgetSession, Long> {
    List<BudgetSession> findByUserIdOrderByLastAccessedAtDesc(Long userId);
    Optional<BudgetSession> findByUserIdAndId(Long userId, Long id);
    List<BudgetSession> findByUserIdAndNameContaining(Long userId, String name);
}

