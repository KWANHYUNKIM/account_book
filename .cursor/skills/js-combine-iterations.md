# JS Combine Iterations

## 개요
여러 번의 반복을 하나로 합쳐서 성능을 향상시킵니다.

## 예시

```typescript
// ❌ 나쁜 예: 여러 번 반복
function processTransactions(transactions: Transaction[]) {
  const income = transactions.filter(t => t.type === 'INCOME')
  const expense = transactions.filter(t => t.type === 'EXPENSE')
  const byCategory = transactions.reduce((acc, t) => {
    acc[t.categoryId] = (acc[t.categoryId] || 0) + t.amount
    return acc
  }, {})
}

// ✅ 좋은 예: 한 번만 반복
function processTransactions(transactions: Transaction[]) {
  const result = transactions.reduce((acc, t) => {
    // 수입/지출 분류
    if (t.type === 'INCOME') {
      acc.income.push(t)
    } else {
      acc.expense.push(t)
    }
    
    // 카테고리별 집계
    acc.byCategory[t.categoryId] = (acc.byCategory[t.categoryId] || 0) + t.amount
    
    return acc
  }, {
    income: [] as Transaction[],
    expense: [] as Transaction[],
    byCategory: {} as Record<number, number>
  })
  
  return result
}

// React 컴포넌트에서 사용
function TransactionSummary({ transactions }: { transactions: Transaction[] }) {
  const stats = useMemo(() => {
    return transactions.reduce((acc, t) => {
      if (t.type === 'INCOME') {
        acc.totalIncome += t.amount
        acc.incomeCount++
      } else {
        acc.totalExpense += t.amount
        acc.expenseCount++
      }
      return acc
    }, { totalIncome: 0, totalExpense: 0, incomeCount: 0, expenseCount: 0 })
  }, [transactions])
  
  return <div>{/* stats 렌더링 */}</div>
}
```

## 베스트 프랙티스
- 여러 번의 filter/map을 하나의 reduce로 통합
- 루프 내에서 여러 작업 수행
- useMemo로 결과 캐싱

