'use client'

import { useState, useEffect } from 'react'
import api from '@/utils/api'

interface BankAccount {
  id: number
  accountName: string
  bankName: string
  accountNumber: string
  accountType: string
  connectionType: string
  isActive: boolean
  lastSyncedAt: string | null
}

export default function BankAccountList() {
  const [accounts, setAccounts] = useState<BankAccount[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadAccounts()
  }, [])

  const loadAccounts = async () => {
    try {
      const response = await api.get('/bank-accounts')
      setAccounts(response.data)
    } catch (error) {
      console.error('계좌 목록 로드 실패:', error)
    }
  }

  const handleSync = async (accountId: number, connectionType: string) => {
    setLoading(true)
    try {
      const endpoint = connectionType === 'OPENBANKING' 
        ? `/bank-accounts/${accountId}/openbanking/sync`
        : `/bank-accounts/${accountId}/card/sync`
      
      await api.post(endpoint)
      alert('동기화가 완료되었습니다.')
      loadAccounts()
    } catch (error) {
      console.error('동기화 실패:', error)
      alert('동기화에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (accountId: number) => {
    if (confirm('정말 연결을 해제하시겠습니까?')) {
      try {
        await api.delete(`/bank-accounts/${accountId}`)
        loadAccounts()
      } catch (error) {
        console.error('삭제 실패:', error)
        alert('삭제에 실패했습니다.')
      }
    }
  }

  const formatDate = (dateString: string | null) => {
    if (!dateString) return '동기화 안됨'
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return (
    <div className="card">
      <h2 style={{ marginBottom: '20px' }}>연결된 자산</h2>
      
      {accounts.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          연결된 계좌나 카드가 없습니다.
          <br />
          아래 "자산 연결" 버튼을 눌러 추가하세요.
        </div>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>이름</th>
              <th>은행/카드사</th>
              <th>계좌번호</th>
              <th>연동 유형</th>
              <th>마지막 동기화</th>
              <th style={{ textAlign: 'center' }}>작업</th>
            </tr>
          </thead>
          <tbody>
            {accounts.map(account => (
              <tr key={account.id}>
                <td>{account.accountName}</td>
                <td>{account.bankName}</td>
                <td>{account.accountNumber}</td>
                <td>
                  <span style={{
                    padding: '4px 8px',
                    borderRadius: '4px',
                    fontSize: '12px',
                    backgroundColor: account.connectionType === 'OPENBANKING' ? '#e3f2fd' : '#fff3e0',
                    color: account.connectionType === 'OPENBANKING' ? '#1976d2' : '#f57c00'
                  }}>
                    {account.connectionType === 'OPENBANKING' ? '오픈뱅킹' : '카드사 API'}
                  </span>
                </td>
                <td>{formatDate(account.lastSyncedAt)}</td>
                <td style={{ textAlign: 'center' }}>
                  <button
                    className="button button-primary"
                    style={{ marginRight: '5px', padding: '5px 10px', fontSize: '14px' }}
                    onClick={() => handleSync(account.id, account.connectionType)}
                    disabled={loading}
                  >
                    동기화
                  </button>
                  <button
                    className="button button-danger"
                    style={{ padding: '5px 10px', fontSize: '14px' }}
                    onClick={() => handleDelete(account.id)}
                  >
                    해제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
