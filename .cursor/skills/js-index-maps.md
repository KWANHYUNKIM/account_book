# JS Index Maps

## 개요
배열 검색 대신 Map/Set을 사용하여 O(1) 조회 성능을 달성합니다.

## 예시

```typescript
// ❌ 나쁜 예: 배열에서 검색 (O(n))
function findCategoryById(categories: Category[], id: number) {
  return categories.find(cat => cat.id === id)
}

function TransactionList({ transactions, categories }: Props) {
  return transactions.map(t => {
    const category = categories.find(cat => cat.id === t.categoryId) // O(n)
    return <div key={t.id}>{category?.name}</div>
  })
}

// ✅ 좋은 예: Map 사용 (O(1))
function useCategoryMap(categories: Category[]) {
  return useMemo(() => {
    const map = new Map<number, Category>()
    categories.forEach(cat => map.set(cat.id, cat))
    return map
  }, [categories])
}

function TransactionList({ transactions, categories }: Props) {
  const categoryMap = useCategoryMap(categories)
  
  return transactions.map(t => {
    const category = categoryMap.get(t.categoryId) // O(1)
    return <div key={t.id}>{category?.name}</div>
  })
}

// Set으로 중복 체크
function useUniqueIds(items: { id: number }[]) {
  return useMemo(() => {
    return new Set(items.map(item => item.id))
  }, [items])
}
```

## 베스트 프랙티스
- 반복적인 조회가 필요한 경우 Map 사용
- 중복 체크는 Set 사용
- useMemo로 Map/Set 생성 최적화

