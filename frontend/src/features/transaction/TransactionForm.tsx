'use client'

import { useState, useEffect } from 'react'
import api from '@/utils/api'

interface TransactionFormProps {
  transaction?: any
  categories: any[]
  sessionId?: number
  onSave: () => void
  onCancel: () => void
}

export default function TransactionForm({ transaction, categories, sessionId, onSave, onCancel }: TransactionFormProps) {
  const [formData, setFormData] = useState({
    type: 'INCOME',
    amount: '',
    description: '',
    categoryId: '',
    transactionDate: new Date().toISOString().split('T')[0]
  })

  useEffect(() => {
    if (transaction) {
      setFormData({
        type: transaction.type,
        amount: transaction.amount.toString(),
        description: transaction.description || '',
        categoryId: transaction.categoryId?.toString() || '',
        transactionDate: transaction.transactionDate 
          ? transaction.transactionDate.split('T')[0] 
          : new Date().toISOString().split('T')[0]
      })
    }
  }, [transaction])

  const filteredCategories = categories.filter(c => c.type === formData.type)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    try {
      const payload = {
        type: formData.type,
        amount: parseFloat(formData.amount),
        description: formData.description,
        categoryId: formData.categoryId ? parseInt(formData.categoryId) : null,
        sessionId: sessionId || null,
        transactionDate: formData.transactionDate + 'T00:00:00'
      }

      if (transaction) {
        await api.put(`/transactions/${transaction.id}`, payload)
      } else {
        await api.post('/transactions', payload)
      }
      
      onSave()
    } catch (error) {
      console.error('저장 실패:', error)
      alert('저장에 실패했습니다.')
    }
  }

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h2>{transaction ? '거래 수정' : '거래 추가'}</h2>
          <button className="close-button" onClick={onCancel}>×</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="label">유형</label>
            <select
              className="select"
              value={formData.type}
              onChange={(e) => {
                setFormData({ ...formData, type: e.target.value, categoryId: '' })
              }}
              required
            >
              <option value="INCOME">수입</option>
              <option value="EXPENSE">지출</option>
            </select>
          </div>

          <div className="form-group">
            <label className="label">금액</label>
            <input
              type="number"
              className="input"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              placeholder="금액을 입력하세요"
              required
              min="0"
              step="0.01"
            />
          </div>

          <div className="form-group">
            <label className="label">카테고리</label>
            <select
              className="select"
              value={formData.categoryId}
              onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
            >
              <option value="">선택 안함</option>
              {filteredCategories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="label">설명</label>
            <input
              type="text"
              className="input"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="설명을 입력하세요"
            />
          </div>

          <div className="form-group">
            <label className="label">날짜</label>
            <input
              type="date"
              className="input"
              value={formData.transactionDate}
              onChange={(e) => setFormData({ ...formData, transactionDate: e.target.value })}
              required
            />
          </div>

          <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '20px' }}>
            <button type="button" className="button button-secondary" onClick={onCancel}>
              취소
            </button>
            <button type="submit" className="button button-primary">
              {transaction ? '수정' : '추가'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
