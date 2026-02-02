'use client'

interface TransactionListProps {
  transactions: any[]
  onEdit: (transaction: any) => void
  onDelete: (id: number) => void
}

export default function TransactionList({ transactions, onEdit, onDelete }: TransactionListProps) {
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(amount)
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  }

  if (transactions.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
        거래 내역이 없습니다.
      </div>
    )
  }

  return (
    <table className="table">
      <thead>
        <tr>
          <th>날짜</th>
          <th>유형</th>
          <th>카테고리</th>
          <th>설명</th>
          <th style={{ textAlign: 'right' }}>금액</th>
          <th style={{ textAlign: 'center' }}>작업</th>
        </tr>
      </thead>
      <tbody>
        {transactions.map(transaction => (
          <tr key={transaction.id}>
            <td>{formatDate(transaction.transactionDate)}</td>
            <td>
              <span className={transaction.type === 'INCOME' ? 'income' : 'expense'}>
                {transaction.type === 'INCOME' ? '수입' : '지출'}
              </span>
            </td>
            <td>{transaction.categoryName || '-'}</td>
            <td>{transaction.description || '-'}</td>
            <td style={{ textAlign: 'right' }}>
              <span className={transaction.type === 'INCOME' ? 'income' : 'expense'}>
                {transaction.type === 'INCOME' ? '+' : '-'}
                {formatCurrency(transaction.amount)}
              </span>
            </td>
            <td style={{ textAlign: 'center' }}>
              <button
                className="button button-secondary"
                style={{ marginRight: '5px', padding: '5px 10px', fontSize: '14px' }}
                onClick={() => onEdit(transaction)}
              >
                수정
              </button>
              <button
                className="button button-danger"
                style={{ padding: '5px 10px', fontSize: '14px' }}
                onClick={() => onDelete(transaction.id)}
              >
                삭제
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

