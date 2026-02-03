# JS Cache Storage

## 개요
로컬 스토리지나 메모리 캐시를 활용하여 불필요한 요청을 줄입니다.

## 예시

```typescript
import { useState, useEffect } from 'react'

// 간단한 메모리 캐시
const cache = new Map<string, { data: any; timestamp: number }>()
const CACHE_TTL = 5 * 60 * 1000 // 5분

function useCachedData<T>(key: string, fetcher: () => Promise<T>) {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(false)
  
  useEffect(() => {
    const cached = cache.get(key)
    const now = Date.now()
    
    // 캐시가 있고 유효한 경우
    if (cached && (now - cached.timestamp) < CACHE_TTL) {
      setData(cached.data)
      return
    }
    
    // 캐시가 없거나 만료된 경우
    setLoading(true)
    fetcher().then(result => {
      cache.set(key, { data: result, timestamp: now })
      setData(result)
      setLoading(false)
    })
  }, [key])
  
  return { data, loading }
}

// 사용 예시
function SessionList() {
  const { data: sessions, loading } = useCachedData(
    'sessions',
    () => api.get('/sessions').then(res => res.data)
  )
  
  if (loading) return <div>로딩 중...</div>
  return <div>{/* sessions 렌더링 */}</div>
}
```

## 베스트 프랙티스
- 자주 변경되지 않는 데이터는 캐싱
- TTL(Time To Live) 설정
- 캐시 무효화 전략 수립

