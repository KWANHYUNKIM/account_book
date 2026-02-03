# JS Set Map Lookups

## 개요
Set과 Map을 사용하여 빠른 조회 성능을 달성합니다.

## 예시

```typescript
// ❌ 나쁜 예: 배열 includes (O(n))
function isActiveCategory(categoryId: number, activeIds: number[]) {
  return activeIds.includes(categoryId)
}

// ✅ 좋은 예: Set 사용 (O(1))
function useActiveCategorySet(activeIds: number[]) {
  return useMemo(() => new Set(activeIds), [activeIds])
}

function CategoryFilter({ categories, activeIds }: Props) {
  const activeSet = useActiveCategorySet(activeIds)
  
  return categories.map(cat => (
    <CategoryItem
      key={cat.id}
      category={cat}
      isActive={activeSet.has(cat.id)} // O(1)
    />
  ))
}

// Map으로 빠른 조회
function useCategoryMap(categories: Category[]) {
  return useMemo(() => {
    const map = new Map()
    categories.forEach(cat => {
      map.set(cat.id, cat)
      map.set(cat.name, cat) // 이름으로도 조회 가능
    })
    return map
  }, [categories])
}
```

## 베스트 프랙티스
- 반복적인 조회는 Set/Map 사용
- useMemo로 Set/Map 생성 최적화
- 배열 includes 대신 Set.has 사용

