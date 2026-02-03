# Rendering Conditional Render

## 개요
조건부 렌더링을 효율적으로 구현합니다.

## 예시

```typescript
// ❌ 나쁜 예: 불필요한 렌더링
function TransactionList({ transactions }: { transactions: Transaction[] }) {
  return (
    <div>
      {transactions.length > 0 && (
        <div>
          {transactions.map(t => (
            <TransactionItem key={t.id} transaction={t} />
          ))}
        </div>
      )}
      {transactions.length === 0 && <EmptyState />}
    </div>
  )
}

// ✅ 좋은 예: Early return
function TransactionList({ transactions }: { transactions: Transaction[] }) {
  if (transactions.length === 0) {
    return <EmptyState />
  }
  
  return (
    <div>
      {transactions.map(t => (
        <TransactionItem key={t.id} transaction={t} />
      ))}
    </div>
  )
}

// 삼항 연산자 (간단한 경우)
function SessionStatus({ isActive }: { isActive: boolean }) {
  return isActive ? <ActiveBadge /> : <InactiveBadge />
}

// 복잡한 조건
function SessionCard({ session }: { session: Session }) {
  if (!session) return null
  if (!session.isActive) return <InactiveCard session={session} />
  
  return <ActiveCard session={session} />
}
```

## 베스트 프랙티스
- Early return으로 불필요한 렌더링 방지
- 간단한 조건은 삼항 연산자
- 복잡한 조건은 if-else 사용

