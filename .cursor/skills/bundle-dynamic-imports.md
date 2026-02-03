# Bundle Dynamic Imports

## 개요
코드 스플리팅을 통해 필요한 코드만 로드합니다.

## 예시

```typescript
// React.lazy 사용
import { lazy, Suspense } from 'react'

const SessionDetailView = lazy(() => import('@/features/session/SessionDetailView'))
const BankAccountForm = lazy(() => import('@/features/bank/BankAccountForm'))

function App() {
  const [showDetail, setShowDetail] = useState(false)
  
  return (
    <div>
      <button onClick={() => setShowDetail(true)}>상세 보기</button>
      {showDetail && (
        <Suspense fallback={<div>로딩 중...</div>}>
          <SessionDetailView sessionId={1} />
        </Suspense>
      )}
    </div>
  )
}

// Next.js dynamic import
import dynamic from 'next/dynamic'

const Modal = dynamic(() => import('@/ui/common/Modal'), {
  ssr: false,
  loading: () => <div>모달 로딩 중...</div>
})
```

## 베스트 프랙티스
- 라우트별로 코드 스플리팅
- 모달, 드로어 등 사용자 액션 후 로드되는 컴포넌트는 lazy load
- Suspense로 로딩 상태 처리

