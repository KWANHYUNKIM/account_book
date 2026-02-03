'use client'

import { useState } from 'react'
import api from '@/utils/api'
import Icon, { availableIcons, getIconName } from '@/ui/common/icons/Icon'

interface SessionFormProps {
  onClose: () => void
  onSuccess: () => void
  session?: any
}

const COLORS = [
  { value: '#0070f3', name: '파랑' },
  { value: '#28a745', name: '초록' },
  { value: '#ff6b9d', name: '핑크' },
  { value: '#ffc107', name: '노랑' },
  { value: '#6f42c1', name: '보라' },
  { value: '#fd7e14', name: '주황' },
]

// 이전 이모지 아이콘을 지원하기 위한 기본값
const DEFAULT_ICON = 'money'

export default function SessionForm({ onClose, onSuccess, session }: SessionFormProps) {
  const [formData, setFormData] = useState({
    name: session?.name || '',
    description: session?.description || '',
    color: session?.color || '#0070f3',
    icon: session?.icon ? getIconName(session.icon) : DEFAULT_ICON
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!formData.name.trim()) {
      alert('기록 이름을 입력해주세요.')
      return
    }

    setLoading(true)
    try {
      if (session) {
        await api.put(`/sessions/${session.id}`, formData)
      } else {
        await api.post('/sessions', formData)
      }
      onSuccess()
    } catch (error) {
      console.error('저장 실패:', error)
      alert('저장에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal">
      <div className="modal-content" style={{ maxWidth: '600px' }}>
        <div className="modal-header">
          <h2>{session ? '기록 수정' : '새 기록 만들기'}</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="label">기록 이름</label>
            <input
              type="text"
              className="input"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="예: 2024년 가계부, 여행 예산 등"
              required
            />
          </div>

          <div className="form-group">
            <label className="label">설명 (선택사항)</label>
            <input
              type="text"
              className="input"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="기록에 대한 간단한 설명"
            />
          </div>

          <div className="form-group">
            <label className="label">색상</label>
            <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
              {COLORS.map(color => (
                <button
                  key={color.value}
                  type="button"
                  onClick={() => setFormData({ ...formData, color: color.value })}
                  style={{
                    width: '40px',
                    height: '40px',
                    borderRadius: '8px',
                    backgroundColor: color.value,
                    border: formData.color === color.value ? '3px solid #333' : '2px solid #ddd',
                    cursor: 'pointer'
                  }}
                  title={color.name}
                />
              ))}
            </div>
          </div>

          <div className="form-group">
            <label className="label">아이콘</label>
            <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
              {availableIcons.map(iconName => (
                <button
                  key={iconName}
                  type="button"
                  onClick={() => setFormData({ ...formData, icon: iconName })}
                  style={{
                    width: '48px',
                    height: '48px',
                    borderRadius: '8px',
                    backgroundColor: formData.icon === iconName ? formData.color : '#f0f0f0',
                    border: formData.icon === iconName ? '2px solid #333' : '2px solid #ddd',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}
                >
                  <Icon 
                    name={iconName} 
                    size={24} 
                    color={formData.icon === iconName ? '#fff' : '#666'} 
                  />
                </button>
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '20px' }}>
            <button type="button" className="button button-secondary" onClick={onClose}>
              취소
            </button>
            <button type="submit" className="button button-primary" disabled={loading}>
              {loading ? '저장 중...' : session ? '수정' : '생성'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
