# Rendering Hoist JSX

## 개요
JSX를 컴포넌트 외부로 끌어올려 불필요한 재생성을 방지합니다.

## 예시

```typescript
// ❌ 나쁜 예: 매번 새로운 JSX 생성
function TransactionList({ transactions }: { transactions: Transaction[] }) {
  const emptyState = <div>거래 내역이 없습니다.</div>
  
  return transactions.length === 0 ? emptyState : (
    <div>
      {transactions.map(t => <TransactionItem key={t.id} transaction={t} />)}
    </div>
  )
}

// ✅ 좋은 예: JSX 호이스팅
const EMPTY_STATE = <div>거래 내역이 없습니다.</div>

function TransactionList({ transactions }: { transactions: Transaction[] }) {
  if (transactions.length === 0) {
    return EMPTY_STATE
  }
  
  return (
    <div>
      {transactions.map(t => <TransactionItem key={t.id} transaction={t} />)}
    </div>
  )
}

// 또는 컴포넌트로 분리
const EmptyState = () => <div>거래 내역이 없습니다.</div>

function TransactionList({ transactions }: { transactions: Transaction[] }) {
  if (transactions.length === 0) {
    return <EmptyState />
  }
  
  return (
    <div>
      {transactions.map(t => <TransactionItem key={t.id} transaction={t} />)}
    </div>
  )
}
```

## 베스트 프랙티스
- 정적 JSX는 컴포넌트 외부에 정의
- 재사용되는 UI는 컴포넌트로 분리
- 조건부 렌더링은 Early return 사용

