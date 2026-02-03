# Server Cache React

## 개요
React의 캐싱 API를 사용하여 서버 컴포넌트 결과를 캐싱합니다.

## 예시

```typescript
import { cache } from 'react'

// React의 cache 함수로 중복 요청 제거
const getSession = cache(async (sessionId: number) => {
  const response = await api.get(`/sessions/${sessionId}`)
  return response.data
})

// Next.js Server Component
export default async function SessionPage({ params }: { params: { id: string } }) {
  const sessionId = parseInt(params.id)
  
  // 같은 요청 내에서 중복 호출해도 한 번만 실행
  const session = await getSession(sessionId)
  
  return (
    <div>
      <h1>{session.name}</h1>
      <SessionDetails sessionId={sessionId} />
    </div>
  )
}

async function SessionDetails({ sessionId }: { sessionId: number }) {
  // 같은 요청 내에서 getSession 재호출해도 캐시된 결과 사용
  const session = await getSession(sessionId)
  return <div>{session.description}</div>
}

// unstable_cache로 더 긴 캐싱
import { unstable_cache } from 'next/cache'

const getCachedSession = unstable_cache(
  async (sessionId: number) => {
    return await fetchSession(sessionId)
  },
  ['session'], // 캐시 키 prefix
  {
    revalidate: 60, // 60초마다 재검증
    tags: ['session'] // 태그로 캐시 무효화
  }
)
```

## 베스트 프랙티스
- 같은 요청 내 중복 호출 방지에 cache 사용
- 더 긴 캐싱은 unstable_cache 사용
- 적절한 revalidate 시간 설정

