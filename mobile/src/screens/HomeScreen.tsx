import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  RefreshControl,
} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {NativeStackNavigationProp} from '@react-navigation/native-stack';
import {RootStackParamList} from '../App';
import {transactionApi} from '../services/api';
import {Summary} from '../types/Transaction';

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

const HomeScreen: React.FC = () => {
  const navigation = useNavigation<NavigationProp>();
  const [summary, setSummary] = useState<Summary>({
    totalIncome: 0,
    totalExpense: 0,
    balance: 0,
  });
  const [refreshing, setRefreshing] = useState(false);

  const loadSummary = async () => {
    try {
      const data = await transactionApi.getSummary();
      setSummary(data);
    } catch (error) {
      console.error('요약 정보 로드 실패:', error);
    }
  };

  useEffect(() => {
    loadSummary();
  }, []);

  const onRefresh = async () => {
    setRefreshing(true);
    await loadSummary();
    setRefreshing(false);
  };

  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW',
    }).format(amount);
  };

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }>
      <View style={styles.summaryContainer}>
        <View style={[styles.summaryCard, styles.incomeCard]}>
          <Text style={styles.summaryLabel}>총 수입</Text>
          <Text style={[styles.summaryAmount, styles.incomeAmount]}>
            {formatCurrency(summary.totalIncome)}
          </Text>
        </View>

        <View style={[styles.summaryCard, styles.expenseCard]}>
          <Text style={styles.summaryLabel}>총 지출</Text>
          <Text style={[styles.summaryAmount, styles.expenseAmount]}>
            {formatCurrency(summary.totalExpense)}
          </Text>
        </View>

        <View
          style={[
            styles.summaryCard,
            summary.balance >= 0 ? styles.balanceCard : styles.negativeCard,
          ]}>
          <Text style={styles.summaryLabel}>잔액</Text>
          <Text
            style={[
              styles.summaryAmount,
              summary.balance >= 0 ? styles.balanceAmount : styles.negativeAmount,
            ]}>
            {formatCurrency(summary.balance)}
          </Text>
        </View>
      </View>

      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[styles.button, styles.incomeButton]}
          onPress={() =>
            navigation.navigate('TransactionForm', {
              transaction: {type: 'INCOME', amount: 0, description: '', transactionDate: new Date().toISOString()},
            })
          }>
          <Text style={styles.buttonText}>+ 수입 추가</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.expenseButton]}
          onPress={() =>
            navigation.navigate('TransactionForm', {
              transaction: {type: 'EXPENSE', amount: 0, description: '', transactionDate: new Date().toISOString()},
            })
          }>
          <Text style={styles.buttonText}>- 지출 추가</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  summaryContainer: {
    padding: 16,
    gap: 12,
  },
  summaryCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  incomeCard: {
    borderLeftWidth: 4,
    borderLeftColor: '#28a745',
  },
  expenseCard: {
    borderLeftWidth: 4,
    borderLeftColor: '#dc3545',
  },
  balanceCard: {
    borderLeftWidth: 4,
    borderLeftColor: '#0070f3',
  },
  negativeCard: {
    borderLeftWidth: 4,
    borderLeftColor: '#dc3545',
  },
  summaryLabel: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  summaryAmount: {
    fontSize: 24,
    fontWeight: 'bold',
  },
  incomeAmount: {
    color: '#28a745',
  },
  expenseAmount: {
    color: '#dc3545',
  },
  balanceAmount: {
    color: '#0070f3',
  },
  negativeAmount: {
    color: '#dc3545',
  },
  buttonContainer: {
    padding: 16,
    gap: 12,
  },
  button: {
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  incomeButton: {
    backgroundColor: '#28a745',
  },
  expenseButton: {
    backgroundColor: '#dc3545',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default HomeScreen;

