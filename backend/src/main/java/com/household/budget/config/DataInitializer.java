package com.household.budget.config;

import com.household.budget.entity.Category;
import com.household.budget.entity.User;
import com.household.budget.repository.CategoryRepository;
import com.household.budget.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin 계정 생성
        if (!userRepository.existsByEmail("admin")) {
            User admin = new User();
            admin.setEmail("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("관리자");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        // 기본 카테고리 생성
        if (categoryRepository.count() == 0) {
            // 기본 수입 카테고리
            categoryRepository.save(new Category(null, "급여", "INCOME", "월급"));
            categoryRepository.save(new Category(null, "부수입", "INCOME", "알바, 용돈 등"));
            categoryRepository.save(new Category(null, "투자수익", "INCOME", "배당금, 이자 등"));
            categoryRepository.save(new Category(null, "기타수입", "INCOME", "기타 수입"));

            // 기본 지출 카테고리
            categoryRepository.save(new Category(null, "식비", "EXPENSE", "음식, 식료품"));
            categoryRepository.save(new Category(null, "교통비", "EXPENSE", "대중교통, 주유비"));
            categoryRepository.save(new Category(null, "주거비", "EXPENSE", "월세, 관리비, 전기세 등"));
            categoryRepository.save(new Category(null, "의료비", "EXPENSE", "병원비, 약값"));
            categoryRepository.save(new Category(null, "교육비", "EXPENSE", "학원비, 도서 등"));
            categoryRepository.save(new Category(null, "문화생활", "EXPENSE", "영화, 공연, 취미"));
            categoryRepository.save(new Category(null, "쇼핑", "EXPENSE", "의류, 생활용품"));
            categoryRepository.save(new Category(null, "기타지출", "EXPENSE", "기타 지출"));
        }
    }
}

