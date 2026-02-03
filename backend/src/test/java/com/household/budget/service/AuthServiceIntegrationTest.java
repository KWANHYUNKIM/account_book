package com.household.budget.service;

import com.household.budget.dto.AuthResponse;
import com.household.budget.dto.LoginRequest;
import com.household.budget.dto.RegisterRequest;
import com.household.budget.entity.User;
import com.household.budget.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AuthService 통합 테스트
 * 실제 데이터베이스(H2)를 사용하여 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthService 통합 테스트")
class AuthServiceIntegrationTest {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("회원가입 및 로그인 전체 플로우")
    void should_RegisterAndLogin_When_ValidFlow() {
        // Given: 회원가입
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("Test User");
        
        // When: 회원가입
        AuthResponse registerResponse = authService.register(registerRequest);
        
        // Then: 회원가입 성공
        assertThat(registerResponse).isNotNull();
        assertThat(registerResponse.getToken()).isNotNull();
        assertThat(registerResponse.getEmail()).isEqualTo("test@example.com");
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        
        // When: 로그인
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        AuthResponse loginResponse = authService.login(loginRequest);
        
        // Then: 로그인 성공
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getToken()).isNotNull();
        assertThat(loginResponse.getEmail()).isEqualTo("test@example.com");
        
        // lastLoginAt이 업데이트되었는지 확인
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(user.getLastLoginAt()).isNotNull();
    }
    
    @Test
    @DisplayName("중복 이메일로 회원가입 실패")
    void should_FailRegister_When_DuplicateEmail() {
        // Given: 첫 번째 회원가입
        RegisterRequest request1 = new RegisterRequest();
        request1.setEmail("duplicate@example.com");
        request1.setPassword("password123");
        request1.setName("User 1");
        authService.register(request1);
        
        // When & Then: 두 번째 회원가입 시도 (같은 이메일)
        RegisterRequest request2 = new RegisterRequest();
        request2.setEmail("duplicate@example.com");
        request2.setPassword("password456");
        request2.setName("User 2");
        
        assertThatThrownBy(() -> authService.register(request2))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("이미 등록된 이메일입니다.");
    }
    
    @Test
    @DisplayName("비밀번호 암호화 확인")
    void should_EncryptPassword_When_Register() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("encrypt@example.com");
        request.setPassword("plainpassword");
        request.setName("Test User");
        
        // When
        authService.register(request);
        
        // Then: 저장된 비밀번호가 암호화되었는지 확인
        User savedUser = userRepository.findByEmail("encrypt@example.com").orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo("plainpassword");
        assertThat(savedUser.getPassword()).startsWith("$2a$"); // BCrypt 해시 형식
        assertThat(passwordEncoder.matches("plainpassword", savedUser.getPassword())).isTrue();
    }
}

