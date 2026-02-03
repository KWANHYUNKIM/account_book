# Rerender Transitions

## 개요
useTransition으로 긴급하지 않은 업데이트를 지연시킵니다.

## 예시

```typescript
import { useTransition, useState } from 'react'

// ❌ 나쁜 예: 모든 업데이트가 긴급하게 처리
function SearchInput({ onSearch }: { onSearch: (query: string) => void }) {
  const [query, setQuery] = useState('')
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    setQuery(value)
    onSearch(value) // 매 입력마다 즉시 실행
  }
  
  return <input value={query} onChange={handleChange} />
}

// ✅ 좋은 예: useTransition으로 우선순위 조정
function SearchInput({ onSearch }: { onSearch: (query: string) => void }) {
  const [query, setQuery] = useState('')
  const [isPending, startTransition] = useTransition()
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    setQuery(value) // 긴급: 입력값 즉시 업데이트
    
    startTransition(() => {
      onSearch(value) // 긴급하지 않음: 검색 지연
    })
  }
  
  return (
    <div>
      <input value={query} onChange={handleChange} />
      {isPending && <span>검색 중...</span>}
    </div>
  )
}

// 필터링에도 적용
function FilterableList({ items }: { items: Item[] }) {
  const [filter, setFilter] = useState('')
  const [isPending, startTransition] = useTransition()
  const [filteredItems, setFilteredItems] = useState(items)
  
  const handleFilterChange = (newFilter: string) => {
    setFilter(newFilter) // 긴급: 입력값 즉시 업데이트
    
    startTransition(() => {
      // 긴급하지 않음: 필터링 지연
      setFilteredItems(
        items.filter(item => item.name.includes(newFilter))
      )
    })
  }
  
  return (
    <div>
      <input value={filter} onChange={e => handleFilterChange(e.target.value)} />
      {isPending && <div>필터링 중...</div>}
      <ul>
        {filteredItems.map(item => (
          <li key={item.id}>{item.name}</li>
        ))}
      </ul>
    </div>
  )
}
```

## 베스트 프랙티스
- 사용자 입력은 즉시 반영 (긴급)
- 검색/필터링은 transition으로 지연 (긴급하지 않음)
- isPending으로 로딩 상태 표시

