# JS toSorted Immutable

## 개요
불변성을 유지하면서 배열을 정렬합니다.

## 예시

```typescript
// ❌ 나쁜 예: 원본 배열 변경
function sortTransactions(transactions: Transaction[]) {
  return transactions.sort((a, b) => 
    new Date(b.date).getTime() - new Date(a.date).getTime()
  )
}

// ✅ 좋은 예: toSorted 사용 (ES2023)
function sortTransactions(transactions: Transaction[]) {
  return transactions.toSorted((a, b) => 
    new Date(b.date).getTime() - new Date(a.date).getTime()
  )
}

// 또는 스프레드 연산자 사용
function sortTransactions(transactions: Transaction[]) {
  return [...transactions].sort((a, b) => 
    new Date(b.date).getTime() - new Date(a.date).getTime()
  )
}

// React에서 사용
function TransactionList({ transactions }: { transactions: Transaction[] }) {
  const sortedTransactions = useMemo(() => {
    return transactions.toSorted((a, b) => 
      new Date(b.transactionDate).getTime() - new Date(a.transactionDate).getTime()
    )
  }, [transactions])
  
  return (
    <div>
      {sortedTransactions.map(t => (
        <TransactionItem key={t.id} transaction={t} />
      ))}
    </div>
  )
}
```

## 베스트 프랙티스
- 원본 배열 변경 방지
- toSorted 또는 스프레드 연산자 사용
- useMemo로 정렬 결과 캐싱

