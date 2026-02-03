# Test Mocking Dependencies

## 개요
의존성을 모킹하여 단위 테스트를 격리합니다. Mockito를 사용합니다.

## 예시

```java
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    void should_Login_When_ValidCredentials() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "$2a$10$...";
        
        User user = User.builder()
            .id(1L)
            .email(email)
            .password(encodedPassword)
            .build();
            
        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword))
            .thenReturn(true);
        when(jwtUtil.generateToken(user))
            .thenReturn("jwt-token");
        
        // When
        AuthResponse response = authService.login(email, password);
        
        // Then
        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtUtil).generateToken(user);
        verify(userRepository).save(user); // lastLoginAt 업데이트
    }
    
    @Test
    void should_ThrowException_When_InvalidPassword() {
        // Given
        String email = "test@example.com";
        String password = "wrong-password";
        
        User user = User.builder()
            .email(email)
            .password("$2a$10$...")
            .build();
            
        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword()))
            .thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authService.login(email, password))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("Invalid password");
            
        verify(jwtUtil, never()).generateToken(any());
    }
    
    @Test
    void should_Register_When_NewUser() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .email("new@example.com")
            .password("password123")
            .name("New User")
            .build();
            
        when(userRepository.existsByEmail(request.getEmail()))
            .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword()))
            .thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
        
        // When
        AuthResponse response = authService.register(request);
        
        // Then
        assertThat(response).isNotNull();
        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void should_ThrowException_When_EmailAlreadyExists() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .email("existing@example.com")
            .password("password123")
            .build();
            
        when(userRepository.existsByEmail(request.getEmail()))
            .thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email already exists");
            
        verify(userRepository, never()).save(any());
    }
    
    // ArgumentMatcher 사용
    @Test
    void should_FindUser_When_ByEmailPattern() {
        // Given
        when(userRepository.findByEmail(anyString()))
            .thenReturn(Optional.empty());
        when(userRepository.findByEmail(startsWith("test")))
            .thenReturn(Optional.of(User.builder().email("test@example.com").build()));
        
        // When
        Optional<User> user = authService.findUserByEmail("test@example.com");
        
        // Then
        assertThat(user).isPresent();
    }
    
    // ArgumentCaptor 사용
    @Test
    void should_SaveUser_With_CorrectData() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .email("test@example.com")
            .password("password123")
            .name("Test User")
            .build();
            
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        authService.register(request);
        
        // Then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getName()).isEqualTo("Test User");
    }
}
```

## 베스트 프랙티스
- `@Mock`으로 의존성 모킹
- `when().thenReturn()`으로 동작 정의
- `verify()`로 메서드 호출 검증
- `ArgumentMatcher`로 유연한 매칭
- `ArgumentCaptor`로 전달된 인자 검증
- `never()`, `times(n)`으로 호출 횟수 검증

