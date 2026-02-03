# Server Serialization

## 개요
서버에서 클라이언트로 전달할 데이터를 올바르게 직렬화합니다.

## 예시

```typescript
// ❌ 나쁜 예: 직렬화 불가능한 객체 전달
async function SessionPage({ params }: { params: { id: string } }) {
  const session = await fetchSession(params.id)
  // Date 객체는 직렬화되지 않음
  return <div>{session.createdAt}</div> // 에러 발생 가능
}

// ✅ 좋은 예: 직렬화 가능한 형태로 변환
async function SessionPage({ params }: { params: { id: string } }) {
  const session = await fetchSession(params.id)
  
  // Date를 문자열로 변환
  const serializedSession = {
    ...session,
    createdAt: session.createdAt.toISOString(),
    lastAccessedAt: session.lastAccessedAt?.toISOString() || null
  }
  
  return <SessionView session={serializedSession} />
}

// 또는 DTO 사용
interface SessionDTO {
  id: number
  name: string
  description: string | null
  createdAt: string // ISO string
  lastAccessedAt: string | null
}

function toSessionDTO(session: Session): SessionDTO {
  return {
    id: session.id,
    name: session.name,
    description: session.description,
    createdAt: session.createdAt.toISOString(),
    lastAccessedAt: session.lastAccessedAt?.toISOString() || null
  }
}

// Next.js API Route
export async function GET(request: NextRequest) {
  const sessions = await fetchSessions()
  const dtos = sessions.map(toSessionDTO)
  return NextResponse.json(dtos)
}
```

## 베스트 프랙티스
- Date 객체는 ISO 문자열로 변환
- 함수, Symbol 등은 제거
- DTO 패턴으로 명시적 변환

