# Server Cache LRU

## 개요
LRU 캐시를 사용하여 서버 응답을 캐싱합니다.

## 예시

```typescript
import { LRUCache } from 'lru-cache'

// LRU 캐시 생성
const cache = new LRUCache<string, any>({
  max: 100, // 최대 100개 항목
  ttl: 1000 * 60 * 5, // 5분 TTL
})

// Next.js API Route
export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url)
  const sessionId = searchParams.get('sessionId')
  
  if (!sessionId) {
    return NextResponse.json({ error: 'Session ID required' }, { status: 400 })
  }
  
  const cacheKey = `session-${sessionId}`
  
  // 캐시 확인
  const cached = cache.get(cacheKey)
  if (cached) {
    return NextResponse.json(cached)
  }
  
  // 캐시 미스: 데이터 가져오기
  const session = await fetchSessionFromDB(sessionId)
  
  // 캐시에 저장
  cache.set(cacheKey, session)
  
  return NextResponse.json(session)
}

// React Server Component에서 사용
async function getCachedSession(sessionId: number) {
  const cacheKey = `session-${sessionId}`
  const cached = cache.get(cacheKey)
  
  if (cached) {
    return cached
  }
  
  const session = await fetchSession(sessionId)
  cache.set(cacheKey, session)
  return session
}
```

## 베스트 프랙티스
- 자주 조회되는 데이터는 LRU 캐시 사용
- 적절한 TTL 설정
- 메모리 사용량 제한 (max 옵션)

