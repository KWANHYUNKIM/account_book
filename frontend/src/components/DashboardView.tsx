'use client'

import { useState, useEffect } from 'react'
import api from '@/utils/api'
import SessionCard from './SessionCard'
import SessionForm from './SessionForm'
import Icon from './icons/Icon'

interface DashboardViewProps {
  onSessionClick: (sessionId: number) => void
}

interface Session {
  id: number
  name: string
  description?: string
  color: string
  icon: string
  transactionCount: number
  totalIncome: number
  totalExpense: number
  balance: number
  lastAccessedAt: string
}

export default function DashboardView({ onSessionClick }: DashboardViewProps) {
  const [sessions, setSessions] = useState<Session[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [activeTab, setActiveTab] = useState<'recent' | 'all'>('recent')
  const [showSessionForm, setShowSessionForm] = useState(false)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadSessions()
  }, [])

  const loadSessions = async () => {
    try {
      const response = await api.get('/sessions')
      setSessions(response.data)
    } catch (error) {
      console.error('세션 로드 실패:', error)
    }
  }

  const handleCreateSession = () => {
    setShowSessionForm(true)
  }

  const handleSessionCreated = () => {
    setShowSessionForm(false)
    loadSessions()
  }

  const filteredSessions = sessions.filter(session =>
    session.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    (session.description && session.description.toLowerCase().includes(searchQuery.toLowerCase()))
  )

  const sortedSessions = activeTab === 'recent'
    ? [...filteredSessions].sort((a, b) => 
        new Date(b.lastAccessedAt).getTime() - new Date(a.lastAccessedAt).getTime()
      )
    : filteredSessions

  return (
    <div style={{ padding: '40px', marginLeft: '240px' }}>
      {/* 상단 헤더 */}
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px', color: '#333' }}>
          가계부
        </h1>
        <p style={{ fontSize: '16px', color: '#666' }}>
          아이디어를 설명하고 실현하세요
        </p>
      </div>

      {/* 검색 및 필터 */}
      <div style={{ marginBottom: '24px' }}>
        <div style={{
          position: 'relative',
          marginBottom: '20px'
        }}>
          <input
            type="text"
            placeholder="세션을 검색하세요..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              width: '100%',
              padding: '16px 20px',
              fontSize: '16px',
              border: '1px solid #e0e0e0',
              borderRadius: '8px',
              outline: 'none',
              transition: 'border-color 0.2s'
            }}
            onFocus={(e) => e.currentTarget.style.borderColor = '#0070f3'}
            onBlur={(e) => e.currentTarget.style.borderColor = '#e0e0e0'}
          />
          <span style={{
            position: 'absolute',
            right: '20px',
            top: '50%',
            transform: 'translateY(-50%)',
            fontSize: '12px',
            padding: '4px 8px',
            backgroundColor: '#f0f0f0',
            borderRadius: '4px',
            color: '#666'
          }}>
            AI
          </span>
        </div>

        {/* 탭 */}
        <div style={{ display: 'flex', gap: '8px', marginBottom: '20px' }}>
          <button
            onClick={() => setActiveTab('recent')}
            style={{
              padding: '8px 16px',
              border: 'none',
              background: 'transparent',
              borderBottom: activeTab === 'recent' ? '2px solid #0070f3' : '2px solid transparent',
              color: activeTab === 'recent' ? '#0070f3' : '#666',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: activeTab === 'recent' ? '600' : '400'
            }}
          >
            최근 본 항목
          </button>
          <button
            onClick={() => setActiveTab('all')}
            style={{
              padding: '8px 16px',
              border: 'none',
              background: 'transparent',
              borderBottom: activeTab === 'all' ? '2px solid #0070f3' : '2px solid transparent',
              color: activeTab === 'all' ? '#0070f3' : '#666',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: activeTab === 'all' ? '600' : '400'
            }}
          >
            모든 세션
          </button>
        </div>
      </div>

      {/* 세션 그리드 */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
        gap: '20px',
        marginBottom: '40px'
      }}>
        {/* 새 세션 생성 카드 */}
        <div
          onClick={handleCreateSession}
          style={{
            backgroundColor: '#f8f9fa',
            borderRadius: '8px',
            padding: '20px',
            cursor: 'pointer',
            border: '2px dashed #d0d0d0',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            height: '200px',
            transition: 'all 0.2s'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.borderColor = '#0070f3'
            e.currentTarget.style.backgroundColor = '#f0f7ff'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.borderColor = '#d0d0d0'
            e.currentTarget.style.backgroundColor = '#f8f9fa'
          }}
        >
          <div style={{ fontSize: '32px', marginBottom: '12px' }}>+</div>
          <div style={{ fontSize: '16px', fontWeight: '600', color: '#666' }}>
            새 가계부 세션 만들기
          </div>
        </div>

        {/* 세션 카드들 */}
        {sortedSessions.map(session => (
          <SessionCard
            key={session.id}
            session={session}
            onClick={() => onSessionClick(session.id)}
          />
        ))}
      </div>

      {sortedSessions.length === 0 && !searchQuery && (
        <div style={{
          textAlign: 'center',
          padding: '60px 20px',
          color: '#999'
        }}>
          <div style={{ marginBottom: '20px', display: 'flex', justifyContent: 'center' }}>
            <Icon name="chart" size={48} color="#999" />
          </div>
          <div style={{ fontSize: '18px', marginBottom: '8px' }}>세션이 없습니다</div>
          <div style={{ fontSize: '14px' }}>새 가계부 세션을 만들어 시작하세요</div>
        </div>
      )}

      {showSessionForm && (
        <SessionForm
          onClose={() => setShowSessionForm(false)}
          onSuccess={handleSessionCreated}
        />
      )}
    </div>
  )
}

