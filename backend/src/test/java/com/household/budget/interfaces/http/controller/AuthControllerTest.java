package com.household.budget.interfaces.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.household.budget.config.JwtAuthenticationFilter;
import com.household.budget.config.JwtUtil;
import com.household.budget.dto.AuthResponse;
import com.household.budget.dto.LoginRequest;
import com.household.budget.dto.RegisterRequest;
import com.household.budget.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 테스트
 * AuthService를 Mock으로 처리
 */
@WebMvcTest(com.household.budget.controller.AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화 (CSRF 우회)
@DisplayName("AuthController 테스트")
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 회원가입 테스트
    
    @Test
    @DisplayName("회원가입 성공")
    void should_Register_When_ValidRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setName("New User");
        
        AuthResponse response = new AuthResponse(
            "jwt-token",
            "new@example.com",
            "New User",
            "회원가입이 완료되었습니다."
        );
        
        when(authService.register(any(RegisterRequest.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("jwt-token"))
            .andExpect(jsonPath("$.email").value("new@example.com"))
            .andExpect(jsonPath("$.name").value("New User"))
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 이미 등록된 이메일")
    void should_ReturnBadRequest_When_EmailAlreadyExists() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setName("New User");
        
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("이미 등록된 이메일입니다."));
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이미 등록된 이메일입니다."));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 실패 (이메일 형식 오류)")
    void should_ReturnBadRequest_When_InvalidEmail() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email"); // 잘못된 이메일 형식
        request.setPassword("password123");
        request.setName("New User");
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 실패 (비밀번호 너무 짧음)")
    void should_ReturnBadRequest_When_PasswordTooShort() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("12345"); // 6자 미만
        request.setName("New User");
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    // 로그인 테스트
    
    @Test
    @DisplayName("로그인 성공")
    void should_Login_When_ValidCredentials() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        AuthResponse response = new AuthResponse(
            "jwt-token",
            "test@example.com",
            "Test User",
            "로그인 성공"
        );
        
        when(authService.login(any(LoginRequest.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.message").value("로그인 성공"));
    }
    
    @Test
    @DisplayName("로그인 실패 - 잘못된 이메일 또는 비밀번호")
    void should_ReturnUnauthorized_When_InvalidCredentials() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");
        
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다."));
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }
    
    @Test
    @DisplayName("로그인 실패 - 유효성 검사 실패 (이메일 누락)")
    void should_ReturnBadRequest_When_EmailMissing() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPassword("password123");
        // email 누락
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("로그인 실패 - 유효성 검사 실패 (비밀번호 누락)")
    void should_ReturnBadRequest_When_PasswordMissing() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        // password 누락
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}

