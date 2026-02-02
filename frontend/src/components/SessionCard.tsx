'use client'

import Icon, { getIconName } from './icons/Icon'

interface SessionCardProps {
  session: {
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
  onClick: () => void
}

export default function SessionCard({ session, onClick }: SessionCardProps) {
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW',
      notation: 'compact'
    }).format(amount)
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    
    if (days === 0) return '오늘'
    if (days === 1) return '어제'
    if (days < 7) return `${days}일 전`
    if (days < 30) return `${Math.floor(days / 7)}주 전`
    if (days < 365) return `${Math.floor(days / 30)}개월 전`
    return `${Math.floor(days / 365)}년 전`
  }

  return (
    <div
      onClick={onClick}
      style={{
        backgroundColor: '#fff',
        borderRadius: '8px',
        padding: '20px',
        cursor: 'pointer',
        border: '1px solid #e0e0e0',
        transition: 'all 0.2s',
        height: '200px',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between'
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = 'translateY(-4px)'
        e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.1)'
        e.currentTarget.style.borderColor = session.color || '#0070f3'
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = 'translateY(0)'
        e.currentTarget.style.boxShadow = 'none'
        e.currentTarget.style.borderColor = '#e0e0e0'
      }}
    >
      <div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '12px' }}>
          <div style={{
            width: '40px',
            height: '40px',
            borderRadius: '8px',
            backgroundColor: session.color || '#0070f3',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <Icon 
              name={getIconName(session.icon || '💰')} 
              size={24} 
              color="#fff" 
            />
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: '16px', fontWeight: '600', color: '#333', marginBottom: '4px' }}>
              {session.name}
            </div>
            {session.description && (
              <div style={{ fontSize: '12px', color: '#666' }}>
                {session.description}
              </div>
            )}
          </div>
        </div>
      </div>

      <div>
        <div style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          marginBottom: '8px'
        }}>
          <div style={{ fontSize: '12px', color: '#999' }}>
            거래 {session.transactionCount}건
          </div>
          <div style={{ fontSize: '12px', color: '#999' }}>
            {formatDate(session.lastAccessedAt)}
          </div>
        </div>
        <div style={{
          padding: '12px',
          borderRadius: '6px',
          backgroundColor: '#f8f9fa',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <div>
            <div style={{ fontSize: '11px', color: '#666', marginBottom: '4px' }}>잔액</div>
            <div style={{
              fontSize: '18px',
              fontWeight: 'bold',
              color: session.balance >= 0 ? '#28a745' : '#dc3545'
            }}>
              {formatCurrency(session.balance)}
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '11px', color: '#666', marginBottom: '2px' }}>수입</div>
            <div style={{ fontSize: '12px', color: '#28a745', fontWeight: '600' }}>
              {formatCurrency(session.totalIncome)}
            </div>
            <div style={{ fontSize: '11px', color: '#666', marginTop: '4px', marginBottom: '2px' }}>지출</div>
            <div style={{ fontSize: '12px', color: '#dc3545', fontWeight: '600' }}>
              {formatCurrency(session.totalExpense)}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

