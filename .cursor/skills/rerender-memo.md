# Rerender Memo

## 개요
React.memo로 불필요한 리렌더링을 방지합니다.

## 예시

```typescript
import { memo } from 'react'

// ❌ 나쁜 예: props가 변경되지 않아도 리렌더링
function TransactionItem({ transaction }: { transaction: Transaction }) {
  return (
    <div>
      <span>{transaction.description}</span>
      <span>{transaction.amount}</span>
    </div>
  )
}

// ✅ 좋은 예: React.memo로 메모이제이션
const TransactionItem = memo(function TransactionItem({ 
  transaction 
}: { 
  transaction: Transaction 
}) {
  return (
    <div>
      <span>{transaction.description}</span>
      <span>{transaction.amount}</span>
    </div>
  )
})

// 커스텀 비교 함수
const SessionCard = memo(function SessionCard({ 
  session 
}: { 
  session: Session 
}) {
  return <div>{session.name}</div>
}, (prevProps, nextProps) => {
  // id만 비교하여 리렌더링 최적화
  return prevProps.session.id === nextProps.session.id &&
         prevProps.session.name === nextProps.session.name
})

// 사용 예시
function TransactionList({ transactions }: { transactions: Transaction[] }) {
  return (
    <div>
      {transactions.map(t => (
        <TransactionItem key={t.id} transaction={t} />
      ))}
    </div>
  )
}
```

## 베스트 프랙티스
- 자주 리렌더링되는 리스트 아이템은 memo 사용
- props가 자주 변경되지 않는 컴포넌트에 적용
- 커스텀 비교 함수로 세밀한 제어

