# Rerender Functional setState

## 개요
함수형 setState를 사용하여 최신 상태를 보장합니다.

## 예시

```typescript
import { useState } from 'react'

// ❌ 나쁜 예: 이전 상태 직접 참조
function Counter() {
  const [count, setCount] = useState(0)
  
  const handleIncrement = () => {
    setCount(count + 1) // 이전 count 값 사용
    setCount(count + 1) // 같은 값으로 두 번 설정됨
  }
  
  return <button onClick={handleIncrement}>Count: {count}</button>
}

// ✅ 좋은 예: 함수형 setState
function Counter() {
  const [count, setCount] = useState(0)
  
  const handleIncrement = () => {
    setCount(prev => prev + 1) // 최신 값 사용
    setCount(prev => prev + 1) // 정상적으로 2 증가
  }
  
  return <button onClick={handleIncrement}>Count: {count}</button>
}

// 복잡한 상태 업데이트
function TransactionForm() {
  const [formData, setFormData] = useState({
    amount: 0,
    type: 'INCOME'
  })
  
  const updateAmount = (newAmount: number) => {
    setFormData(prev => ({
      ...prev,
      amount: newAmount
    }))
  }
  
  // 여러 필드 동시 업데이트
  const updateFields = (fields: Partial<typeof formData>) => {
    setFormData(prev => ({
      ...prev,
      ...fields
    }))
  }
}
```

## 베스트 프랙티스
- 이전 상태를 참조할 때는 함수형 setState 사용
- 여러 상태 업데이트는 하나로 합치기
- 객체 상태는 스프레드 연산자로 불변성 유지

