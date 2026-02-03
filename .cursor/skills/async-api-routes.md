# Async API Routes

## 개요
Next.js API 라우트에서 비동기 처리를 올바르게 구현합니다.

## 예시

```typescript
// app/api/transactions/route.ts
import { NextRequest, NextResponse } from 'next/server'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const sessionId = searchParams.get('sessionId')

    // 병렬로 여러 데이터 가져오기
    const [transactions, summary] = await Promise.all([
      fetchTransactions(sessionId),
      fetchSummary(sessionId)
    ])

    return NextResponse.json({
      transactions,
      summary
    })
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to fetch transactions' },
      { status: 500 }
    )
  }
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const transaction = await createTransaction(body)
    
    return NextResponse.json(transaction, { status: 201 })
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to create transaction' },
      { status: 500 }
    )
  }
}
```

## 베스트 프랙티스
- 항상 try-catch로 에러 처리
- 적절한 HTTP 상태 코드 반환
- 병렬 요청은 `Promise.all` 사용

