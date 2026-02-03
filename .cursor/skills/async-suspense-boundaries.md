# Async Suspense Boundaries

## 개요
Suspense를 사용하여 비동기 데이터 로딩을 우아하게 처리합니다.

## 예시

```typescript
import { Suspense } from 'react'

// 비동기 컴포넌트
async function TransactionsList({ sessionId }: { sessionId: number }) {
  const transactions = await api.get(`/transactions/session/${sessionId}`)
  return <div>{/* transactions 렌더링 */}</div>
}

// Suspense로 감싸기
export default function SessionPage({ sessionId }: { sessionId: number }) {
  return (
    <div>
      <SessionHeader sessionId={sessionId} />
      
      <Suspense fallback={<TransactionsSkeleton />}>
        <TransactionsList sessionId={sessionId} />
      </Suspense>
      
      <Suspense fallback={<SummarySkeleton />}>
        <Summary sessionId={sessionId} />
      </Suspense>
    </div>
  )
}

// 스켈레톤 컴포넌트
function TransactionsSkeleton() {
  return (
    <div className="animate-pulse">
      <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
      <div className="h-4 bg-gray-200 rounded w-1/2"></div>
    </div>
  )
}
```

## 베스트 프랙티스
- 각 비동기 섹션을 개별 Suspense로 감싸기
- 의미 있는 fallback UI 제공
- 에러 바운더리와 함께 사용

