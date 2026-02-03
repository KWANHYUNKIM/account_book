'use client'

import { useState, useEffect } from 'react'
import api from '@/utils/api'

interface TodoItem {
  id: string
  text: string
  completed: boolean
  createdAt: string
}

interface TodoListProps {
  sessionId: number
}

export default function TodoList({ sessionId }: TodoListProps) {
  const [todos, setTodos] = useState<TodoItem[]>([])
  const [newTodoText, setNewTodoText] = useState('')
  const [isEditing, setIsEditing] = useState<string | null>(null)
  const [editingText, setEditingText] = useState('')

  useEffect(() => {
    loadTodos()
  }, [sessionId])

  const loadTodos = () => {
    // 로컬 스토리지에서 To-do 리스트 로드
    const stored = localStorage.getItem(`todos_${sessionId}`)
    if (stored) {
      setTodos(JSON.parse(stored))
    }
  }

  const saveTodos = (newTodos: TodoItem[]) => {
    setTodos(newTodos)
    localStorage.setItem(`todos_${sessionId}`, JSON.stringify(newTodos))
  }

  const handleAddTodo = () => {
    if (!newTodoText.trim()) return

    const newTodo: TodoItem = {
      id: Date.now().toString(),
      text: newTodoText.trim(),
      completed: false,
      createdAt: new Date().toISOString()
    }

    saveTodos([...todos, newTodo])
    setNewTodoText('')
  }

  const handleToggleTodo = (id: string) => {
    const updatedTodos = todos.map(todo =>
      todo.id === id ? { ...todo, completed: !todo.completed } : todo
    )
    saveTodos(updatedTodos)
  }

  const handleDeleteTodo = (id: string) => {
    const updatedTodos = todos.filter(todo => todo.id !== id)
    saveTodos(updatedTodos)
  }

  const handleStartEdit = (todo: TodoItem) => {
    setIsEditing(todo.id)
    setEditingText(todo.text)
  }

  const handleSaveEdit = (id: string) => {
    if (!editingText.trim()) {
      setIsEditing(null)
      return
    }

    const updatedTodos = todos.map(todo =>
      todo.id === id ? { ...todo, text: editingText.trim() } : todo
    )
    saveTodos(updatedTodos)
    setIsEditing(null)
    setEditingText('')
  }

  const handleCancelEdit = () => {
    setIsEditing(null)
    setEditingText('')
  }

  const handleKeyPress = (e: React.KeyboardEvent, action: () => void) => {
    if (e.key === 'Enter') {
      action()
    } else if (e.key === 'Escape') {
      handleCancelEdit()
    }
  }

  return (
    <div style={{
      backgroundColor: '#fff',
      borderRadius: '8px',
      border: '1px solid #e0e0e0',
      padding: '20px',
      marginBottom: '24px'
    }}>
      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        marginBottom: '16px',
        fontSize: '16px',
        fontWeight: '600',
        color: '#333'
      }}>
        <span>✓</span>
        <span>할 일</span>
        <span style={{
          fontSize: '12px',
          color: '#999',
          fontWeight: '400',
          marginLeft: '8px'
        }}>
          ({todos.filter(t => !t.completed).length}개 남음)
        </span>
      </div>

      {/* To-do 입력 */}
      <div style={{ marginBottom: '16px' }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          padding: '8px 12px',
          border: '1px solid #e0e0e0',
          borderRadius: '6px',
          backgroundColor: '#fafafa',
          transition: 'border-color 0.2s'
        }}>
          <input
            type="text"
            value={newTodoText}
            onChange={(e) => setNewTodoText(e.target.value)}
            onKeyPress={(e) => handleKeyPress(e, handleAddTodo)}
            placeholder="할 일을 입력하고 Enter를 누르세요..."
            style={{
              flex: 1,
              border: 'none',
              outline: 'none',
              backgroundColor: 'transparent',
              fontSize: '14px',
              color: '#333'
            }}
          />
          <button
            onClick={handleAddTodo}
            style={{
              padding: '4px 12px',
              backgroundColor: '#0070f3',
              color: '#fff',
              border: 'none',
              borderRadius: '4px',
              fontSize: '12px',
              cursor: 'pointer',
              fontWeight: '500'
            }}
          >
            추가
          </button>
        </div>
      </div>

      {/* To-do 리스트 */}
      {todos.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {todos.map((todo) => (
            <div
              key={todo.id}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '10px 12px',
                borderRadius: '6px',
                backgroundColor: todo.completed ? '#f5f5f5' : '#fff',
                border: '1px solid #e0e0e0',
                transition: 'all 0.2s'
              }}
            >
              {/* 체크박스 */}
              <input
                type="checkbox"
                checked={todo.completed}
                onChange={() => handleToggleTodo(todo.id)}
                style={{
                  width: '18px',
                  height: '18px',
                  cursor: 'pointer',
                  flexShrink: 0
                }}
              />

              {/* 텍스트 */}
              {isEditing === todo.id ? (
                <input
                  type="text"
                  value={editingText}
                  onChange={(e) => setEditingText(e.target.value)}
                  onKeyPress={(e) => handleKeyPress(e, () => handleSaveEdit(todo.id))}
                  onBlur={() => handleSaveEdit(todo.id)}
                  autoFocus
                  style={{
                    flex: 1,
                    border: '1px solid #0070f3',
                    borderRadius: '4px',
                    padding: '4px 8px',
                    fontSize: '14px',
                    outline: 'none'
                  }}
                />
              ) : (
                <div
                  onClick={() => handleStartEdit(todo)}
                  style={{
                    flex: 1,
                    fontSize: '14px',
                    color: todo.completed ? '#999' : '#333',
                    textDecoration: todo.completed ? 'line-through' : 'none',
                    cursor: 'text',
                    padding: '4px 8px',
                    borderRadius: '4px',
                    transition: 'background-color 0.2s'
                  }}
                  onMouseEnter={(e) => {
                    if (!todo.completed) {
                      e.currentTarget.style.backgroundColor = '#f5f5f5'
                    }
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = 'transparent'
                  }}
                >
                  {todo.text}
                </div>
              )}

              {/* 삭제 버튼 */}
              <button
                onClick={() => handleDeleteTodo(todo.id)}
                style={{
                  padding: '4px 8px',
                  border: 'none',
                  backgroundColor: 'transparent',
                  color: '#dc3545',
                  cursor: 'pointer',
                  fontSize: '12px',
                  borderRadius: '4px',
                  transition: 'background-color 0.2s',
                  flexShrink: 0
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.backgroundColor = '#ffe0e0'
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.backgroundColor = 'transparent'
                }}
              >
                삭제
              </button>
            </div>
          ))}
        </div>
      )}

      {todos.length === 0 && (
        <div style={{
          textAlign: 'center',
          padding: '20px',
          color: '#999',
          fontSize: '14px'
        }}>
          할 일을 추가해보세요
        </div>
      )}
    </div>
  )
}

