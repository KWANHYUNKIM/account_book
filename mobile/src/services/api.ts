import axios from 'axios';
import {Transaction, Category, Summary} from '../types/Transaction';

const API_BASE_URL = 'http://localhost:8100/api';

// Android 에뮬레이터의 경우 localhost 대신 10.0.2.2 사용
// iOS 시뮬레이터는 localhost 사용 가능
// 실제 기기에서는 컴퓨터의 IP 주소 사용 필요
const getApiBaseUrl = () => {
  // 개발 환경에 따라 변경 가능
  if (__DEV__) {
    // Android 에뮬레이터
    // return 'http://10.0.2.2:8100/api';
    // iOS 시뮬레이터 또는 실제 기기
    return 'http://localhost:8100/api';
  }
  return API_BASE_URL;
};

const api = axios.create({
  baseURL: getApiBaseUrl(),
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const transactionApi = {
  getAll: async (): Promise<Transaction[]> => {
    const response = await api.get<Transaction[]>('/transactions');
    return response.data;
  },

  getById: async (id: number): Promise<Transaction> => {
    const response = await api.get<Transaction>(`/transactions/${id}`);
    return response.data;
  },

  getByType: async (type: 'INCOME' | 'EXPENSE'): Promise<Transaction[]> => {
    const response = await api.get<Transaction[]>(`/transactions/type/${type}`);
    return response.data;
  },

  create: async (transaction: Omit<Transaction, 'id' | 'createdAt'>): Promise<Transaction> => {
    const response = await api.post<Transaction>('/transactions', transaction);
    return response.data;
  },

  update: async (id: number, transaction: Partial<Transaction>): Promise<Transaction> => {
    const response = await api.put<Transaction>(`/transactions/${id}`, transaction);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/transactions/${id}`);
  },

  getSummary: async (): Promise<Summary> => {
    const response = await api.get<Summary>('/transactions/summary');
    return response.data;
  },
};

export const categoryApi = {
  getAll: async (): Promise<Category[]> => {
    const response = await api.get<Category[]>('/categories');
    return response.data;
  },

  getByType: async (type: 'INCOME' | 'EXPENSE'): Promise<Category[]> => {
    const response = await api.get<Category[]>(`/categories/type/${type}`);
    return response.data;
  },

  create: async (category: Omit<Category, 'id'>): Promise<Category> => {
    const response = await api.post<Category>('/categories', category);
    return response.data;
  },

  update: async (id: number, category: Partial<Category>): Promise<Category> => {
    const response = await api.put<Category>(`/categories/${id}`, category);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/categories/${id}`);
  },
};

