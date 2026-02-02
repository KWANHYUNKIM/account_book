'use client'

import { useState } from 'react'
import Icon from './icons/Icon'

interface SidebarProps {
  user: { email: string; name: string }
  onLogout: () => void
  onNavigate: (view: string) => void
  currentView: string
}

export default function Sidebar({ user, onLogout, onNavigate, currentView }: SidebarProps) {
  const [showProfileMenu, setShowProfileMenu] = useState(false)

  const getInitials = (name: string) => {
    return name ? name.charAt(0).toUpperCase() : 'U'
  }

  return (
    <div style={{
      width: '240px',
      height: '100vh',
      backgroundColor: '#2d2d2d',
      color: '#fff',
      display: 'flex',
      flexDirection: 'column',
      position: 'fixed',
      left: 0,
      top: 0,
      overflowY: 'auto'
    }}>
      {/* 프로필 섹션 */}
      <div style={{ padding: '20px', borderBottom: '1px solid #404040' }}>
        <div style={{ 
          display: 'flex', 
          alignItems: 'center', 
          gap: '12px',
          marginBottom: '12px'
        }}>
          <div style={{
            width: '48px',
            height: '48px',
            borderRadius: '50%',
            backgroundColor: '#ff6b9d',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '20px',
            fontWeight: 'bold',
            color: '#fff'
          }}>
            {getInitials(user.name)}
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: '14px', fontWeight: '600' }}>{user.name}</div>
            <div style={{ fontSize: '12px', color: '#999' }}>{user.email}</div>
          </div>
        </div>
      </div>

      {/* 네비게이션 메뉴 */}
      <div style={{ padding: '12px 0', flex: 1 }}>
        <div
          style={{
            padding: '10px 20px',
            cursor: 'pointer',
            backgroundColor: currentView === 'dashboard' ? '#404040' : 'transparent',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            fontSize: '14px'
          }}
          onClick={() => onNavigate('dashboard')}
          onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#404040'}
          onMouseLeave={(e) => e.currentTarget.style.backgroundColor = currentView === 'dashboard' ? '#404040' : 'transparent'}
        >
          <Icon name="dashboard" size={18} color="#fff" />
          <span>대시보드</span>
        </div>
        <div
          style={{
            padding: '10px 20px',
            cursor: 'pointer',
            backgroundColor: currentView === 'sessions' ? '#404040' : 'transparent',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            fontSize: '14px'
          }}
          onClick={() => onNavigate('sessions')}
          onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#404040'}
          onMouseLeave={(e) => e.currentTarget.style.backgroundColor = currentView === 'sessions' ? '#404040' : 'transparent'}
        >
          <Icon name="folder" size={18} color="#fff" />
          <span>내 세션</span>
        </div>
        <div
          style={{
            padding: '10px 20px',
            cursor: 'pointer',
            backgroundColor: currentView === 'accounts' ? '#404040' : 'transparent',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            fontSize: '14px'
          }}
          onClick={() => onNavigate('accounts')}
          onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#404040'}
          onMouseLeave={(e) => e.currentTarget.style.backgroundColor = currentView === 'accounts' ? '#404040' : 'transparent'}
        >
          <Icon name="bank" size={18} color="#fff" />
          <span>자산 연결</span>
        </div>
      </div>

      {/* 하단 설정 */}
      <div style={{ padding: '12px 0', borderTop: '1px solid #404040' }}>
        <div
          style={{
            padding: '10px 20px',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            fontSize: '14px',
            color: '#999'
          }}
          onClick={() => onNavigate('settings')}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = '#404040'
            e.currentTarget.style.color = '#fff'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = 'transparent'
            e.currentTarget.style.color = '#999'
          }}
        >
          <Icon name="settings" size={18} color="currentColor" />
          <span>설정</span>
        </div>
        <div
          style={{
            padding: '10px 20px',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            fontSize: '14px',
            color: '#999'
          }}
          onClick={onLogout}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = '#404040'
            e.currentTarget.style.color = '#fff'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = 'transparent'
            e.currentTarget.style.color = '#999'
          }}
        >
          <Icon name="logout" size={18} color="currentColor" />
          <span>로그아웃</span>
        </div>
      </div>
    </div>
  )
}

