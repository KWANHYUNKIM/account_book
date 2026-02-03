# Rendering Hydration No Flicker

## 개요
하이드레이션 불일치를 방지하여 깜빡임을 제거합니다.

## 예시

```typescript
import { useState, useEffect } from 'react'

// ❌ 나쁜 예: 클라이언트에서만 다른 내용 렌더링
function ThemeToggle() {
  const [theme, setTheme] = useState('light')
  
  useEffect(() => {
    const saved = localStorage.getItem('theme')
    if (saved) setTheme(saved)
  }, [])
  
  // 서버와 클라이언트 렌더링이 다를 수 있음
  return <div>현재 테마: {theme}</div>
}

// ✅ 좋은 예: 하이드레이션 후에만 다르게 렌더링
function ThemeToggle() {
  const [theme, setTheme] = useState('light')
  const [mounted, setMounted] = useState(false)
  
  useEffect(() => {
    setMounted(true)
    const saved = localStorage.getItem('theme')
    if (saved) setTheme(saved)
  }, [])
  
  // 서버와 클라이언트가 동일하게 렌더링
  if (!mounted) {
    return <div>현재 테마: light</div>
  }
  
  return <div>현재 테마: {theme}</div>
}

// 또는 suppressHydrationWarning 사용 (주의해서 사용)
function ClientOnly({ children }: { children: React.ReactNode }) {
  const [mounted, setMounted] = useState(false)
  
  useEffect(() => {
    setMounted(true)
  }, [])
  
  if (!mounted) return null
  
  return <div suppressHydrationWarning>{children}</div>
}
```

## 베스트 프랙티스
- 서버와 클라이언트 렌더링 일치 유지
- mounted 상태로 하이드레이션 후에만 변경
- suppressHydrationWarning은 신중하게 사용

