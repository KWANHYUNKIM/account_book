# Bundle Preload

## 개요
중요한 리소스를 미리 로드하여 성능을 향상시킵니다.

## 예시

```typescript
// Link 컴포넌트의 prefetch
import Link from 'next/link'

function Navigation() {
  return (
    <nav>
      {/* Next.js는 기본적으로 prefetch를 수행 */}
      <Link href="/dashboard">대시보드</Link>
      
      {/* 중요하지 않은 페이지는 prefetch 비활성화 */}
      <Link href="/settings" prefetch={false}>설정</Link>
    </nav>
  )
}

// 프로그래밍 방식으로 preload
import { useRouter } from 'next/navigation'

function SessionCard({ sessionId }: { sessionId: number }) {
  const router = useRouter()
  
  const handleMouseEnter = () => {
    // 호버 시 미리 로드
    router.prefetch(`/session/${sessionId}`)
  }
  
  return (
    <div onMouseEnter={handleMouseEnter}>
      {/* 카드 내용 */}
    </div>
  )
}

// 리소스 preload
useEffect(() => {
  const link = document.createElement('link')
  link.rel = 'preload'
  link.as = 'script'
  link.href = '/heavy-library.js'
  document.head.appendChild(link)
}, [])
```

## 베스트 프랙티스
- 사용자가 방문할 가능성이 높은 페이지는 prefetch
- 무거운 리소스는 사용자 액션 후 preload
- 불필요한 prefetch는 비활성화

