# JS Length Check First

## 개요
배열 길이를 먼저 확인하여 불필요한 반복을 방지합니다.

## 예시

```typescript
// ❌ 나쁜 예: 빈 배열도 반복
function renderTransactions(transactions: Transaction[]) {
  return transactions.map(t => <TransactionItem key={t.id} transaction={t} />)
}

// ✅ 좋은 예: 길이 체크 먼저
function renderTransactions(transactions: Transaction[]) {
  if (transactions.length === 0) {
    return <div>거래 내역이 없습니다.</div>
  }
  
  return transactions.map(t => <TransactionItem key={t.id} transaction={t} />)
}

// 조건부 렌더링
function SessionList({ sessions }: { sessions: Session[] }) {
  if (!sessions || sessions.length === 0) {
    return <EmptyState />
  }
  
  if (sessions.length === 1) {
    return <SingleSession session={sessions[0]} />
  }
  
  return (
    <div>
      {sessions.map(session => (
        <SessionCard key={session.id} session={session} />
      ))}
    </div>
  )
}

// 배열 조작 전 체크
function processTransactions(transactions: Transaction[]) {
  if (transactions.length === 0) return { total: 0, count: 0 }
  
  return transactions.reduce((acc, t) => {
    acc.total += t.amount
    acc.count++
    return acc
  }, { total: 0, count: 0 })
}
```

## 베스트 프랙티스
- 빈 배열 체크를 먼저 수행
- 조건부 렌더링으로 불필요한 처리 방지
- null/undefined 체크도 함께 수행

