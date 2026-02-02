'use client'

import { useState } from 'react'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8100/api'

interface RegisterFormProps {
  onRegister: (token: string, email: string, name: string) => void
  onSwitchToLogin: () => void
}

export default function RegisterForm({ onRegister, onSwitchToLogin }: RegisterFormProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await axios.post(`${API_BASE_URL}/auth/register`, {
        email,
        password,
        name
      })

      if (response.data.token) {
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('userEmail', response.data.email)
        localStorage.setItem('userName', response.data.name)
        onRegister(response.data.token, response.data.email, response.data.name)
      } else {
        setError(response.data.message || '회원가입에 실패했습니다.')
      }
    } catch (error: any) {
      setError(error.response?.data?.message || '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal">
      <div className="modal-content">
        <div className="modal-header">
          <h2>회원가입</h2>
        </div>
        
        <form onSubmit={handleSubmit}>
          {error && (
            <div style={{ 
              padding: '10px', 
              marginBottom: '15px', 
              backgroundColor: '#fee', 
              color: '#c33',
              borderRadius: '4px'
            }}>
              {error}
            </div>
          )}

          <div className="form-group">
            <label className="label">이름</label>
            <input
              type="text"
              className="input"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="이름을 입력하세요"
              required
            />
          </div>

          <div className="form-group">
            <label className="label">이메일</label>
            <input
              type="email"
              className="input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="이메일을 입력하세요"
              required
            />
          </div>

          <div className="form-group">
            <label className="label">비밀번호</label>
            <input
              type="password"
              className="input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요 (최소 6자)"
              required
              minLength={6}
            />
          </div>

          <button
            type="submit"
            className="button button-primary"
            style={{ width: '100%', marginTop: '10px' }}
            disabled={loading}
          >
            {loading ? '가입 중...' : '회원가입'}
          </button>

          <div style={{ textAlign: 'center', marginTop: '20px' }}>
            <button
              type="button"
              onClick={onSwitchToLogin}
              style={{ 
                background: 'none', 
                border: 'none', 
                color: '#0070f3', 
                cursor: 'pointer',
                textDecoration: 'underline'
              }}
            >
              이미 계정이 있으신가요? 로그인
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

