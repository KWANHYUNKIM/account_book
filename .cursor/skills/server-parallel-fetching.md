# Server Parallel Fetching

## 개요
서버에서 독립적인 데이터를 병렬로 가져옵니다.

## 예시

```typescript
// Next.js Server Component
async function SessionPage({ params }: { params: { id: string } } ) {
  const sessionId = parseInt(params.id)
  
  // ❌ 나쁜 예: 순차 실행
  // const session = await fetchSession(sessionId)
  // const transactions = await fetchTransactions(sessionId)
  // const categories = await fetchCategories()
  
  // ✅ 좋은 예: 병렬 실행
  const [session, transactions, categories] = await Promise.all([
    fetchSession(sessionId),
    fetchTransactions(sessionId),
    fetchCategories()
  ])
  
  return (
    <div>
      <SessionHeader session={session} />
      <TransactionList transactions={transactions} categories={categories} />
    </div>
  )
}

// 일부만 성공해도 되는 경우
async function DashboardPage() {
  const results = await Promise.allSettled([
    fetchRecentTransactions(),
    fetchAnalytics(),
    fetchRecommendations()
  ])
  
  const [transactions, analytics, recommendations] = results.map(result =>
    result.status === 'fulfilled' ? result.value : null
  )
  
  return (
    <div>
      {transactions && <TransactionList transactions={transactions} />}
      {analytics && <Analytics data={analytics} />}
      {recommendations && <Recommendations items={recommendations} />}
    </div>
  )
}
```

## 베스트 프랙티스
- 독립적인 요청은 Promise.all로 병렬 처리
- 일부 실패해도 되는 경우 Promise.allSettled 사용
- 의존성이 있는 요청만 순차 처리

