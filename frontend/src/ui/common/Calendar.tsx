'use client'

import { useState, useMemo } from 'react'

interface CalendarProps {
  onDateClick?: (date: Date) => void
  markedDates?: Date[] // 표시할 날짜들
}

// 한국 공휴일 데이터 (2024-2025년)
const getHolidays = (year: number): Date[] => {
  const holidays: Date[] = []
  
  // 시간을 0시 0분 0초로 설정하여 날짜만 비교
  const createDate = (y: number, m: number, d: number) => {
    const date = new Date(y, m, d)
    date.setHours(0, 0, 0, 0)
    return date
  }
  
  // 고정 공휴일
  holidays.push(createDate(year, 0, 1)) // 신정
  holidays.push(createDate(year, 2, 1)) // 삼일절
  holidays.push(createDate(year, 4, 5)) // 어린이날
  holidays.push(createDate(year, 5, 6)) // 현충일
  holidays.push(createDate(year, 7, 15)) // 광복절
  holidays.push(createDate(year, 9, 3)) // 개천절
  holidays.push(createDate(year, 9, 9)) // 한글날
  holidays.push(createDate(year, 11, 25)) // 크리스마스
  
  // 음력 공휴일 (매년 달라짐)
  if (year === 2024) {
    // 2024년
    holidays.push(createDate(2024, 1, 9)) // 설날 연휴
    holidays.push(createDate(2024, 1, 10)) // 설날
    holidays.push(createDate(2024, 1, 11)) // 설날 연휴
    holidays.push(createDate(2024, 1, 12)) // 설날 대체공휴일
    holidays.push(createDate(2024, 4, 15)) // 부처님오신날
    holidays.push(createDate(2024, 8, 16)) // 추석 연휴
    holidays.push(createDate(2024, 8, 17)) // 추석
    holidays.push(createDate(2024, 8, 18)) // 추석 연휴
  } else if (year === 2025) {
    // 2025년
    holidays.push(createDate(2025, 0, 28)) // 설날 연휴
    holidays.push(createDate(2025, 0, 29)) // 설날
    holidays.push(createDate(2025, 0, 30)) // 설날 연휴
    holidays.push(createDate(2025, 4, 5)) // 부처님오신날
    holidays.push(createDate(2025, 9, 5)) // 추석 연휴
    holidays.push(createDate(2025, 9, 6)) // 추석
    holidays.push(createDate(2025, 9, 7)) // 추석 연휴
    holidays.push(createDate(2025, 9, 8)) // 추석 대체공휴일
  }
  
  return holidays
}

export default function Calendar({ onDateClick, markedDates = [] }: CalendarProps) {
  const [currentDate, setCurrentDate] = useState(new Date())
  
  const year = currentDate.getFullYear()
  const month = currentDate.getMonth()
  
  // 공휴일 목록
  const holidays = useMemo(() => getHolidays(year), [year])
  
  // 공휴일 이름 매핑
  const getHolidayName = (date: Date): string | null => {
    const month = date.getMonth()
    const day = date.getDate()
    
    if (month === 0 && day === 1) return '신정'
    if (month === 2 && day === 1) return '삼일절'
    if (month === 4 && day === 5) return '어린이날'
    if (month === 5 && day === 6) return '현충일'
    if (month === 7 && day === 15) return '광복절'
    if (month === 9 && day === 3) return '개천절'
    if (month === 9 && day === 9) return '한글날'
    if (month === 11 && day === 25) return '크리스마스'
    
    // 음력 공휴일 (년도별로 다름)
    if (year === 2024) {
      if (month === 1 && day === 10) return '설날'
      if (month === 4 && day === 15) return '부처님오신날'
      if (month === 8 && day === 17) return '추석'
    } else if (year === 2025) {
      if (month === 0 && day === 29) return '설날'
      if (month === 4 && day === 5) return '부처님오신날'
      if (month === 9 && day === 6) return '추석'
    }
    
    return null
  }
  
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const daysInMonth = lastDay.getDate()
  const startingDayOfWeek = firstDay.getDay()
  
  const monthNames = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
  const dayNames = ['일', '월', '화', '수', '목', '금', '토']
  
  const prevMonth = () => {
    setCurrentDate(new Date(year, month - 1, 1))
  }
  
  const nextMonth = () => {
    setCurrentDate(new Date(year, month + 1, 1))
  }
  
  const goToToday = () => {
    setCurrentDate(new Date())
  }
  
  const isMarked = (day: number) => {
    const date = new Date(year, month, day)
    return markedDates.some(markedDate => 
      markedDate.getFullYear() === date.getFullYear() &&
      markedDate.getMonth() === date.getMonth() &&
      markedDate.getDate() === date.getDate()
    )
  }
  
  const isHoliday = (day: number) => {
    const date = new Date(year, month, day)
    date.setHours(0, 0, 0, 0)
    
    return holidays.some(holiday => {
      const holidayDate = new Date(holiday)
      holidayDate.setHours(0, 0, 0, 0)
      return date.getTime() === holidayDate.getTime()
    })
  }
  
  const getHolidayNameForDay = (day: number): string | null => {
    const date = new Date(year, month, day)
    return getHolidayName(date)
  }
  
  const isToday = (day: number) => {
    const today = new Date()
    return (
      year === today.getFullYear() &&
      month === today.getMonth() &&
      day === today.getDate()
    )
  }
  
  const handleDateClick = (day: number) => {
    const date = new Date(year, month, day)
    if (onDateClick) {
      onDateClick(date)
    }
  }
  
  const renderDays = () => {
    const days = []
    
    // 빈 칸 (이전 달의 마지막 날들)
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(
        <div key={`empty-${i}`} style={{ width: '14.28%', aspectRatio: '1', padding: '4px' }}>
          <div style={{
            width: '100%',
            height: '100%',
            borderRadius: '8px',
            backgroundColor: '#f5f5f5'
          }} />
        </div>
      )
    }
    
    // 이번 달의 날짜들
    for (let day = 1; day <= daysInMonth; day++) {
      const marked = isMarked(day)
      const today = isToday(day)
      const holiday = isHoliday(day)
      const holidayName = getHolidayNameForDay(day)
      
      days.push(
        <div
          key={day}
          onClick={() => handleDateClick(day)}
          style={{
            width: '14.28%',
            aspectRatio: '1',
            padding: '4px',
            cursor: onDateClick ? 'pointer' : 'default',
            position: 'relative'
          }}
          title={holidayName || undefined}
        >
          <div
            style={{
              width: '100%',
              height: '100%',
              borderRadius: '8px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: today || holiday ? '13px' : '14px',
              fontWeight: today || holiday ? '600' : '400',
              backgroundColor: today 
                ? '#0070f3' 
                : holiday 
                  ? '#fff3e0' 
                  : marked 
                    ? '#e3f2fd' 
                    : 'transparent',
              color: today 
                ? '#fff' 
                : holiday 
                  ? '#f57c00' 
                  : marked 
                    ? '#0070f3' 
                    : '#333',
              border: today 
                ? '2px solid #0070f3' 
                : holiday 
                  ? '1px solid #ff9800' 
                  : marked 
                    ? '1px solid #0070f3' 
                    : '1px solid transparent',
              transition: 'all 0.2s'
            }}
            onMouseEnter={(e) => {
              if (onDateClick && !today && !holiday && !marked) {
                e.currentTarget.style.backgroundColor = '#f5f5f5'
              }
            }}
            onMouseLeave={(e) => {
              if (onDateClick && !today && !holiday && !marked) {
                e.currentTarget.style.backgroundColor = 'transparent'
              }
            }}
          >
            <span>{day}</span>
            {holiday && (
              <span style={{
                fontSize: '8px',
                color: '#f57c00',
                marginTop: '2px',
                lineHeight: '1'
              }}>
                ●
              </span>
            )}
          </div>
        </div>
      )
    }
    
    return days
  }
  
  return (
    <div style={{
      backgroundColor: '#fff',
      borderRadius: '12px',
      border: '1px solid #e0e0e0',
      padding: '20px',
      width: '100%'
    }}>
      {/* 헤더 */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <button
            onClick={prevMonth}
            style={{
              padding: '8px 12px',
              border: '1px solid #e0e0e0',
              borderRadius: '6px',
              backgroundColor: '#fff',
              cursor: 'pointer',
              fontSize: '14px',
              color: '#666',
              transition: 'all 0.2s'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#f5f5f5'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = '#fff'
            }}
          >
            ←
          </button>
          <h3 style={{
            fontSize: '18px',
            fontWeight: '600',
            color: '#333',
            margin: 0,
            minWidth: '120px',
            textAlign: 'center'
          }}>
            {year}년 {monthNames[month]}
          </h3>
          <button
            onClick={nextMonth}
            style={{
              padding: '8px 12px',
              border: '1px solid #e0e0e0',
              borderRadius: '6px',
              backgroundColor: '#fff',
              cursor: 'pointer',
              fontSize: '14px',
              color: '#666',
              transition: 'all 0.2s'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#f5f5f5'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = '#fff'
            }}
          >
            →
          </button>
        </div>
        <button
          onClick={goToToday}
          style={{
            padding: '8px 16px',
            border: '1px solid #0070f3',
            borderRadius: '6px',
            backgroundColor: '#fff',
            cursor: 'pointer',
            fontSize: '14px',
            color: '#0070f3',
            fontWeight: '500',
            transition: 'all 0.2s'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = '#f0f7ff'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = '#fff'
          }}
        >
          오늘
        </button>
      </div>
      
      {/* 요일 헤더 */}
      <div style={{
        display: 'flex',
        marginBottom: '8px'
      }}>
        {dayNames.map((day, index) => (
          <div
            key={day}
            style={{
              width: '14.28%',
              textAlign: 'center',
              fontSize: '12px',
              fontWeight: '600',
              color: index === 0 ? '#dc3545' : index === 6 ? '#0070f3' : '#666',
              padding: '8px 4px'
            }}
          >
            {day}
          </div>
        ))}
      </div>
      
      {/* 날짜 그리드 */}
      <div style={{
        display: 'flex',
        flexWrap: 'wrap'
      }}>
        {renderDays()}
      </div>
      
      {/* 범례 */}
      <div style={{
        marginTop: '16px',
        paddingTop: '16px',
        borderTop: '1px solid #e0e0e0',
        display: 'flex',
        flexWrap: 'wrap',
        gap: '16px',
        fontSize: '12px',
        color: '#666'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <div style={{
            width: '16px',
            height: '16px',
            borderRadius: '4px',
            backgroundColor: '#0070f3',
            border: '2px solid #0070f3'
          }} />
          <span>오늘</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <div style={{
            width: '16px',
            height: '16px',
            borderRadius: '4px',
            backgroundColor: '#fff3e0',
            border: '1px solid #ff9800'
          }} />
          <span>공휴일</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <div style={{
            width: '16px',
            height: '16px',
            borderRadius: '4px',
            backgroundColor: '#e3f2fd',
            border: '1px solid #0070f3'
          }} />
          <span>거래 있는 날</span>
        </div>
      </div>
    </div>
  )
}

