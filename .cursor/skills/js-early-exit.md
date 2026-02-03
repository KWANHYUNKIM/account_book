# JS Early Exit

## 개요
조건을 만족하지 않으면 조기에 반환하여 불필요한 처리를 방지합니다.

## 예시

```typescript
// ❌ 나쁜 예: 중첩된 조건문
function processTransaction(transaction: Transaction) {
  if (transaction) {
    if (transaction.amount > 0) {
      if (transaction.type === 'INCOME') {
        // 처리 로직
      }
    }
  }
}

// ✅ 좋은 예: Early exit
function processTransaction(transaction: Transaction) {
  if (!transaction) return
  if (transaction.amount <= 0) return
  if (transaction.type !== 'INCOME') return
  
  // 처리 로직
}

// 배열 처리
function findActiveSession(sessions: Session[]) {
  // ❌ 나쁜 예
  let activeSession = null
  for (const session of sessions) {
    if (session.isActive) {
      activeSession = session
      break
    }
  }
  
  // ✅ 좋은 예
  return sessions.find(session => session.isActive)
}

// React 컴포넌트
function SessionCard({ session }: { session?: Session }) {
  if (!session) return null
  if (!session.isActive) return null
  
  return <div>{/* 렌더링 */}</div>
}
```

## 베스트 프랙티스
- 가드 클로즈로 조기 반환
- null/undefined 체크 먼저 수행
- 복잡한 조건은 단순화

