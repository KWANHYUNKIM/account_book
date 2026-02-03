# Bundle Conditional

## 개요
조건부로 코드를 번들에 포함하여 불필요한 코드를 제거합니다.

## 예시

```typescript
// 환경 변수로 조건부 번들링
if (process.env.NODE_ENV === 'development') {
  // 개발 환경에서만 포함
  console.log('Debug info:', data)
}

// 기능 플래그로 조건부 로드
const enableAnalytics = process.env.NEXT_PUBLIC_ENABLE_ANALYTICS === 'true'

if (enableAnalytics) {
  // 동적 import로 조건부 로드
  const { trackEvent } = await import('@/utils/analytics')
  trackEvent('page_view')
}

// Next.js 동적 import
import dynamic from 'next/dynamic'

const HeavyComponent = dynamic(
  () => import('./HeavyComponent'),
  { 
    ssr: false, // 서버 사이드 렌더링 비활성화
    loading: () => <div>로딩 중...</div>
  }
)
```

## 베스트 프랙티스
- 개발 전용 코드는 환경 변수로 가드
- 무거운 컴포넌트는 동적 import
- 사용자 액션 후에만 로드되는 코드는 lazy load

