# Rerender Derived State

## 개요
파생 상태를 useMemo로 계산하여 불필요한 재계산을 방지합니다.

## 예시

```typescript
import { useMemo } from 'react'

// ❌ 나쁜 예: 매번 계산
function TransactionSummary({ transactions }: { transactions: Transaction[] }) {
  const totalIncome = transactions
    .filter(t => t.type === 'INCOME')
    .reduce((sum, t) => sum + t.amount, 0)
  
  const totalExpense = transactions
    .filter(t => t.type === 'EXPENSE')
    .reduce((sum, t) => sum + t.amount, 0)
  
  return (
    <div>
      <div>수입: {totalIncome}</div>
      <div>지출: {totalExpense}</div>
    </div>
  )
}

// ✅ 좋은 예: useMemo로 파생 상태 계산
function TransactionSummary({ transactions }: { transactions: Transaction[] }) {
  const { totalIncome, totalExpense, balance } = useMemo(() => {
    return transactions.reduce((acc, t) => {
      if (t.type === 'INCOME') {
        acc.totalIncome += t.amount
      } else {
        acc.totalExpense += t.amount
      }
      acc.balance = acc.totalIncome - acc.totalExpense
      return acc
    }, { totalIncome: 0, totalExpense: 0, balance: 0 })
  }, [transactions])
  
  return (
    <div>
      <div>수입: {totalIncome}</div>
      <div>지출: {totalExpense}</div>
      <div>잔액: {balance}</div>
    </div>
  )
}

// 필터링된 리스트도 파생 상태
function FilteredTransactionList({ 
  transactions, 
  filter 
}: { 
  transactions: Transaction[]
  filter: 'all' | 'income' | 'expense'
}) {
  const filtered = useMemo(() => {
    if (filter === 'all') return transactions
    return transactions.filter(t => t.type === filter.toUpperCase())
  }, [transactions, filter])
  
  return (
    <div>
      {filtered.map(t => (
        <TransactionItem key={t.id} transaction={t} />
      ))}
    </div>
  )
}
```

## 베스트 프랙티스
- 계산 비용이 큰 값은 useMemo 사용
- 필터링/정렬 결과도 useMemo로 캐싱
- 의존성 배열 정확히 지정

