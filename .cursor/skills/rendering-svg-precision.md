# Rendering SVG Precision

## 개요
SVG 렌더링 정밀도를 최적화합니다.

## 예시

```typescript
// ❌ 나쁜 예: 부정확한 좌표
function Chart() {
  return (
    <svg width="100" height="100">
      <circle cx="33.333" cy="33.333" r="10" />
    </svg>
  )
}

// ✅ 좋은 예: 정수 좌표 사용
function Chart() {
  return (
    <svg width="100" height="100" viewBox="0 0 100 100">
      <circle cx="33" cy="33" r="10" />
    </svg>
  )
}

// viewBox로 반응형 처리
function ResponsiveIcon() {
  return (
    <svg 
      viewBox="0 0 24 24" 
      width="24" 
      height="24"
      preserveAspectRatio="xMidYMid meet"
    >
      <path d="M12 2L2 7L12 12L22 7L12 2Z" />
    </svg>
  )
}

// CSS로 크기 조절
function Icon({ size = 24 }: { size?: number }) {
  return (
    <svg 
      viewBox="0 0 24 24"
      style={{ width: size, height: size }}
    >
      <path d="..." />
    </svg>
  )
}
```

## 베스트 프랙티스
- 가능한 한 정수 좌표 사용
- viewBox로 반응형 처리
- preserveAspectRatio 설정

