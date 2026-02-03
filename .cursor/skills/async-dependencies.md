# Async Dependencies

## 개요
비동기 작업의 의존성을 올바르게 관리합니다.

## 예시

```typescript
import { useEffect, useState } from 'react'

function TransactionList({ sessionId }: { sessionId: number }) {
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    let cancelled = false

    async function loadTransactions() {
      setLoading(true)
      try {
        const data = await api.get(`/transactions/session/${sessionId}`)
        if (!cancelled) {
          setTransactions(data)
        }
      } catch (error) {
        if (!cancelled) {
          console.error('Failed to load transactions:', error)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadTransactions()

    // cleanup: 컴포넌트 언마운트 시 취소
    return () => {
      cancelled = true
    }
  }, [sessionId]) // sessionId가 변경될 때만 재실행

  if (loading) return <div>로딩 중...</div>
  return <div>{/* transactions 렌더링 */}</div>
}
```

## 베스트 프랙티스
- cleanup 함수로 메모리 누수 방지
- 의존성 배열에 필요한 값만 포함
- 취소 플래그로 경쟁 조건 방지

