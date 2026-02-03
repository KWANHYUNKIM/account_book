# Rerender Lazy State Init

## 개요
초기 상태 계산이 비용이 클 때 지연 초기화를 사용합니다.

## 예시

```typescript
import { useState } from 'react'

// ❌ 나쁜 예: 매번 초기값 계산
function SessionList({ sessions }: { sessions: Session[] }) {
  const [selectedIds, setSelectedIds] = useState(
    sessions.filter(s => s.isActive).map(s => s.id) // 매 렌더링마다 실행
  )
}

// ✅ 좋은 예: 지연 초기화
function SessionList({ sessions }: { sessions: Session[] }) {
  const [selectedIds, setSelectedIds] = useState(() => {
    // 초기 렌더링에만 실행
    return sessions.filter(s => s.isActive).map(s => s.id)
  })
}

// 복잡한 초기 계산
function TransactionCalculator({ transactions }: { transactions: Transaction[] }) {
  const [stats, setStats] = useState(() => {
    // 비용이 큰 계산을 초기화 시에만 수행
    return transactions.reduce((acc, t) => {
      // 복잡한 계산...
      return acc
    }, { total: 0, count: 0 })
  })
}

// localStorage에서 초기값 가져오기
function usePersistedState<T>(key: string, defaultValue: T) {
  const [state, setState] = useState<T>(() => {
    // 초기화 시에만 localStorage 읽기
    if (typeof window === 'undefined') return defaultValue
    const stored = localStorage.getItem(key)
    return stored ? JSON.parse(stored) : defaultValue
  })
  
  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(state))
  }, [key, state])
  
  return [state, setState] as const
}
```

## 베스트 프랙티스
- 비용이 큰 초기 계산은 함수로 지연 초기화
- localStorage 읽기는 초기화 시에만 수행
- 함수형 초기화는 첫 렌더링에만 실행

