# Client SWR Deduplication

## 개요
SWR을 사용하여 중복 요청을 자동으로 제거합니다.

## 예시

```typescript
import useSWR from 'swr'

const fetcher = (url: string) => api.get(url).then(res => res.data)

// 같은 키로 여러 컴포넌트에서 호출해도 한 번만 요청
function SessionHeader({ sessionId }: { sessionId: number }) {
  const { data: session } = useSWR(`/sessions/${sessionId}`, fetcher)
  return <h1>{session?.name}</h1>
}

function SessionSummary({ sessionId }: { sessionId: number }) {
  // 같은 키이므로 캐시된 데이터 사용 (중복 요청 없음)
  const { data: session } = useSWR(`/sessions/${sessionId}`, fetcher)
  return <div>잔액: {session?.balance}</div>
}

// 커스텀 캐시 키
function useSession(sessionId: number) {
  return useSWR(
    sessionId ? `session-${sessionId}` : null,
    () => api.get(`/sessions/${sessionId}`).then(res => res.data),
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: true,
      dedupingInterval: 2000 // 2초 내 중복 요청 방지
    }
  )
}
```

## 베스트 프랙티스
- 동일한 데이터는 동일한 키 사용
- dedupingInterval로 중복 요청 방지
- 캐시 전략 적절히 설정

