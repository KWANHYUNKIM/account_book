# Rendering Activity

## 개요
렌더링 활동을 최적화하여 성능을 향상시킵니다.

## 예시

```typescript
import { useState, useMemo } from 'react'

// ❌ 나쁜 예: 매번 새로운 객체 생성
function SessionCard({ session }: { session: Session }) {
  const style = {
    backgroundColor: session.color,
    padding: '20px'
  }
  
  return <div style={style}>{session.name}</div>
}

// ✅ 좋은 예: useMemo로 스타일 객체 캐싱
function SessionCard({ session }: { session: Session }) {
  const style = useMemo(() => ({
    backgroundColor: session.color,
    padding: '20px'
  }), [session.color])
  
  return <div style={style}>{session.name}</div>
}

// 또는 인라인 스타일 사용 (간단한 경우)
function SessionCard({ session }: { session: Session }) {
  return (
    <div style={{ backgroundColor: session.color, padding: '20px' }}>
      {session.name}
    </div>
  )
}

// 조건부 렌더링 최적화
function TransactionList({ transactions }: { transactions: Transaction[] }) {
  if (transactions.length === 0) {
    return <EmptyState />
  }
  
  return (
    <ul>
      {transactions.map(t => (
        <TransactionItem key={t.id} transaction={t} />
      ))}
    </ul>
  )
}
```

## 베스트 프랙티스
- 매번 생성되는 객체는 useMemo로 캐싱
- 간단한 스타일은 인라인으로 직접 작성
- 조건부 렌더링으로 불필요한 렌더링 방지

