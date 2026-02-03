# Rendering Animate SVG Wrapper

## 개요
SVG 애니메이션을 최적화하여 성능을 향상시킵니다.

## 예시

```typescript
import { motion } from 'framer-motion'

// ❌ 나쁜 예: SVG 전체에 애니메이션
function AnimatedIcon() {
  return (
    <motion.svg
      animate={{ rotate: 360 }}
      transition={{ duration: 2, repeat: Infinity }}
    >
      <path d="..." />
    </motion.svg>
  )
}

// ✅ 좋은 예: 래퍼 div에 애니메이션
function AnimatedIcon() {
  return (
    <motion.div
      animate={{ rotate: 360 }}
      transition={{ duration: 2, repeat: Infinity }}
      style={{ display: 'inline-flex' }}
    >
      <svg>
        <path d="..." />
      </svg>
    </motion.div>
  )
}

// CSS 애니메이션 사용 (더 효율적)
function AnimatedIcon() {
  return (
    <div className="animate-spin">
      <Icon name="loading" />
    </div>
  )
}

// CSS
// .animate-spin {
//   animation: spin 2s linear infinite;
// }
```

## 베스트 프랙티스
- SVG는 정적 유지, 래퍼에 애니메이션
- CSS 애니메이션이 더 효율적
- transform 속성 사용 (GPU 가속)

