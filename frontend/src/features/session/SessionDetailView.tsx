'use client'

import { useState, useEffect } from 'react'
import api from '@/utils/api'
import TransactionForm from '@/features/transaction/TransactionForm'
import TransactionList from '@/features/transaction/TransactionList'
import Summary from '@/ui/common/Summary'
import Icon, { getIconName } from '@/ui/common/icons/Icon'
import TodoList from './TodoList'

interface SessionDetailViewProps {
  sessionId: number
  onBack: () => void
}

export default function SessionDetailView({ sessionId, onBack }: SessionDetailViewProps) {
  const [session, setSession] = useState<any>(null)
  const [transactions, setTransactions] = useState<any[]>([])
  const [categories, setCategories] = useState<any[]>([])
  const [summary, setSummary] = useState({ totalIncome: 0, totalExpense: 0, balance: 0 })
  const [activeTab, setActiveTab] = useState<'all' | 'income' | 'expense'>('all')
  const [showForm, setShowForm] = useState(false)
  const [editingTransaction, setEditingTransaction] = useState<any>(null)

  useEffect(() => {
    loadSession()
    loadTransactions()
    loadCategories()
    loadSummary()
  }, [sessionId])

  const loadSession = async () => {
    try {
      const response = await api.get(`/sessions/${sessionId}`)
      // ApiResponse로 감싸져 있을 수 있으므로 data 필드 확인
      const data = response.data?.data || response.data
      setSession(data)
    } catch (error) {
      console.error('세션 로드 실패:', error)
    }
  }

  const loadTransactions = async () => {
    try {
      const response = await api.get(`/transactions/session/${sessionId}`)
      // ApiResponse로 감싸져 있을 수 있으므로 data 필드 확인
      const data = response.data?.data || response.data
      setTransactions(Array.isArray(data) ? data : [])
    } catch (error) {
      console.error('거래 목록 로드 실패:', error)
      setTransactions([]) // 에러 시 빈 배열로 설정
    }
  }

  const loadCategories = async () => {
    try {
      const response = await api.get('/categories')
      // ApiResponse로 감싸져 있을 수 있으므로 data 필드 확인
      const data = response.data?.data || response.data
      setCategories(Array.isArray(data) ? data : [])
    } catch (error) {
      console.error('카테고리 로드 실패:', error)
      setCategories([]) // 에러 시 빈 배열로 설정
    }
  }

  const loadSummary = async () => {
    try {
      if (session) {
        setSummary({
          totalIncome: Number(session.totalIncome) || 0,
          totalExpense: Number(session.totalExpense) || 0,
          balance: Number(session.balance) || 0
        })
      }
    } catch (error) {
      console.error('요약 정보 로드 실패:', error)
    }
  }

  useEffect(() => {
    if (session) {
      loadSummary()
    }
  }, [session])

  const handleTransactionSaved = () => {
    loadTransactions()
    loadSession()
    setShowForm(false)
    setEditingTransaction(null)
  }

  const handleEdit = (transaction: any) => {
    setEditingTransaction(transaction)
    setShowForm(true)
  }

  const handleDelete = async (id: number) => {
    if (confirm('정말 삭제하시겠습니까?')) {
      try {
        await api.delete(`/transactions/${id}`)
        loadTransactions()
        loadSession()
      } catch (error) {
        console.error('삭제 실패:', error)
        alert('삭제에 실패했습니다.')
      }
    }
  }

  const filteredTransactions = Array.isArray(transactions)
    ? (activeTab === 'all' 
        ? transactions 
        : transactions.filter(t => t?.type === activeTab.toUpperCase()))
    : []

  if (!session) {
    return (
      <div style={{ padding: '40px', marginLeft: '240px' }}>
        <div>로딩 중...</div>
      </div>
    )
  }

  return (
    <div style={{ padding: '40px', marginLeft: '240px' }}>
      {/* 헤더 */}
      <div style={{ marginBottom: '32px' }}>
        <button
          onClick={onBack}
          style={{
            padding: '8px 16px',
            border: 'none',
            background: 'transparent',
            color: '#666',
            cursor: 'pointer',
            marginBottom: '16px',
            fontSize: '14px'
          }}
        >
          ← 대시보드로 돌아가기
        </button>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div style={{
            width: '64px',
            height: '64px',
            borderRadius: '12px',
            backgroundColor: session.color || '#0070f3',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <Icon 
              name={getIconName(session.icon || '💰')} 
              size={32} 
              color="#fff" 
            />
          </div>
          <div>
            <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px', color: '#333' }}>
              {session.name}
            </h1>
            {session.description && (
              <p style={{ fontSize: '16px', color: '#666' }}>{session.description}</p>
            )}
          </div>
        </div>
      </div>

      <Summary summary={summary} />

      {/* To-do 리스트 */}
      <TodoList sessionId={sessionId} />

      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <div className="tabs">
            <button 
              className={`tab ${activeTab === 'all' ? 'active' : ''}`}
              onClick={() => setActiveTab('all')}
            >
              전체
            </button>
            <button 
              className={`tab ${activeTab === 'income' ? 'active' : ''}`}
              onClick={() => setActiveTab('income')}
            >
              수입
            </button>
            <button 
              className={`tab ${activeTab === 'expense' ? 'active' : ''}`}
              onClick={() => setActiveTab('expense')}
            >
              지출
            </button>
          </div>
          <button 
            className="button button-primary"
            onClick={() => {
              setEditingTransaction(null)
              setShowForm(true)
            }}
          >
            + 거래 추가
          </button>
        </div>

        <TransactionList 
          transactions={filteredTransactions}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      </div>

      {showForm && (
        <TransactionForm
          transaction={editingTransaction}
          categories={categories}
          sessionId={sessionId}
          onSave={handleTransactionSaved}
          onCancel={() => {
            setShowForm(false)
            setEditingTransaction(null)
          }}
        />
      )}
    </div>
  )
}
