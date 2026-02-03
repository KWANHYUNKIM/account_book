# Bundle Barrel Imports

## 개요
Barrel exports(index.ts)는 편리하지만 번들 크기를 증가시킬 수 있습니다.

## 예시

```typescript
// ❌ 나쁜 예: barrel export
// features/session/index.ts
export { default as DashboardView } from './DashboardView'
export { default as SessionDetailView } from './SessionDetailView'
export { default as SessionForm } from './SessionForm'
export { default as SessionCard } from './SessionCard'

// 사용 시: 모든 컴포넌트가 번들에 포함됨
import { DashboardView, SessionCard } from '@/features/session'

// ✅ 좋은 예: 직접 import
import DashboardView from '@/features/session/DashboardView'
import SessionCard from '@/features/session/SessionCard'
```

## 베스트 프랙티스
- 필요한 컴포넌트만 직접 import
- Barrel export는 타입 정의에만 사용
- Tree-shaking 최적화를 위해 named export 사용

