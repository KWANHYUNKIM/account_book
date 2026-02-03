'use client'

import { useState, useEffect } from 'react'
import api from '@/utils/api'
import SessionForm from './SessionForm'
import Icon, { getIconName } from '@/ui/common/icons/Icon'

interface SessionListViewProps {
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

export default function SessionListView({ onSessionClick }: SessionListViewProps) {
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
      // ApiResponse로 감싸져 있을 수 있으므로 data 필드 확인
      const data = response.data?.data || response.data
      setSessions(Array.isArray(data) ? data : [])
    } catch (error: any) {
      console.error('기록 로드 실패:', error)
      if (error.response?.status === 401) {
        alert('로그인이 필요합니다. 다시 로그인해주세요.')
        window.location.href = '/'
      } else if (error.response?.status === 500) {
        const errorMessage = error.response?.data?.message || '서버 오류가 발생했습니다.'
        console.error('서버 오류:', errorMessage)
        alert(`기록을 불러올 수 없습니다: ${errorMessage}`)
      }
      setSessions([]) // 에러 시 빈 배열로 설정
    }
  }

  const handleCreateSession = () => {
    setShowSessionForm(true)
  }

  const handleSessionCreated = () => {
    setShowSessionForm(false)
    loadSessions()
  }

  const filteredSessions = Array.isArray(sessions) ? sessions.filter(session =>
    session?.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    (session?.description && session.description.toLowerCase().includes(searchQuery.toLowerCase()))
  ) : []

  const sortedSessions = activeTab === 'recent'
    ? [...filteredSessions].sort((a, b) => 
        new Date(b.lastAccessedAt).getTime() - new Date(a.lastAccessedAt).getTime()
      )
    : filteredSessions

  return (
    <div style={{ padding: '40px', marginLeft: '240px' }}>
      {/* 상단 헤더 */}
      <div style={{ marginBottom: '32px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px', color: '#333' }}>
            기록
          </h1>
          <p style={{ fontSize: '16px', color: '#666' }}>
            모든 기록을 관리하고 확인하세요
          </p>
        </div>
        <button
          onClick={handleCreateSession}
          style={{
            padding: '12px 24px',
            backgroundColor: '#0070f3',
            color: '#fff',
            border: 'none',
            borderRadius: '8px',
            fontSize: '14px',
            fontWeight: '600',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            transition: 'background-color 0.2s'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = '#0051cc'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = '#0070f3'
          }}
        >
          <span style={{ fontSize: '20px' }}>+</span>
          새 기록 만들기
        </button>
      </div>

      {/* 검색 및 필터 */}
      <div style={{ marginBottom: '24px' }}>
        <div style={{
          position: 'relative',
          marginBottom: '20px'
        }}>
          <input
            type="text"
            placeholder="기록을 검색하세요..."
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
            모든 기록
          </button>
        </div>
      </div>

      {/* 기록 리스트 (일정관리 스타일) */}
      <div style={{ marginBottom: '40px' }}>
        {sortedSessions.length > 0 ? (
          <div style={{
            backgroundColor: '#fff',
            borderRadius: '12px',
            border: '1px solid #e0e0e0',
            overflow: 'hidden'
          }}>
            {sortedSessions.map((session, index) => {
              const date = new Date(session.lastAccessedAt)
              const dateStr = date.toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
              })
              const timeStr = date.toLocaleTimeString('ko-KR', {
                hour: '2-digit',
                minute: '2-digit'
              })
              const isToday = date.toDateString() === new Date().toDateString()
              const isYesterday = date.toDateString() === new Date(Date.now() - 86400000).toDateString()
              
              let dateLabel = dateStr
              if (isToday) dateLabel = '오늘'
              else if (isYesterday) dateLabel = '어제'
              
              const showDateHeader = index === 0 || 
                sortedSessions[index - 1].lastAccessedAt.split('T')[0] !== session.lastAccessedAt.split('T')[0]

              return (
                <div key={session.id}>
                  {showDateHeader && (
                    <div style={{
                      padding: '16px 20px',
                      backgroundColor: '#f8f9fa',
                      borderBottom: '1px solid #e0e0e0',
                      fontSize: '14px',
                      fontWeight: '600',
                      color: '#666'
                    }}>
                      {dateLabel}
                    </div>
                  )}
                  <div
                    onClick={() => onSessionClick(session.id)}
                    style={{
                      padding: '20px',
                      borderBottom: index < sortedSessions.length - 1 ? '1px solid #f0f0f0' : 'none',
                      cursor: 'pointer',
                      transition: 'background-color 0.2s',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '16px'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.backgroundColor = '#f8f9fa'
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.backgroundColor = '#fff'
                    }}
                  >
                    {/* 아이콘 */}
                    <div style={{
                      width: '48px',
                      height: '48px',
                      borderRadius: '12px',
                      backgroundColor: session.color || '#0070f3',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0
                    }}>
                      <Icon 
                        name={getIconName(session.icon || '💰')} 
                        size={24} 
                        color="#fff" 
                      />
                    </div>

                    {/* 기록 정보 */}
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '12px',
                        marginBottom: '8px'
                      }}>
                        <h3 style={{
                          fontSize: '16px',
                          fontWeight: '600',
                          color: '#333',
                          margin: 0,
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap'
                        }}>
                          {session.name}
                        </h3>
                        {session.description && (
                          <span style={{
                            fontSize: '12px',
                            color: '#999',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap'
                          }}>
                            {session.description}
                          </span>
                        )}
                      </div>
                      
                      <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '16px',
                        fontSize: '13px',
                        color: '#666'
                      }}>
                        <span>거래 {session.transactionCount}건</span>
                        <span>•</span>
                        <span>{timeStr}</span>
                      </div>
                    </div>

                    {/* 금액 정보 */}
                    <div style={{
                      textAlign: 'right',
                      flexShrink: 0,
                      minWidth: '120px'
                    }}>
                      <div style={{
                        fontSize: '18px',
                        fontWeight: 'bold',
                        color: session.balance >= 0 ? '#28a745' : '#dc3545',
                        marginBottom: '4px'
                      }}>
                        {new Intl.NumberFormat('ko-KR', {
                          style: 'currency',
                          currency: 'KRW',
                          notation: 'compact'
                        }).format(session.balance)}
                      </div>
                      <div style={{
                        fontSize: '12px',
                        color: '#999',
                        display: 'flex',
                        gap: '8px',
                        justifyContent: 'flex-end'
                      }}>
                        <span style={{ color: '#28a745' }}>
                          +{new Intl.NumberFormat('ko-KR', {
                            style: 'currency',
                            currency: 'KRW',
                            notation: 'compact'
                          }).format(session.totalIncome)}
                        </span>
                        <span style={{ color: '#dc3545' }}>
                          -{new Intl.NumberFormat('ko-KR', {
                            style: 'currency',
                            currency: 'KRW',
                            notation: 'compact'
                          }).format(session.totalExpense)}
                        </span>
                      </div>
                    </div>

                    {/* 화살표 아이콘 */}
                    <div style={{
                      color: '#999',
                      fontSize: '20px',
                      flexShrink: 0
                    }}>
                      →
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        ) : (
          <div style={{
            backgroundColor: '#fff',
            borderRadius: '12px',
            border: '1px solid #e0e0e0',
            padding: '60px 20px',
            textAlign: 'center',
            color: '#999'
          }}>
            <div style={{ marginBottom: '20px', display: 'flex', justifyContent: 'center' }}>
              <Icon name="chart" size={48} color="#999" />
            </div>
            <div style={{ fontSize: '18px', marginBottom: '8px' }}>기록이 없습니다</div>
            <div style={{ fontSize: '14px' }}>새 기록을 만들어 시작하세요</div>
          </div>
        )}
      </div>

      {showSessionForm && (
        <SessionForm
          onClose={() => setShowSessionForm(false)}
          onSuccess={handleSessionCreated}
        />
      )}
    </div>
  )
}

