'use client'

interface SummaryProps {
  summary: {
    totalIncome: number
    totalExpense: number
    balance: number
  }
}

export default function Summary({ summary }: SummaryProps) {
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(amount)
  }

  return (
    <div className="summary">
      <div className="summary-card">
        <h3>총 수입</h3>
        <div className="amount income">{formatCurrency(summary.totalIncome)}</div>
      </div>
      <div className="summary-card">
        <h3>총 지출</h3>
        <div className="amount expense">{formatCurrency(summary.totalExpense)}</div>
      </div>
      <div className="summary-card">
        <h3>잔액</h3>
        <div className={`amount ${summary.balance >= 0 ? 'balance' : 'expense'}`}>
          {formatCurrency(summary.balance)}
        </div>
      </div>
    </div>
  )
}

