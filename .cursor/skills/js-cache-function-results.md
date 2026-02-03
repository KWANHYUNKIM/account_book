# JS Cache Function Results

## 개요
비용이 큰 함수의 결과를 캐싱하여 성능을 향상시킵니다.

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
  
  return <div>{/* 렌더링 */}</div>
}

// ✅ 좋은 예: useMemo로 캐싱
function TransactionSummary({ transactions }: { transactions: Transaction[] }) {
  const { totalIncome, totalExpense } = useMemo(() => {
    return transactions.reduce((acc, t) => {
      if (t.type === 'INCOME') {
        acc.totalIncome += t.amount
      } else {
        acc.totalExpense += t.amount
      }
      return acc
    }, { totalIncome: 0, totalExpense: 0 })
  }, [transactions])
  
  return <div>{/* 렌더링 */}</div>
}

// 복잡한 계산 함수
function useTransactionStats(transactions: Transaction[]) {
  return useMemo(() => {
    // 복잡한 통계 계산
    const stats = calculateStats(transactions)
    return stats
  }, [transactions])
}
```

## 베스트 프랙티스
- 비용이 큰 계산은 useMemo로 메모이제이션
- 의존성 배열 정확히 지정
- 단순한 계산은 메모이제이션 불필요

