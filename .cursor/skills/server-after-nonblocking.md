# Server After Nonblocking

## 개요
서버에서 논블로킹 작업을 먼저 수행하여 응답 시간을 단축합니다.

## 예시

```typescript
// Next.js App Router 예시
async function SessionPage({ params }: { params: { id: string } }) {
  // 블로킹 작업: 중요한 데이터 먼저
  const session = await fetchSession(params.id)
  
  // 논블로킹: 덜 중요한 데이터는 Promise로 시작
  const transactionsPromise = fetchTransactions(params.id)
  const analyticsPromise = fetchAnalytics(params.id)
  
  // 중요한 데이터 먼저 렌더링
  return (
    <div>
      <SessionHeader session={session} />
      
      {/* 논블로킹 데이터는 Suspense로 처리 */}
      <Suspense fallback={<TransactionsLoading />}>
        <TransactionsList promise={transactionsPromise} />
      </Suspense>
      
      <Suspense fallback={<AnalyticsLoading />}>
        <Analytics promise={analyticsPromise} />
      </Suspense>
    </div>
  )
}

// Promise를 받는 컴포넌트
async function TransactionsList({ promise }: { promise: Promise<Transaction[]> }) {
  const transactions = await promise
  return (
    <ul>
      {transactions.map(t => (
        <li key={t.id}>{t.description}</li>
      ))}
    </ul>
  )
}
```

## 베스트 프랙티스
- 중요한 데이터는 즉시 await
- 덜 중요한 데이터는 Promise로 전달
- Suspense로 로딩 상태 처리

