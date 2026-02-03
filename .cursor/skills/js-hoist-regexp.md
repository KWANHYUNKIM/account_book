# JS Hoist RegExp

## 개요
정규표현식을 컴포넌트 외부로 끌어올려 반복 생성을 방지합니다.

## 예시

```typescript
// ❌ 나쁜 예: 매번 정규표현식 생성
function validateEmail(email: string) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

function EmailInput() {
  const [email, setEmail] = useState('')
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    // 매번 새로운 정규표현식 생성
    const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
    setEmail(value)
  }
}

// ✅ 좋은 예: 정규표현식 호이스팅
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const PHONE_REGEX = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/
const CURRENCY_REGEX = /^[0-9,]+$/

function validateEmail(email: string) {
  return EMAIL_REGEX.test(email)
}

function EmailInput() {
  const [email, setEmail] = useState('')
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    const isValid = EMAIL_REGEX.test(value) // 재사용
    setEmail(value)
  }
}

// 유틸리티 파일로 분리
// utils/validation.ts
export const REGEX = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PHONE: /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/,
  CURRENCY: /^[0-9,]+$/
} as const
```

## 베스트 프랙티스
- 정규표현식은 컴포넌트 외부에 상수로 정의
- 유틸리티 파일로 분리하여 재사용
- 복잡한 패턴은 주석 추가

