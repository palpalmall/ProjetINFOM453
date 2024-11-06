import { Platform, SafeAreaView, StyleSheet, StatusBar, Text, TouchableOpacity, Linking } from "react-native"
import { storeData, getData } from "../Utils";
import { useEffect } from "react";

import { promptForEnableLocationIfNeeded } from 'react-native-android-location-enabler';
import { PERMISSIONS, RESULTS, request } from 'react-native-permissions';
import BleManager, { BleState }  from 'react-native-ble-manager';

function Configuration(){

  const handleBluetoothConnection = async () => {
    await handleGPSPermission()
    await handleLocationPermission()
    await handleBluetoothPermission()
    await enableBluetoothAndroid()
  }

  const askGPSPermission = async () => {
    //prompt to enable gps
    try {
      await promptForEnableLocationIfNeeded();
      /*
      Here now the user has accepted to enable the location services
      data can be :
       - "already-enabled" if the location services has been already enabled
       - "enabled" if user has clicked on OK button in the popup
      */
      return {
        enabled: true,
      };
    } catch (error: unknown) {
      if (error instanceof Error) {
        /*
        The user has not accepted to enable the location services
        OR something went wrong during the process
        "err" : { "code" : "ERR00|ERR01|ERR02|ERR03", "message" : "message"}
        codes :
         - ERR00 : The user has clicked on Cancel button in the popup
         - ERR01 : If the Settings change are unavailable
         - ERR02 : If the popup has failed to open
         - ERR03 : Internal error
        */
        return {
          enabled: false,
          code: error.message,
        };
      }
    }
  };

  const handleGPSPermission = async () => {
    //ask for GPS permission
    const result = await askGPSPermission();
    if (result?.enabled) {
      //GPS enabled successfully, display next permission
    } else {
      //GPS couldn't be enabled. Show GPS modal warning
      console.log(result?.code)
      if (result?.code === 'ERR00') {
        //The user has clicked on Cancel button in the popup
        //show the pemission denied popup or ask them to manually enable the gps
        return;
      } else {
        //something went wrong, prompt user to manually enable the GPS from his side from settings as our attempt failed
      }
    }
  };

  const handleLocationPermission = async () => {
    let isPermitted = false;
    let isIos = Platform.OS === "ios";
    if (isIos) {
      //ask for location permissions for IOS
      const locationResult = await Promise.all([
        request(PERMISSIONS.IOS.LOCATION_ALWAYS),
        request(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE),
      ]);
      let resultAlways = locationResult[0];
      let resultWhenInUse = locationResult.length > 1 ? locationResult[1] : null;
      const isResultAlwaysDenied = () => {
        if (
          resultAlways === RESULTS.BLOCKED ||
          resultAlways === RESULTS.UNAVAILABLE ||
          resultAlways === RESULTS.DENIED
        ) {
          return true;
        } else {
          return false;
        }
      };
      const isResultWhenInUseDenied = () => {
        if (
          resultWhenInUse === RESULTS.BLOCKED ||
          resultWhenInUse === RESULTS.UNAVAILABLE ||
          resultWhenInUse === RESULTS.DENIED
        ) {
          return true;
        } else {
          return false;
        }
      };
      if (isResultAlwaysDenied() && isResultWhenInUseDenied()) {
        //user hasn't allowed location
        isPermitted = false;
      } else {
        //user has allowed location
        isPermitted = true;
      }
    } else {
      //ask for location permissions for Android
      const result = await request(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION);
      isPermitted = result === RESULTS.GRANTED;
    }

    if (isPermitted) {
      //Location permitted successfully, display next permission
     };
  };

  const askBluetoothPermissions = async () => {
    let isIos = Platform.OS === "ios";
    if (isIos) {
      //ask for bluetooth permission for IOS
      const result = await request(PERMISSIONS.IOS.BLUETOOTH);
      if (result !== RESULTS.GRANTED) {
        //either permission is not granted or it's unavailable.
        if (result === RESULTS.UNAVAILABLE) {
          //if bluetooth is off, prompt user to enable the bluetooth manually.
          return { type: "enableBluetooth", value: false };
        } else {
          //if user denied for bluetooth permission, prompt them to enable it from settigs later.
          return { type: "bletoothPermission", value: false };
        }
      }
      //bluetooth permission has been granted successfully
      return { type: "bletoothPermission", value: true };
    } else {
      const version = parseInt(Platform.Version.toString(), 10)
      if(version > 30) {
        //ask for bluetooth permission for Android for version >= 12.
        if ((await request(PERMISSIONS.ANDROID.BLUETOOTH_SCAN)) !== RESULTS.GRANTED) {
          return { type: "bletoothPermission", value: false };
        }
        console.info('BLUETOOTH_SCAN permission allowed');
        if ((await request(PERMISSIONS.ANDROID.BLUETOOTH_CONNECT)) !== RESULTS.GRANTED) {
          return { type: "bletoothPermission", value: false };
        }
        console.info('BLUETOOTH_CONNECT permission allowed');
        if ((await request(PERMISSIONS.ANDROID.BLUETOOTH_ADVERTISE)) !== RESULTS.GRANTED) {
          return { type: "bletoothPermission", value: false };
        }
        console.info('BLUETOOTH_ADVERTISE permission allowed');
        return { type: "bletoothPermission", value: true };
      } else {
        //for android version < 12, no need of runtime permissions.
        return { type: "bletoothPermission", value: true };
      }
    }
  };

  const handleBluetoothPermission = async () => {
    const isPermissionAllowed = await askBluetoothPermissions();
    if (isPermissionAllowed.value) {
      //Bluetooth permission allowed successfully
    } else {
      //Bluetooth permission denied. Show Bluetooth modal warning
      if (isPermissionAllowed.type === "bletoothPermission") {
        //if user denied for bluetooth permission, prompt them to enable it from settigs later.
      } else {
        //For ios, if bluetooth is off, prompt user to enable the bluetooth manually by themselves.
      }
    }
  };

  const openBluetoothSettings = () => {
    if (Platform.OS === 'ios') {
      Linking.openURL('App-Prefs:Bluetooth');
    } else {
      Linking.sendIntent('android.settings.BLUETOOTH_SETTINGS');
    }
  };

  const enableBluetoothAndroid = async () => {
    //before scaning try to enable bluetooth if not enabled already
    if (Platform.OS === 'android' && await BleManager.checkState() === BleState.Off) {
      try {
        await BleManager.enableBluetooth().then(() => console.info('Bluetooth is enabled'));
        //go ahead to scan nearby devices
      } catch (e) {
        //prompt user to enable bluetooth manually and also give them the option to navigate to bluetooth settings directly.
        openBluetoothSettings()
        return;
      }
    }
  };

  useEffect(() => {
    async function sayConfigDone(){
      await storeData("configDone", false)
    }

    sayConfigDone()
  }, [])

  return(
    <>
      <SafeAreaView style={styles.container}>
        <Text>Configuration</Text>

        <TouchableOpacity onPress={handleGPSPermission}>
          <Text>Enable GPS</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={handleBluetoothConnection}>
          <Text>Test everything button</Text>
        </TouchableOpacity>
        
      </SafeAreaView>
    </>
  )
}

let android = Platform.OS === "android";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    paddingTop : android ? StatusBar.currentHeight : 0
  },
})

export default Configuration;