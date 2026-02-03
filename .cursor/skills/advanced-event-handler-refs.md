# Advanced Event Handler Refs

## 개요
이벤트 핸들러에서 DOM 요소에 직접 접근해야 할 때 `useRef`를 사용합니다.

## 예시

```typescript
import { useRef, useEffect } from 'react'

function SearchInput() {
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    // 마운트 시 자동 포커스
    inputRef.current?.focus()
  }, [])

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape') {
      inputRef.current?.blur()
    }
  }

  return (
    <input
      ref={inputRef}
      onKeyDown={handleKeyDown}
      placeholder="검색..."
    />
  )
}
```

## 베스트 프랙티스
- `ref.current` 접근 전 null 체크 (`?.` 사용)
- `useEffect` 내에서 ref 접근
- 이벤트 핸들러는 `useCallback`으로 메모이제이션

