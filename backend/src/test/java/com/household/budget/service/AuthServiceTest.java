package com.household.budget.service;

import com.household.budget.config.JwtUtil;
import com.household.budget.dto.AuthResponse;
import com.household.budget.dto.LoginRequest;
import com.household.budget.dto.RegisterRequest;
import com.household.budget.entity.User;
import com.household.budget.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthService 테스트
 * Repository, PasswordEncoder, JwtUtil을 Mock으로 처리
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private String encodedPassword;
    
    @BeforeEach
    void setUp() {
        encodedPassword = "$2a$10$encodedPasswordHash";
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword(encodedPassword);
        testUser.setName("Test User");
        testUser.setRole("USER");
    }
    
    // 회원가입 테스트
    
    @Test
    @DisplayName("회원가입 성공")
    void should_Register_When_ValidRequest() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setName("New User");
        
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setEmail("new@example.com");
        savedUser.setName("New User");
        
        when(userRepository.existsByEmail("new@example.com"))
            .thenReturn(false);
        when(passwordEncoder.encode("password123"))
            .thenReturn(encodedPassword);
        when(userRepository.save(any(User.class)))
            .thenReturn(savedUser);
        when(jwtUtil.generateToken("new@example.com", 2L))
            .thenReturn("jwt-token");
        
        // When
        AuthResponse response = authService.register(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getName()).isEqualTo("New User");
        assertThat(response.getMessage()).isEqualTo("회원가입이 완료되었습니다.");
        
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken("new@example.com", 2L);
    }
    
    @Test
    @DisplayName("회원가입 실패 - 이미 등록된 이메일")
    void should_ThrowException_When_EmailAlreadyExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setName("New User");
        
        when(userRepository.existsByEmail("existing@example.com"))
            .thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("이미 등록된 이메일입니다.");
        
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(anyString(), any());
    }
    
    // 로그인 테스트
    
    @Test
    @DisplayName("로그인 성공")
    void should_Login_When_ValidCredentials() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", encodedPassword))
            .thenReturn(true);
        when(userRepository.save(any(User.class)))
            .thenReturn(testUser);
        when(jwtUtil.generateToken("test@example.com", 1L))
            .thenReturn("jwt-token");
        
        // When
        AuthResponse response = authService.login(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Test User");
        assertThat(response.getMessage()).isEqualTo("로그인 성공");
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", encodedPassword);
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken("test@example.com", 1L);
    }
    
    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void should_ThrowException_When_EmailNotFound() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password123");
        
        when(userRepository.findByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
        
        verify(userRepository).findByEmail("notfound@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), any());
    }
    
    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void should_ThrowException_When_WrongPassword() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");
        
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", encodedPassword))
            .thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", encodedPassword);
        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(anyString(), any());
    }
    
    @Test
    @DisplayName("로그인 시 lastLoginAt 업데이트")
    void should_UpdateLastLoginAt_When_LoginSuccess() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", encodedPassword))
            .thenReturn(true);
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                assertThat(user.getLastLoginAt()).isNotNull();
                return user;
            });
        when(jwtUtil.generateToken("test@example.com", 1L))
            .thenReturn("jwt-token");
        
        // When
        authService.login(request);
        
        // Then
        verify(userRepository).save(any(User.class));
    }
    
    // 사용자 조회 테스트
    
    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void should_GetUserByEmail_When_ValidEmail() {
        // Given
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        
        // When
        User result = authService.getUserByEmail("test@example.com");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
    }
    
    @Test
    @DisplayName("이메일로 사용자 조회 실패")
    void should_ThrowException_When_UserNotFoundByEmail() {
        // Given
        when(userRepository.findByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authService.getUserByEmail("notfound@example.com"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }
    
    @Test
    @DisplayName("ID로 사용자 조회 성공")
    void should_GetUserById_When_ValidId() {
        // Given
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(testUser));
        
        // When
        User result = authService.getUserById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("ID로 사용자 조회 실패")
    void should_ThrowException_When_UserNotFoundById() {
        // Given
        when(userRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authService.getUserById(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }
}

