import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  RefreshControl,
  Alert,
} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {NativeStackNavigationProp} from '@react-navigation/native-stack';
import {RootStackParamList} from '../App';
import {transactionApi} from '../services/api';
import {Transaction} from '../types/Transaction';

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

const TransactionListScreen: React.FC = () => {
  const navigation = useNavigation<NavigationProp>();
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [filter, setFilter] = useState<'ALL' | 'INCOME' | 'EXPENSE'>('ALL');
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadTransactions();
  }, [filter]);

  const loadTransactions = async () => {
    try {
      let data: Transaction[];
      if (filter === 'ALL') {
        data = await transactionApi.getAll();
      } else {
        data = await transactionApi.getByType(filter);
      }
      // 날짜순 정렬 (최신순)
      data.sort((a, b) => {
        const dateA = new Date(a.transactionDate).getTime();
        const dateB = new Date(b.transactionDate).getTime();
        return dateB - dateA;
      });
      setTransactions(data);
    } catch (error) {
      console.error('거래 목록 로드 실패:', error);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadTransactions();
    setRefreshing(false);
  };

  const handleDelete = async (id: number) => {
    Alert.alert('삭제 확인', '정말 삭제하시겠습니까?', [
      {text: '취소', style: 'cancel'},
      {
        text: '삭제',
        style: 'destructive',
        onPress: async () => {
          try {
            await transactionApi.delete(id);
            loadTransactions();
          } catch (error) {
            console.error('삭제 실패:', error);
            Alert.alert('오류', '삭제에 실패했습니다.');
          }
        },
      },
    ]);
  };

  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW',
    }).format(amount);
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  const renderItem = ({item}: {item: Transaction}) => (
    <TouchableOpacity
      style={styles.transactionItem}
      onPress={() =>
        navigation.navigate('TransactionForm', {transaction: item})
      }>
      <View style={styles.transactionContent}>
        <View style={styles.transactionHeader}>
          <Text style={styles.transactionDate}>{formatDate(item.transactionDate)}</Text>
          <Text
            style={[
              styles.transactionType,
              item.type === 'INCOME' ? styles.incomeType : styles.expenseType,
            ]}>
            {item.type === 'INCOME' ? '수입' : '지출'}
          </Text>
        </View>
        {item.categoryName && (
          <Text style={styles.categoryName}>{item.categoryName}</Text>
        )}
        {item.description && (
          <Text style={styles.description}>{item.description}</Text>
        )}
        <Text
          style={[
            styles.amount,
            item.type === 'INCOME' ? styles.incomeAmount : styles.expenseAmount,
          ]}>
          {item.type === 'INCOME' ? '+' : '-'}
          {formatCurrency(item.amount)}
        </Text>
      </View>
      <TouchableOpacity
        style={styles.deleteButton}
        onPress={() => item.id && handleDelete(item.id)}>
        <Text style={styles.deleteButtonText}>삭제</Text>
      </TouchableOpacity>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.filterContainer}>
        <TouchableOpacity
          style={[styles.filterButton, filter === 'ALL' && styles.filterButtonActive]}
          onPress={() => setFilter('ALL')}>
          <Text
            style={[
              styles.filterButtonText,
              filter === 'ALL' && styles.filterButtonTextActive,
            ]}>
            전체
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.filterButton, filter === 'INCOME' && styles.filterButtonActive]}
          onPress={() => setFilter('INCOME')}>
          <Text
            style={[
              styles.filterButtonText,
              filter === 'INCOME' && styles.filterButtonTextActive,
            ]}>
            수입
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.filterButton, filter === 'EXPENSE' && styles.filterButtonActive]}
          onPress={() => setFilter('EXPENSE')}>
          <Text
            style={[
              styles.filterButtonText,
              filter === 'EXPENSE' && styles.filterButtonTextActive,
            ]}>
            지출
          </Text>
        </TouchableOpacity>
      </View>

      {transactions.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>거래 내역이 없습니다.</Text>
        </View>
      ) : (
        <FlatList
          data={transactions}
          renderItem={renderItem}
          keyExtractor={item => item.id?.toString() || Math.random().toString()}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
          contentContainerStyle={styles.listContent}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  filterContainer: {
    flexDirection: 'row',
    padding: 16,
    gap: 8,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  filterButton: {
    flex: 1,
    padding: 10,
    borderRadius: 8,
    backgroundColor: '#f0f0f0',
    alignItems: 'center',
  },
  filterButtonActive: {
    backgroundColor: '#0070f3',
  },
  filterButtonText: {
    fontSize: 14,
    color: '#666',
    fontWeight: '600',
  },
  filterButtonTextActive: {
    color: '#fff',
  },
  listContent: {
    padding: 16,
  },
  transactionItem: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 16,
    marginBottom: 12,
    flexDirection: 'row',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  transactionContent: {
    flex: 1,
  },
  transactionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  transactionDate: {
    fontSize: 14,
    color: '#666',
  },
  transactionType: {
    fontSize: 12,
    fontWeight: '600',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
  },
  incomeType: {
    backgroundColor: '#d4edda',
    color: '#28a745',
  },
  expenseType: {
    backgroundColor: '#f8d7da',
    color: '#dc3545',
  },
  categoryName: {
    fontSize: 14,
    color: '#333',
    marginBottom: 4,
    fontWeight: '500',
  },
  description: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  amount: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  incomeAmount: {
    color: '#28a745',
  },
  expenseAmount: {
    color: '#dc3545',
  },
  deleteButton: {
    justifyContent: 'center',
    paddingLeft: 16,
  },
  deleteButtonText: {
    color: '#dc3545',
    fontSize: 14,
    fontWeight: '600',
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 16,
    color: '#666',
  },
});

export default TransactionListScreen;

