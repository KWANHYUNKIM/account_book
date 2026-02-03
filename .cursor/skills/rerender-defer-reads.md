# Rerender Defer Reads

## 개요
DOM 읽기를 지연시켜 리플로우를 최소화합니다.

## 예시

```typescript
import { useEffect, useRef } from 'react'

// ❌ 나쁜 예: 읽기와 쓰기 혼합
function updateLayout() {
  const element = document.getElementById('content')
  element.style.width = '200px'  // 쓰기
  const width = element.offsetWidth  // 읽기 (리플로우 발생)
  element.style.height = `${width * 2}px`  // 쓰기
}

// ✅ 좋은 예: 읽기와 쓰기 분리
function updateLayout() {
  const element = document.getElementById('content')
  
  // 모든 읽기 먼저
  const currentWidth = element.offsetWidth
  const currentHeight = element.offsetHeight
  
  // 그 다음 모든 쓰기
  requestAnimationFrame(() => {
    element.style.width = '200px'
    element.style.height = `${currentWidth * 2}px`
  })
}

// React에서 사용
function ResizableBox() {
  const boxRef = useRef<HTMLDivElement>(null)
  
  const handleResize = () => {
    if (!boxRef.current) return
    
    // 읽기 먼저
    const rect = boxRef.current.getBoundingClientRect()
    
    // 쓰기는 requestAnimationFrame으로
    requestAnimationFrame(() => {
      if (boxRef.current) {
        boxRef.current.style.width = `${rect.width * 1.5}px`
      }
    })
  }
  
  return <div ref={boxRef} onMouseMove={handleResize}>박스</div>
}
```

## 베스트 프랙티스
- 읽기와 쓰기 분리
- requestAnimationFrame으로 배치 처리
- getBoundingClientRect 등 읽기 작업 먼저 수행

