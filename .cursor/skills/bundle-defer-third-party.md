# Bundle Defer Third Party

## 개요
서드파티 라이브러리는 지연 로딩하여 초기 번들 크기를 줄입니다.

## 예시

```typescript
// ❌ 나쁜 예: 즉시 import
import Chart from 'recharts'
import { PDFViewer } from '@react-pdf/renderer'

// ✅ 좋은 예: 동적 import
import dynamic from 'next/dynamic'

const Chart = dynamic(() => import('recharts').then(mod => mod.LineChart), {
  ssr: false,
  loading: () => <div>차트 로딩 중...</div>
})

const PDFViewer = dynamic(
  () => import('@react-pdf/renderer').then(mod => mod.PDFViewer),
  { ssr: false }
)

// 사용 예시
function ReportPage() {
  const [showChart, setShowChart] = useState(false)
  
  return (
    <div>
      <button onClick={() => setShowChart(true)}>차트 보기</button>
      {showChart && <Chart data={data} />}
    </div>
  )
}
```

## 베스트 프랙티스
- 큰 라이브러리는 동적 import
- 사용자가 필요로 할 때만 로드
- SSR이 필요 없는 경우 `ssr: false` 설정

