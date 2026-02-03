'use client'

import { useState, useEffect } from 'react'
import api from '@/utils/api'
import Sidebar from '@/layout/Sidebar'
import DashboardView from '@/features/session/DashboardView'
import SessionListView from '@/features/session/SessionListView'
import SessionDetailView from '@/features/session/SessionDetailView'
import BankAccountListView from '@/features/bank/BankAccountListView'
import LoginForm from '@/features/auth/LoginForm'
import RegisterForm from '@/features/auth/RegisterForm'

export default function Home() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [user, setUser] = useState<{ email: string; name: string } | null>(null)
  const [showLogin, setShowLogin] = useState(false)
  const [showRegister, setShowRegister] = useState(false)
  const [currentView, setCurrentView] = useState<'dashboard' | 'sessions' | 'session' | 'accounts' | 'settings'>('dashboard')
  const [selectedSessionId, setSelectedSessionId] = useState<number | null>(null)

  useEffect(() => {
    const token = localStorage.getItem('token')
    const email = localStorage.getItem('userEmail')
    const name = localStorage.getItem('userName')
    
    if (token && email && name) {
      setIsAuthenticated(true)
      setUser({ email, name })
    } else {
      setShowLogin(true)
    }
  }, [])

  const handleLogin = (token: string, email: string, name: string) => {
    setIsAuthenticated(true)
    setUser({ email, name })
    setShowLogin(false)
  }

  const handleRegister = (token: string, email: string, name: string) => {
    setIsAuthenticated(true)
    setUser({ email, name })
    setShowRegister(false)
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userEmail')
    localStorage.removeItem('userName')
    setIsAuthenticated(false)
    setUser(null)
    setCurrentView('dashboard')
    setSelectedSessionId(null)
    setShowLogin(true)
  }

  const handleNavigate = (view: string) => {
    setCurrentView(view as any)
    setSelectedSessionId(null)
  }

  const handleSessionClick = (sessionId: number) => {
    setSelectedSessionId(sessionId)
    setCurrentView('session')
  }

  if (!isAuthenticated) {
    return (
      <>
        {showLogin && (
          <LoginForm
            onLogin={handleLogin}
            onSwitchToRegister={() => {
              setShowLogin(false)
              setShowRegister(true)
            }}
          />
        )}
        {showRegister && (
          <RegisterForm
            onRegister={handleRegister}
            onSwitchToLogin={() => {
              setShowRegister(false)
              setShowLogin(true)
            }}
          />
        )}
      </>
    )
  }

  if (!user) return null

  return (
    <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#fafafa' }}>
      <Sidebar
        user={user}
        onLogout={handleLogout}
        onNavigate={handleNavigate}
        currentView={currentView}
      />
      
      <div style={{ flex: 1, marginLeft: '240px' }}>
        {currentView === 'dashboard' && (
          <DashboardView onSessionClick={handleSessionClick} />
        )}
        {currentView === 'sessions' && (
          <SessionListView onSessionClick={handleSessionClick} />
        )}
        {currentView === 'session' && selectedSessionId && (
          <SessionDetailView
            sessionId={selectedSessionId}
            onBack={() => {
              setCurrentView('sessions')
              setSelectedSessionId(null)
            }}
          />
        )}
        {currentView === 'accounts' && (
          <BankAccountListView />
        )}
        {currentView === 'settings' && (
          <div style={{ padding: '40px' }}>
            <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '20px' }}>설정</h1>
            <div className="card">
              <h2>계정 설정</h2>
              <p>이름: {user.name}</p>
              <p>이메일: {user.email}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
