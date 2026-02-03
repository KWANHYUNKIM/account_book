# Async Defer Await

## 개요
중요하지 않은 데이터는 지연 로딩하여 초기 로딩 시간을 단축합니다.

## 예시

```typescript
import { defer } from 'react-router-dom'

// Next.js App Router 예시
async function loadSessionData(sessionId: string) {
  // 중요한 데이터는 즉시 로드
  const session = await fetchSession(sessionId)
  
  // 덜 중요한 데이터는 지연 로드
  const transactions = fetchTransactions(sessionId)
  const analytics = fetchAnalytics(sessionId)
  
  return {
    session,
    transactions: await transactions,
    analytics: await analytics
  }
}

// React 컴포넌트에서 사용
export default async function SessionPage({ params }: { params: { id: string } }) {
  const { session, transactions, analytics } = await loadSessionData(params.id)
  
  return (
    <div>
      <SessionHeader session={session} />
      <Suspense fallback={<TransactionsLoading />}>
        <TransactionsList transactions={transactions} />
      </Suspense>
      <Suspense fallback={<AnalyticsLoading />}>
        <Analytics data={analytics} />
      </Suspense>
    </div>
  )
}
```

## 베스트 프랙티스
- 중요한 데이터는 즉시 로드
- 덜 중요한 데이터는 지연 로드
- Suspense로 로딩 상태 처리

