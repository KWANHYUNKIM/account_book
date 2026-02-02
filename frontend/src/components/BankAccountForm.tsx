'use client'

import { useState } from 'react'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8100/api'

interface BankAccountFormProps {
  onClose: () => void
  onSuccess: () => void
}

const BANKS = [
  { code: '088', name: '신한은행' },
  { code: '004', name: 'KB국민은행' },
  { code: '020', name: '우리은행' },
  { code: '003', name: '하나은행' },
  { code: '011', name: 'NH농협은행' },
  { code: '088', name: '신한카드' },
  { code: '004', name: 'KB국민카드' },
  { code: '020', name: '하나카드' },
]

export default function BankAccountForm({ onClose, onSuccess }: BankAccountFormProps) {
  const [step, setStep] = useState<'select' | 'auth'>('select')
  const [selectedBank, setSelectedBank] = useState<{ code: string; name: string } | null>(null)
  const [accountName, setAccountName] = useState('')
  const [loading, setLoading] = useState(false)

  const handleBankSelect = async (bank: { code: string; name: string }) => {
    setSelectedBank(bank)
    setStep('auth')
  }

  const handleAuth = async () => {
    if (!selectedBank || !accountName) {
      alert('은행/카드사를 선택하고 계좌 이름을 입력하세요.')
      return
    }

    setLoading(true)
    try {
      // 1. 계좌 등록
      const accountResponse = await api.post('/bank-accounts', {
        accountName: accountName,
        bankCode: selectedBank.code,
        bankName: selectedBank.name,
        accountNumber: '****-****-****', // 인증 후 실제 계좌번호로 업데이트
        accountType: selectedBank.name.includes('카드') ? 'CARD' : 'CHECKING',
        connectionType: selectedBank.name.includes('카드') ? 'CARD_API' : 'OPENBANKING',
        isActive: false
      })

      const accountId = accountResponse.data.id

      // 2. 인증 URL 가져오기
      const connectionType = selectedBank.name.includes('카드') ? 'card' : 'openbanking'
      const authUrlResponse = await api.get(
        `/bank-accounts/${accountId}/${connectionType}/auth-url`,
        {
          params: selectedBank.name.includes('카드') 
            ? { cardCompany: selectedBank.name }
            : { bankCode: selectedBank.code }
        }
      )

      // 3. 새 창에서 인증 페이지 열기
      const authUrl = authUrlResponse.data.authUrl + 
        (authUrlResponse.data.authUrl.includes('?') ? '&' : '?') + 
        `accountId=${accountId}&redirect_uri=${encodeURIComponent(
          `http://localhost:8100/api/oauth/${connectionType}/callback?accountId=${accountId}`
        )}`
      
      const authWindow = window.open(
        authUrl,
        'bankAuth',
        'width=600,height=700,scrollbars=yes'
      )

      // 4. OAuth 콜백 메시지 수신 대기
      const messageHandler = (event: MessageEvent) => {
        if (event.data.type === 'OAUTH_SUCCESS') {
          window.removeEventListener('message', messageHandler)
          if (authWindow) authWindow.close()
          alert('인증이 완료되었습니다! 거래 내역이 자동으로 동기화됩니다.')
          onSuccess()
          onClose()
        } else if (event.data.type === 'OAUTH_ERROR') {
          window.removeEventListener('message', messageHandler)
          if (authWindow) authWindow.close()
          alert('인증 처리에 실패했습니다: ' + event.data.message)
        }
      }

      window.addEventListener('message', messageHandler)

      // 창이 닫혔는지 확인
      const checkClosed = setInterval(() => {
        if (authWindow?.closed) {
          clearInterval(checkClosed)
          window.removeEventListener('message', messageHandler)
        }
      }, 1000)

      // 5분 후 타임아웃
      setTimeout(() => {
        if (authWindow && !authWindow.closed) {
          authWindow.close()
          clearInterval(checkClosed)
          window.removeEventListener('message', messageHandler)
          alert('인증 시간이 초과되었습니다.')
        }
      }, 300000) // 5분

    } catch (error) {
      console.error('인증 시작 실패:', error)
      alert('인증 시작에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  if (step === 'select') {
    return (
      <div className="modal">
        <div className="modal-content">
          <div className="modal-header">
            <h2>자산 연결</h2>
            <button className="close-button" onClick={onClose}>×</button>
          </div>
          
          <div style={{ marginBottom: '20px' }}>
            <label className="label">계좌/카드 이름</label>
            <input
              type="text"
              className="input"
              value={accountName}
              onChange={(e) => setAccountName(e.target.value)}
              placeholder="예: 신한은행 주거래계좌"
            />
          </div>

          <div>
            <label className="label">은행/카드사 선택</label>
            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: 'repeat(2, 1fr)', 
              gap: '10px',
              marginTop: '10px'
            }}>
              {BANKS.map(bank => (
                <button
                  key={bank.code + bank.name}
                  className="button button-secondary"
                  style={{ padding: '15px', textAlign: 'center' }}
                  onClick={() => handleBankSelect(bank)}
                >
                  {bank.name}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h2>인증하기</h2>
          <button className="close-button" onClick={() => setStep('select')}>←</button>
        </div>
        
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <h3 style={{ marginBottom: '20px' }}>{selectedBank?.name} 인증</h3>
          <p style={{ marginBottom: '30px', color: '#666' }}>
            아래 버튼을 클릭하면 {selectedBank?.name} 인증 페이지가 열립니다.
            <br />
            간편인증(공동인증서, OTP 등)을 통해 인증을 완료해주세요.
          </p>
          
          <button
            className="button button-primary"
            onClick={handleAuth}
            disabled={loading}
            style={{ padding: '15px 30px', fontSize: '16px' }}
          >
            {loading ? '인증 중...' : '인증 시작'}
          </button>
          
          <p style={{ marginTop: '20px', fontSize: '12px', color: '#999' }}>
            인증 완료 후 자동으로 거래 내역이 동기화됩니다.
          </p>
        </div>
      </div>
    </div>
  )
}

