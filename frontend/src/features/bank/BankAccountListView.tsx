'use client'

import { useState, useEffect } from 'react'
import BankAccountList from './BankAccountList'
import BankAccountForm from './BankAccountForm'

export default function BankAccountListView() {
  const [showBankForm, setShowBankForm] = useState(false)

  return (
    <div style={{ padding: '40px', marginLeft: '240px' }}>
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px', color: '#333' }}>
          자산 연결
        </h1>
        <p style={{ fontSize: '16px', color: '#666' }}>
          은행 계좌와 카드를 연결하여 거래 내역을 자동으로 가져오세요
        </p>
      </div>

      <div className="card" style={{ marginBottom: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={{ margin: 0 }}>연결된 자산</h2>
          <button
            className="button button-primary"
            onClick={() => setShowBankForm(true)}
          >
            + 자산 연결
          </button>
        </div>
      </div>
      
      <BankAccountList />

      {showBankForm && (
        <BankAccountForm
          onClose={() => setShowBankForm(false)}
          onSuccess={() => {
            setShowBankForm(false)
            window.location.reload()
          }}
        />
      )}
    </div>
  )
}

