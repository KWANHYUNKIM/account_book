# Rendering Content Visibility

## 개요
보이지 않는 콘텐츠의 렌더링을 지연시켜 성능을 향상시킵니다.

## 예시

```typescript
import { useState } from 'react'

// 가상화를 사용한 긴 리스트
import { FixedSizeList } from 'react-window'

function LongTransactionList({ transactions }: { transactions: Transaction[] }) {
  return (
    <FixedSizeList
      height={600}
      itemCount={transactions.length}
      itemSize={80}
      width="100%"
    >
      {({ index, style }) => (
        <div style={style}>
          <TransactionItem transaction={transactions[index]} />
        </div>
      )}
    </FixedSizeList>
  )
}

// Intersection Observer로 지연 로딩
function useIntersectionObserver(ref: React.RefObject<HTMLElement>) {
  const [isVisible, setIsVisible] = useState(false)
  
  useEffect(() => {
    if (!ref.current) return
    
    const observer = new IntersectionObserver(
      ([entry]) => setIsVisible(entry.isIntersecting),
      { threshold: 0.1 }
    )
    
    observer.observe(ref.current)
    return () => observer.disconnect()
  }, [ref])
  
  return isVisible
}

function LazySection({ children }: { children: React.ReactNode }) {
  const ref = useRef<HTMLDivElement>(null)
  const isVisible = useIntersectionObserver(ref)
  
  return (
    <div ref={ref}>
      {isVisible ? children : <div style={{ height: '200px' }} />}
    </div>
  )
}
```

## 베스트 프랙티스
- 긴 리스트는 가상화 사용
- 보이지 않는 섹션은 지연 로딩
- Intersection Observer 활용

