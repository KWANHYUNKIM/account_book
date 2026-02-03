# Async Parallel

## 개요
독립적인 비동기 작업은 병렬로 실행하여 성능을 향상시킵니다.

## 예시

```typescript
// ❌ 순차 실행 (느림)
async function loadSessionData(sessionId: number) {
  const session = await api.get(`/sessions/${sessionId}`)
  const transactions = await api.get(`/transactions/session/${sessionId}`)
  const categories = await api.get('/categories')
  const summary = await api.get(`/sessions/${sessionId}/summary`)
  
  return { session, transactions, categories, summary }
}

// ✅ 병렬 실행 (빠름)
async function loadSessionData(sessionId: number) {
  const [session, transactions, categories, summary] = await Promise.all([
    api.get(`/sessions/${sessionId}`),
    api.get(`/transactions/session/${sessionId}`),
    api.get('/categories'),
    api.get(`/sessions/${sessionId}/summary`)
  ])
  
  return { session, transactions, categories, summary }
}

// 일부만 성공해도 되는 경우
async function loadWithFallback() {
  const results = await Promise.allSettled([
    api.get('/primary-data'),
    api.get('/secondary-data'),
    api.get('/optional-data')
  ])
  
  return results.map((result, index) => 
    result.status === 'fulfilled' ? result.value : null
  )
}
```

## 베스트 프랙티스
- 독립적인 요청은 `Promise.all`로 병렬 처리
- 일부 실패해도 되는 경우 `Promise.allSettled` 사용
- 의존성이 있는 요청만 순차 처리

