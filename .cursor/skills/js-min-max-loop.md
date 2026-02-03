# JS Min Max Loop

## 개요
최소/최대값을 찾을 때 루프를 효율적으로 작성합니다.

## 예시

```typescript
// ❌ 나쁜 예: 여러 번 반복
function getTransactionStats(transactions: Transaction[]) {
  const amounts = transactions.map(t => t.amount)
  const max = Math.max(...amounts)
  const min = Math.min(...amounts)
  return { max, min }
}

// ✅ 좋은 예: 한 번만 반복
function getTransactionStats(transactions: Transaction[]) {
  if (transactions.length === 0) {
    return { max: 0, min: 0 }
  }
  
  return transactions.reduce((acc, t) => {
    acc.max = Math.max(acc.max, t.amount)
    acc.min = Math.min(acc.min, t.amount)
    return acc
  }, { max: -Infinity, min: Infinity })
}

// React에서 사용
function TransactionSummary({ transactions }: { transactions: Transaction[] }) {
  const { max, min, total } = useMemo(() => {
    if (transactions.length === 0) {
      return { max: 0, min: 0, total: 0 }
    }
    
    return transactions.reduce((acc, t) => {
      acc.max = Math.max(acc.max, t.amount)
      acc.min = Math.min(acc.min, t.amount)
      acc.total += t.amount
      return acc
    }, { max: -Infinity, min: Infinity, total: 0 })
  }, [transactions])
  
  return (
    <div>
      <div>최대: {max}</div>
      <div>최소: {min}</div>
      <div>합계: {total}</div>
    </div>
  )
}
```

## 베스트 프랙티스
- 한 번의 루프로 최소/최대값 계산
- 빈 배열 체크 먼저 수행
- useMemo로 결과 캐싱

