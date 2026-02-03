# JS Cache Property Access

## 개요
반복적으로 접근하는 객체 속성을 변수에 캐싱합니다.

## 예시

```typescript
// ❌ 나쁜 예: 반복적인 속성 접근
function renderTransactions(transactions: Transaction[]) {
  return transactions.map(t => (
    <div key={t.id}>
      <span>{t.category.name}</span>
      <span>{t.category.color}</span>
      <span>{t.category.icon}</span>
    </div>
  ))
}

// ✅ 좋은 예: 속성 캐싱
function renderTransactions(transactions: Transaction[]) {
  return transactions.map(t => {
    const category = t.category // 한 번만 접근
    return (
      <div key={t.id}>
        <span>{category.name}</span>
        <span>{category.color}</span>
        <span>{category.icon}</span>
      </div>
    )
  })
}

// 중첩된 속성 접근
function formatTransaction(transaction: Transaction) {
  // ❌ 나쁜 예
  const amount = transaction.session.user.preferences.currency.format(transaction.amount)
  
  // ✅ 좋은 예
  const { session } = transaction
  const { user } = session
  const { preferences } = user
  const { currency } = preferences
  const amount = currency.format(transaction.amount)
}
```

## 베스트 프랙티스
- 반복 접근하는 속성은 변수에 저장
- 중첩된 속성은 단계별로 캐싱
- 루프 내에서 특히 중요

