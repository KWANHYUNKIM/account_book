export interface Transaction {
  id?: number;
  type: 'INCOME' | 'EXPENSE';
  amount: number;
  description: string;
  categoryId?: number;
  categoryName?: string;
  transactionDate: string;
  createdAt?: string;
}

export interface Category {
  id: number;
  name: string;
  type: 'INCOME' | 'EXPENSE';
  description?: string;
}

export interface Summary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
}


