import { Platform, SafeAreaView, StyleSheet, StatusBar, Text, TouchableOpacity, Linking, NativeModules, NativeEventEmitter, EmitterSubscription, TextInput, View } from "react-native"
import { storeData, getData } from "../Utils";
import { useEffect, useRef, useState } from "react";

import { promptForEnableLocationIfNeeded } from 'react-native-android-location-enabler';
import { PERMISSIONS, RESULTS, request } from 'react-native-permissions';
import BleManager, { BleState, Peripheral }  from 'react-native-ble-manager';

global.Buffer = require('buffer').Buffer;

function Configuration({navigation, route}: {navigation: any, route : any}){

  const {userName} = route.params // get params from connection page navigate
  const [nearbyPeripherals, setNearbyPeripherals] = useState<Map<string, Peripheral>>(new Map<string, Peripheral>())
  const [writtenDeviceId, setWrittenDeviceId] = useState("")
  const [configStep, setConfigStep] = useState("Step 1. Enable GPS")
  const [stepExplanations, setStepExplanations] = useState("Click on the button beneath to activate the location of your device")
  const [alertMessage, setAlertMessage] = useState("")
  const [timeLeft, setTimeLeft] = useState(5)
  const wifiNameRef = useRef("")
  const wifiPasswordRef = useRef("")
  const BleManagerModule = NativeModules.BleManager;
  const bleManagerEmitter = new NativeEventEmitter(BleManagerModule);

  // Step 1 Enable GPS
  const handleGPSPermission = async () => {
    //ask for GPS permission
    const result = await askGPSPermission();
    if (result?.enabled) {
      //GPS enabled successfully, display next permission
      setConfigStep("Step 2. Accept permissions")
      setStepExplanations("Click on the beneath button to accept the permissions to use the location and the bluetooth of your device")
      setAlertMessage("")// empty the alertMessage
    } else {
      //GPS couldn't be enabled. Show GPS modal warning
      console.log(result?.code)
      if (result?.code === 'ERR00') {
        //The user has clicked on Cancel button in the popup
        //show the pemission denied popup or ask them to manually enable the gps
        setAlertMessage("The application need the location to be activated for scanning with bluetooth, please turn it on")
        return;
      } else {
        //something went wrong, prompt user to manually enable the GPS from his side from settings as our attempt failed
        setAlertMessage("The application need the location to be activated for scanning with bluetooth, please turn it on")
      }
    }
  };

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

  // Step 2. Accept Location and Bletooth permissions
  const handleAcceptPermissions = async () => {

    let GPSPerm = await handleLocationPermission()
    let BLEPerm = await handleBluetoothPermission()

    if(GPSPerm && (BLEPerm == undefined || BLEPerm)){
      setConfigStep("Step 3. Enable bluetooth")
      setStepExplanations("Click on the button to automatically turn on your device bluetooth")
      setAlertMessage("")// empty the alertMessage
    }else{
      setAlertMessage("One or more permissions has been denied, retry please")
    }
  }

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

    return isPermitted
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

  // Step 3. Enable Bluetooth
  const enableBluetoothAndroid = async () => {

    //before scaning try to enable bluetooth if not enabled already
    if (Platform.OS === 'android' && await BleManager.checkState() === BleState.Off) {
      try {
        await BleManager.enableBluetooth().then(() => console.info('Bluetooth is enabled'));
        //go ahead to scan nearby devices
      } catch (e) {
        //prompt user to enable bluetooth manually and also give them the option to navigate to bluetooth settings directly.
        openBluetoothSettings()
      }
    }
    
    let BleEnabled = await BleManager.checkState() === BleState.On
    if(BleEnabled){
      setConfigStep("Step 4. Scan for Rak device")
      setStepExplanations("Click on the beneath button to scan for the devices using bluetooth (BLE) arond you")
      setAlertMessage("") // empty the alertMessage
    }else{
      setAlertMessage("Bluetooth could not be enabled, please turn it on")
    }
  };

  const openBluetoothSettings = () => {
    if (Platform.OS === 'ios') {
      Linking.openURL('App-Prefs:Bluetooth');
    } else {
      Linking.sendIntent('android.settings.BLUETOOTH_SETTINGS');
    }
  };

  // Step 4. Scan for devices (using only BLE, not casual Bluetooth)
  const SECONDS_TO_SCAN = 6;
  const SERVICE_UUIDS : any[] = []; // peut etre limité a un seul UUID pour ne reconnaitre que notre rak ??? Revoir part 2
  const ALLOW_DUPLICATE = false;

  const scanNearbyDevices = (): Promise<Map<string, Peripheral>> => {

    return new Promise((resolve, reject) => {

      const devicesMap = new Map<string, Peripheral>();

      let listeners: EmitterSubscription[] = [];

      const onBleManagerDiscoverPeripheral = (peripheral: Peripheral) => {
            if (peripheral.id && peripheral.name) {
              devicesMap.set(peripheral.id, peripheral);
            }
      };

      const onBleManagerStopScan = () => {
            for (const listener of listeners) {
              listener.remove();
            }
            setConfigStep("Step 5. Connect to the Rak");
            setStepExplanations("Select the Rak device and click on it when it has appeared in the list")
            setAlertMessage("") // empty the alertMessage
            resolve(devicesMap);
      };

      try {

         listeners = [
           bleManagerEmitter.addListener('BleManagerDiscoverPeripheral', onBleManagerDiscoverPeripheral),
           bleManagerEmitter.addListener('BleManagerStopScan', onBleManagerStopScan),
         ];

         BleManager.scan(SERVICE_UUIDS, SECONDS_TO_SCAN, ALLOW_DUPLICATE);

        } catch (error) {
          setAlertMessage("An error occured")
          reject(new Error(error instanceof Error ? error.message : (error as string)));
        }
    })
  };

  const scanDevices = async () => {
    const nearbyDevices = await scanNearbyDevices();
    console.log(nearbyDevices)
    setNearbyPeripherals(nearbyDevices)
  };

  // Step 5. Connect to the Rak
  const MAX_CONNECT_WAITING_PERIOD = 30000;
  const serviceReadinIdentifier = 'A07498CA-AD5B-474E-940D-16F1FBE7E8CD';
  const charNotificationIdentifier = "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021B"//'51FF12BB-3ED8-46E5-B4F9-D64E2FEC021B';
  const connectedDeviceId = useRef("")
  
  const enableBluetooth = async () => {
   //before connecting try to enable bluetooth if not enabled already
   console.log(Platform.OS, await BleManager.checkState())
    if (Platform.OS === 'android' && await BleManager.checkState() === BleState.Off) {
      try {
        await BleManager.enableBluetooth().then(() => console.log('Bluetooth is enabled'));
        //go ahead to connect to the device.
        return true;
      } catch (e) {
        openBluetoothSettings()
      }
    } else{ if(Platform.OS === 'ios' && await BleManager.checkState() === BleState.Off){
      //For ios, if bluetooth is disabled, don't let user connect to device.
      setAlertMessage("Bluetooth has been disabled, please turn it on")
      return false;
    }}
    // ci-dessous : ajouter par Mael
    return (await BleManager.checkState() === BleState.On) ? true : false
  };
  
  const isDeviceConnected = async (deviceId: string) => {
    return await BleManager.isPeripheralConnected(deviceId, []);
  };

  const connect = (deviceId: string): Promise<boolean> => {
    setAlertMessage("") // empty the alertMessage

    return new Promise<boolean>(async (resolve, reject) => {
        console.log("connect function launched")
        let failedToConnectTimer: NodeJS.Timeout;
         
        //For android always ensure to enable the bluetooth again before connecting.
        const isEnabled = await enableBluetooth();
        console.log(isEnabled)
        if(!isEnabled) {
          //if blutooth is somehow off, first prompt user to turn on the bluetooth
          console.log("bluetooth not enabled")
          return resolve(false);
        }

        //before connecting, ensure if app is already connected to device or not.
        let isConnected = await isDeviceConnected(deviceId);
        console.log("is already connected : ", isConnected)
        if (!isConnected) {

          //if not connected already, set the timer such that after some time connection process automatically stops if its failed to connect.
          failedToConnectTimer = setTimeout(() => {
              return resolve(false);
          }, MAX_CONNECT_WAITING_PERIOD);

          await BleManager.connect(deviceId).then(() => {
            //if connected successfully, stop the previous set timer.
            clearTimeout(failedToConnectTimer);
            console.log("connected !")
          });
          isConnected = await isDeviceConnected(deviceId);
        }

        if (!isConnected) {
          //now if its not connected somehow, just close the process.
          return resolve(false);
        } else {
        //Connection success
        connectedDeviceId.current = deviceId
        console.log("connected to ", connectedDeviceId.current)
        //get the services and characteristics information for the connected hardware device.
        const peripheralInformation = await BleManager.retrieveServices(deviceId);
        //console.log("connected ! and ", peripheralInformation)
        for (let characteristic of peripheralInformation.characteristics!){
          console.log(characteristic.characteristic, characteristic.properties)
        }

        /**
         * Check for supported services and characteristics from device info
         */
        const deviceSupportedServices = (peripheralInformation.services || []).map(item => item?.uuid?.toUpperCase());
        const deviceSupportedCharacteristics = (peripheralInformation.characteristics || []).map(_char =>
          _char.characteristic.toUpperCase(),
        );
        console.log(deviceSupportedServices, " --- ", deviceSupportedCharacteristics)
        if (
          !deviceSupportedServices.includes(serviceReadinIdentifier) ||
          !deviceSupportedCharacteristics.includes(charNotificationIdentifier)
        ) { 
          //if required service ID and Char ID is not supported by hardware, close the connection.
          isConnected = false;
          await BleManager.disconnect(connectedDeviceId.current);
          return reject('Connected device does not have required service and characteristic.');
        }

        await BleManager
          .startNotification(deviceId, serviceReadinIdentifier, charNotificationIdentifier)
          .then(response => {
            console.log('Started notification successfully on ', charNotificationIdentifier);
            setConfigStep("Step 6. Send the wifi's name and password")
            setStepExplanations("Write down into the beneath fields the name of your wifi and the password before sending them to the Rak")
          })
          .catch(async () => {
            isConnected = false;
            await BleManager.disconnect(connectedDeviceId.current);
            return reject('Failed to start notification on required service and characteristic.');
          });

          let disconnectListener = bleManagerEmitter.addListener('BleManagerDisconnectPeripheral',async () => {
            //addd the code to execute after hardware disconnects.
            if(connectedDeviceId.current){
              await BleManager.disconnect(connectedDeviceId.current);
            }
            disconnectListener.remove();
          });

        return resolve(isConnected);
      }})
    };

  const sendDataToRpi = async (wifiName : string, wifiPassword : string) => {
    const service_send_wifi_data = "A07498CA-AD5B-474E-940D-16F1FBE7E8CD"
    const wifi_name_char = "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021B"
    const wifi_password_char = "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021C"
    await BleManager.writeWithoutResponse(connectedDeviceId.current, service_send_wifi_data, wifi_name_char, [...Buffer.from(wifiName, "utf-8")])//"FFF0", "FFF3", [...Buffer.from(wifiName, "utf-8")])
    await BleManager.writeWithoutResponse(connectedDeviceId.current, service_send_wifi_data, wifi_password_char, [...Buffer.from(wifiPassword, "utf-8")])

    await storeData("configDone", true) // a mettre ailleur ensuite
    setConfigStep("Step 7. Finished")
    setStepExplanations("")
    const decount = setInterval(() => {setTimeLeft((timeLeft) => timeLeft -1)}, 1000)
    const redirection = setTimeout(() => {
      navigation.navigate("Home", {userName : userName});
      clearInterval(decount);
      clearTimeout(redirection);
      }, 5000)
  }

  return(
    <>
      <View style={styles.sectionTitleContainer}>
        <Text style={styles.sectionTitle}>Configuration</Text>
      </View>
      <SafeAreaView style={styles.container}>

        {configStep[5] === "6" && <Text style={{fontWeight:"bold", fontSize : 20, marginBottom : "5%"}}>Connected!</Text>}

        <Text style={styles.sectionTitle}>{configStep}</Text>
        <Text style={styles.stepExplanationText}>{stepExplanations}</Text>
        {alertMessage.length > 0 && <Text style={styles.alertMessage}>{alertMessage}</Text>}
        

        {configStep[5] === "1" && <TouchableOpacity style={styles.connectionButton} onPress={handleGPSPermission}>
          <Text style={styles.connectionTextButton}>Enable GPS</Text>
        </TouchableOpacity>}

        {configStep[5] === "2" && <TouchableOpacity style={styles.connectionButton} onPress={handleAcceptPermissions}>
          <Text style={styles.connectionTextButton}>Test everything button</Text>
        </TouchableOpacity>}

        {configStep[5] === "3" && <TouchableOpacity style={styles.connectionButton} onPress={enableBluetoothAndroid}>
          <Text style={styles.connectionTextButton}>enable Bluetooth</Text>
        </TouchableOpacity>}

        {(configStep[5] === "4" || configStep[5] === "5") && <TouchableOpacity style={styles.connectionButton} onPress={scanDevices}>
          <Text style={styles.connectionTextButton}>Scan nearby peripherals</Text>
        </TouchableOpacity>}

        {(configStep[5] === "4" || configStep[5] === "5") && <Text>Nearby peripherals list : </Text>}
        {(configStep[5] === "4" || configStep[5] === "5") && Array.from(nearbyPeripherals, ([key, peripheral]) => (
            <TouchableOpacity key={key} style={styles.deviceButton} onPress={async () => await connect(peripheral.id).then((e) => console.log(e)).catch((e) => console.log(e))}>
              <Text  style={styles.deviceButtonText}>{peripheral.id + " " + peripheral.name}</Text>
            </TouchableOpacity>
        ))}

        {configStep[5] === "6" && <TextInput placeholder="Wifi's name (SSID)" onChangeText={(newText) => wifiNameRef.current = newText} style={styles.nameInput}></TextInput>}
        {configStep[5] === "6" && <TextInput placeholder="Wifi's password" onChangeText={(newText) => wifiPasswordRef.current = newText} style={styles.nameInput}></TextInput>}
        {configStep[5] === "6" && <TouchableOpacity style={styles.connectionButton} onPress={() => sendDataToRpi(wifiNameRef.current, wifiPasswordRef.current)}>
          <Text style={styles.connectionTextButton}>Send config to the Rak</Text>
        </TouchableOpacity>}

        {configStep[5] === "7" && 
        <View>
          <Text>Configuration finished.</Text> 
          <Text>Your will be redirected in {timeLeft} sec...</Text>
        </View>}
        
      </SafeAreaView>
    </>
  )
}

let android = Platform.OS === "android";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent :"center",
    alignItems : "center",
    backgroundColor: '#fff',
  },
  sectionTitleContainer:{
    width : "100%",
    backgroundColor : "#bab1b1",
    paddingTop : android ? StatusBar.currentHeight : 10
  },
  sectionTitle:{
    fontWeight : "bold",
    fontSize : 25,
    marginLeft : "5%"
  },
  stepExplanationText:{
    width : "80%",
    fontSize : 15,
    marginVertical : "2%"
  },
  alertMessage:{
    width : "100%",
    backgroundColor : "red",
    color : "white",
    fontSize : 15,
    marginBottom : "5%",
    padding : 10
  },
  connectionButton:{
    width : "70%",
    height :"10%",
    display : "flex",
    justifyContent : "center",
    alignItems:"center",
    borderRadius : 15,
    backgroundColor : "blue",
    marginVertical : "5%"
  },
  connectionTextButton:{
    color:"white",
    fontSize:20
  },
  deviceButton:{
    width : "65%",
    height :"10%",
    maxHeight: 50,
    display : "flex",
    justifyContent : "center",
    alignItems:"center",
    borderRadius : 5,
    backgroundColor : "grey",
    marginVertical : "2%",
    padding : 5
  },
  deviceButtonText:{
    color:"white",
    fontSize:15
  },
  nameInput:{
    width : "70%",
    height : "10%",
    borderColor:"black",
    borderWidth : 2,
    borderRadius : 5,
    padding : 15,
    marginBottom : "5%"
  },
})

export default Configuration;