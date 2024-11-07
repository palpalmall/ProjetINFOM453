import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import 'expo-dev-client';
import Home from './app/screens/HomePage'
import Configuration from './app/screens/ConfigurationPage'
import Connection from './app/screens/ConnectionPage';
import { useEffect } from 'react';

import BleManager, { BleState, Peripheral }  from 'react-native-ble-manager';

const Stack = createNativeStackNavigator();// sert a la navigation entre différents écrants

//https://stackoverflow.com/questions/71092085/react-native-release-apk-does-not-fetch-data-from-local-network

export default function App() {

  useEffect(() => {
    BleManager.start({ showAlert: false, forceLegacy: true }).then(() => {});
  }, []);

  return (
    <>
      <NavigationContainer>
        <Stack.Navigator screenOptions={{headerShown: false}}>
          
          <Stack.Screen
            name="Connection"
            component={Connection}
          />
        
          <Stack.Screen
            name="Home"
            component={Home}
          />

          <Stack.Screen
            name="Configuration"
            component={Configuration}
          />

        </Stack.Navigator>
      </NavigationContainer>
    </>
  );
}
