# Rerender Dependencies

## 개요
의존성 배열을 정확히 지정하여 불필요한 리렌더링을 방지합니다.

## 예시

```typescript
import { useEffect, useMemo, useCallback } from 'react'

// ❌ 나쁜 예: 의존성 누락
function SessionDetail({ sessionId }: { sessionId: number }) {
  const [session, setSession] = useState(null)
  
  useEffect(() => {
    api.get(`/sessions/${sessionId}`).then(setSession)
  }, []) // sessionId 의존성 누락!
  
  return <div>{session?.name}</div>
}

// ✅ 좋은 예: 정확한 의존성
function SessionDetail({ sessionId }: { sessionId: number }) {
  const [session, setSession] = useState(null)
  
  useEffect(() => {
    api.get(`/sessions/${sessionId}`).then(setSession)
  }, [sessionId]) // 의존성 명시
  
  return <div>{session?.name}</div>
}

// useCallback 의존성
function TransactionForm({ onSubmit }: { onSubmit: (data: any) => void }) {
  const handleSubmit = useCallback((e: React.FormEvent) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    onSubmit(Object.fromEntries(formData))
  }, [onSubmit]) // onSubmit 의존성
  
  return <form onSubmit={handleSubmit}>...</form>
}

// useMemo 의존성
function TransactionSummary({ transactions }: { transactions: Transaction[] }) {
  const stats = useMemo(() => {
    return calculateStats(transactions)
  }, [transactions]) // transactions 의존성
  
  return <div>{stats.total}</div>
}
```

## 베스트 프랙티스
- 모든 의존성을 배열에 포함
- ESLint의 exhaustive-deps 규칙 사용
- 함수는 useCallback으로 메모이제이션

