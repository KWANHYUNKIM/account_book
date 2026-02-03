# JS Batch DOM CSS

## 개요
DOM과 CSS 변경을 배치 처리하여 리플로우/리페인트를 최소화합니다.

## 예시

```typescript
// ❌ 나쁜 예: 여러 번의 리플로우 발생
function animateElement(element: HTMLElement) {
  element.style.width = '100px'  // 리플로우
  element.style.height = '100px' // 리플로우
  element.style.color = 'red'     // 리페인트
}

// ✅ 좋은 예: 한 번에 배치 처리
function animateElement(element: HTMLElement) {
  // requestAnimationFrame으로 배치
  requestAnimationFrame(() => {
    element.style.cssText = 'width: 100px; height: 100px; color: red;'
  })
}

// 또는 CSS 클래스 사용
function animateElement(element: HTMLElement) {
  element.classList.add('animated')
}

// React에서 사용
function AnimatedBox() {
  const boxRef = useRef<HTMLDivElement>(null)
  
  const handleClick = () => {
    if (boxRef.current) {
      // CSS 변수 사용 (리플로우 최소화)
      boxRef.current.style.setProperty('--scale', '1.2')
      boxRef.current.classList.add('scale-up')
    }
  }
  
  return <div ref={boxRef} onClick={handleClick}>클릭</div>
}
```

## 베스트 프랙티스
- 여러 스타일 변경은 한 번에 처리
- CSS 클래스 사용 선호
- CSS 변수로 동적 스타일 관리

