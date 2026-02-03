# React Best Practices

## 컴포넌트 구조
- 기능별로 `features/` 폴더에 그룹화
- 재사용 가능한 UI 컴포넌트는 `ui/common/`에 배치
- 레이아웃 컴포넌트는 `layout/`에 배치

## 네이밍 규칙
- 컴포넌트 파일: PascalCase (예: `DashboardView.tsx`)
- 훅: camelCase with `use` prefix (예: `useSessionData`)
- 유틸리티 함수: camelCase (예: `formatCurrency`)

## 상태 관리
- 로컬 상태는 `useState` 사용
- 서버 상태는 React Query나 SWR 사용 고려
- 전역 상태는 Context API 또는 상태 관리 라이브러리 사용

## 성능 최적화
- 불필요한 리렌더링 방지를 위해 `React.memo` 사용
- 무거운 계산은 `useMemo`로 메모이제이션
- 이벤트 핸들러는 `useCallback`으로 메모이제이션

## 타입 안정성
- 모든 컴포넌트에 TypeScript 타입 정의
- Props는 interface로 명시적으로 정의
- API 응답 타입도 정의

