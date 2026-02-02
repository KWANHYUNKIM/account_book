import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import HomeScreen from './screens/HomeScreen';
import TransactionFormScreen from './screens/TransactionFormScreen';
import TransactionListScreen from './screens/TransactionListScreen';
import {Transaction} from './types/Transaction';

export type RootStackParamList = {
  MainTabs: undefined;
  TransactionForm: {transaction?: Transaction};
};

const Stack = createNativeStackNavigator<RootStackParamList>();
const Tab = createBottomTabNavigator();

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        tabBarActiveTintColor: '#0070f3',
        tabBarInactiveTintColor: '#666',
        headerStyle: {
          backgroundColor: '#0070f3',
        },
        headerTintColor: '#fff',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
      }}>
      <Tab.Screen
        name="Home"
        component={HomeScreen}
        options={{
          title: '가계부',
          tabBarLabel: '홈',
        }}
      />
      <Tab.Screen
        name="Transactions"
        component={TransactionListScreen}
        options={{
          title: '거래 내역',
          tabBarLabel: '내역',
        }}
      />
    </Tab.Navigator>
  );
}

function App(): JSX.Element {
  return (
    <NavigationContainer>
      <Stack.Navigator
        screenOptions={{
          headerStyle: {
            backgroundColor: '#0070f3',
          },
          headerTintColor: '#fff',
          headerTitleStyle: {
            fontWeight: 'bold',
          },
        }}>
        <Stack.Screen
          name="MainTabs"
          component={MainTabs}
          options={{headerShown: false}}
        />
        <Stack.Screen
          name="TransactionForm"
          component={TransactionFormScreen}
          options={{title: '거래 추가'}}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default App;

