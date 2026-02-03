# Test Utils Functions (3순위)

## 개요
유틸리티 함수는 **80% 커버리지**를 목표로 합니다. 재사용되는 순수 함수를 테스트합니다.

## 예시: CurrencyFormatter

```java
// domain/utils/CurrencyFormatter.java
public class CurrencyFormatter {
    
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###원");
    
    public static String format(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 null일 수 없습니다.");
        }
        return FORMAT.format(amount);
    }
    
    public static BigDecimal parse(String formattedAmount) {
        if (formattedAmount == null || formattedAmount.trim().isEmpty()) {
            throw new IllegalArgumentException("포맷된 금액은 null이거나 비어있을 수 없습니다.");
        }
        String numeric = formattedAmount.replaceAll("[^0-9]", "");
        if (numeric.isEmpty()) {
            throw new IllegalArgumentException("유효한 숫자가 없습니다.");
        }
        return new BigDecimal(numeric);
    }
    
    public static String formatWithSign(BigDecimal amount) {
        if (amount == null) {
            return "0원";
        }
        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            return "+" + format(amount);
        }
        return format(amount);
    }
}
```

## 테스트 예시

```java
// __tests__/unit/domain/utils/CurrencyFormatterTest.java
class CurrencyFormatterTest {
    
    // format 테스트
    
    @Test
    void should_Format_When_ValidAmount() {
        // Given
        BigDecimal amount = new BigDecimal("10000");
        
        // When
        String formatted = CurrencyFormatter.format(amount);
        
        // Then
        assertThat(formatted).isEqualTo("10,000원");
    }
    
    @Test
    void should_Format_When_LargeAmount() {
        // Given
        BigDecimal amount = new BigDecimal("1000000");
        
        // When
        String formatted = CurrencyFormatter.format(amount);
        
        // Then
        assertThat(formatted).isEqualTo("1,000,000원");
    }
    
    @Test
    void should_Format_When_SmallAmount() {
        // Given
        BigDecimal amount = new BigDecimal("1");
        
        // When
        String formatted = CurrencyFormatter.format(amount);
        
        // Then
        assertThat(formatted).isEqualTo("1원");
    }
    
    @Test
    void should_ThrowException_When_AmountIsNull() {
        // When & Then
        assertThatThrownBy(() -> CurrencyFormatter.format(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("금액은 null일 수 없습니다.");
    }
    
    // parse 테스트
    
    @Test
    void should_Parse_When_ValidFormat() {
        // Given
        String formatted = "10,000원";
        
        // When
        BigDecimal amount = CurrencyFormatter.parse(formatted);
        
        // Then
        assertThat(amount).isEqualByComparingTo(new BigDecimal("10000"));
    }
    
    @Test
    void should_Parse_When_WithoutWon() {
        // Given
        String formatted = "10,000";
        
        // When
        BigDecimal amount = CurrencyFormatter.parse(formatted);
        
        // Then
        assertThat(amount).isEqualByComparingTo(new BigDecimal("10000"));
    }
    
    @Test
    void should_ThrowException_When_FormattedIsNull() {
        // When & Then
        assertThatThrownBy(() -> CurrencyFormatter.parse(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("null이거나 비어있을 수 없습니다");
    }
    
    @Test
    void should_ThrowException_When_FormattedIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> CurrencyFormatter.parse(""))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void should_ThrowException_When_NoNumeric() {
        // When & Then
        assertThatThrownBy(() -> CurrencyFormatter.parse("abc"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효한 숫자가 없습니다");
    }
    
    // formatWithSign 테스트
    
    @Test
    void should_AddPlus_When_PositiveAmount() {
        // Given
        BigDecimal amount = new BigDecimal("10000");
        
        // When
        String formatted = CurrencyFormatter.formatWithSign(amount);
        
        // Then
        assertThat(formatted).isEqualTo("+10,000원");
    }
    
    @Test
    void should_NotAddPlus_When_NegativeAmount() {
        // Given
        BigDecimal amount = new BigDecimal("-10000");
        
        // When
        String formatted = CurrencyFormatter.formatWithSign(amount);
        
        // Then
        assertThat(formatted).isEqualTo("-10,000원");
    }
    
    @Test
    void should_ReturnZero_When_AmountIsNull() {
        // When
        String formatted = CurrencyFormatter.formatWithSign(null);
        
        // Then
        assertThat(formatted).isEqualTo("0원");
    }
    
    @Test
    void should_NotAddPlus_When_Zero() {
        // Given
        BigDecimal amount = BigDecimal.ZERO;
        
        // When
        String formatted = CurrencyFormatter.formatWithSign(amount);
        
        // Then
        assertThat(formatted).isEqualTo("0원");
    }
}
```

## 베스트 프랙티스
- **입출력 테스트**: 다양한 입력값에 대한 출력 검증
- **경계값 테스트**: 최소값, 최대값, 0 등
- **Null 안전성**: null 입력에 대한 처리
- **에러 케이스**: 잘못된 입력에 대한 예외
- **순수 함수**: 부작용 없는 함수만 테스트

