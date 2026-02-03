import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  TextInput,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Alert,
  Platform,
} from 'react-native';
import {useNavigation, useRoute, RouteProp} from '@react-navigation/native';
import {NativeStackNavigationProp} from '@react-navigation/native-stack';
import {RootStackParamList} from '../App';
import {transactionApi, categoryApi} from '../services/api';
import {Transaction, Category} from '../types/Transaction';
import {Picker} from '@react-native-picker/picker';

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;
type RouteProp = RouteProp<RootStackParamList, 'TransactionForm'>;

const TransactionFormScreen: React.FC = () => {
  const navigation = useNavigation<NavigationProp>();
  const route = useRoute<RouteProp>();
  const transaction = route.params?.transaction;

  const [formData, setFormData] = useState({
    type: transaction?.type || 'INCOME',
    amount: transaction?.amount?.toString() || '',
    description: transaction?.description || '',
    categoryId: transaction?.categoryId?.toString() || '',
    transactionDate: transaction?.transactionDate
      ? transaction.transactionDate.split('T')[0]
      : new Date().toISOString().split('T')[0],
  });

  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadCategories();
  }, [formData.type]);

  const loadCategories = async () => {
    try {
      const data = await categoryApi.getByType(formData.type);
      setCategories(data);
    } catch (error) {
      console.error('카테고리 로드 실패:', error);
    }
  };

  const handleSubmit = async () => {
    if (!formData.amount || parseFloat(formData.amount) <= 0) {
      Alert.alert('오류', '금액을 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        type: formData.type as 'INCOME' | 'EXPENSE',
        amount: parseFloat(formData.amount),
        description: formData.description,
        categoryId: formData.categoryId ? parseInt(formData.categoryId) : undefined,
        transactionDate: formData.transactionDate + 'T00:00:00',
      };

      if (transaction?.id) {
        await transactionApi.update(transaction.id, payload);
        Alert.alert('성공', '거래가 수정되었습니다.');
      } else {
        await transactionApi.create(payload);
        Alert.alert('성공', '거래가 추가되었습니다.');
      }

      navigation.goBack();
    } catch (error) {
      console.error('저장 실패:', error);
      Alert.alert('오류', '저장에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const filteredCategories = categories.filter(c => c.type === formData.type);

  return (
    <ScrollView style={styles.container}>
      <View style={styles.form}>
        <View style={styles.formGroup}>
          <Text style={styles.label}>유형</Text>
          <View style={styles.pickerContainer}>
            <Picker
              selectedValue={formData.type}
              onValueChange={value => {
                setFormData({...formData, type: value, categoryId: ''});
              }}
              style={styles.picker}>
              <Picker.Item label="수입" value="INCOME" />
              <Picker.Item label="지출" value="EXPENSE" />
            </Picker>
          </View>
        </View>

        <View style={styles.formGroup}>
          <Text style={styles.label}>금액</Text>
          <TextInput
            style={styles.input}
            value={formData.amount}
            onChangeText={text => setFormData({...formData, amount: text})}
            placeholder="금액을 입력하세요"
            keyboardType="numeric"
          />
        </View>

        <View style={styles.formGroup}>
          <Text style={styles.label}>카테고리</Text>
          <View style={styles.pickerContainer}>
            <Picker
              selectedValue={formData.categoryId}
              onValueChange={value => setFormData({...formData, categoryId: value})}
              style={styles.picker}>
              <Picker.Item label="선택 안함" value="" />
              {filteredCategories.map(category => (
                <Picker.Item
                  key={category.id}
                  label={category.name}
                  value={category.id.toString()}
                />
              ))}
            </Picker>
          </View>
        </View>

        <View style={styles.formGroup}>
          <Text style={styles.label}>설명</Text>
          <TextInput
            style={styles.input}
            value={formData.description}
            onChangeText={text => setFormData({...formData, description: text})}
            placeholder="설명을 입력하세요"
            multiline
          />
        </View>

        <View style={styles.formGroup}>
          <Text style={styles.label}>날짜</Text>
          <TextInput
            style={styles.input}
            value={formData.transactionDate}
            onChangeText={text => setFormData({...formData, transactionDate: text})}
            placeholder="YYYY-MM-DD"
          />
        </View>

        <TouchableOpacity
          style={[styles.submitButton, loading && styles.submitButtonDisabled]}
          onPress={handleSubmit}
          disabled={loading}>
          <Text style={styles.submitButtonText}>
            {loading ? '저장 중...' : transaction?.id ? '수정' : '추가'}
          </Text>
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
  form: {
    padding: 16,
  },
  formGroup: {
    marginBottom: 20,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    minHeight: 44,
  },
  pickerContainer: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    overflow: 'hidden',
  },
  picker: {
    height: Platform.OS === 'ios' ? 200 : 50,
  },
  submitButton: {
    backgroundColor: '#0070f3',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 20,
  },
  submitButtonDisabled: {
    opacity: 0.6,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default TransactionFormScreen;


