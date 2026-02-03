# Client Event Listeners

## 개요
클라이언트 사이드 이벤트 리스너를 올바르게 관리합니다.

## 예시

```typescript
import { useEffect, useRef } from 'react'

function SearchInput() {
  const inputRef = useRef<HTMLInputElement>(null)
  
  useEffect(() => {
    const handleKeyPress = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && inputRef.current) {
        inputRef.current.blur()
      }
    }
    
    // 전역 이벤트 리스너
    window.addEventListener('keydown', handleKeyPress)
    
    // cleanup
    return () => {
      window.removeEventListener('keydown', handleKeyPress)
    }
  }, [])
  
  return <input ref={inputRef} />
}

// 최신 값 참조를 위한 useLatest 사용
function useLatest<T>(value: T) {
  const ref = useRef(value)
  useEffect(() => {
    ref.current = value
  }, [value])
  return ref
}

function ScrollTracker() {
  const [scrollY, setScrollY] = useState(0)
  const latestScrollY = useLatest(scrollY)
  
  useEffect(() => {
    const handleScroll = () => {
      // 최신 값 사용
      console.log(latestScrollY.current)
    }
    
    window.addEventListener('scroll', handleScroll, { passive: true })
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])
}
```

## 베스트 프랙티스
- 항상 cleanup 함수로 리스너 제거
- passive 옵션으로 스크롤 성능 향상
- 최신 값 참조는 useLatest 훅 사용

