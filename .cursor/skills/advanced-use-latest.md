# Advanced useLatest Hook

## 개요
항상 최신 값을 참조하는 커스텀 훅입니다. 클로저 문제를 해결합니다.

## 예시

```typescript
import { useRef, useEffect } from 'react'

function useLatest<T>(value: T) {
  const ref = useRef(value)
  
  useEffect(() => {
    ref.current = value
  }, [value])
  
  return ref
}

// 사용 예시
function Timer() {
  const [count, setCount] = useState(0)
  const latestCount = useLatest(count)

  useEffect(() => {
    const interval = setInterval(() => {
      // 항상 최신 count 값 사용
      console.log(latestCount.current)
    }, 1000)

    return () => clearInterval(interval)
  }, [])
}
```

## 사용 사례
- 비동기 작업에서 최신 상태 참조
- 타이머/인터벌에서 최신 값 사용
- 이벤트 리스너에서 최신 props 참조

