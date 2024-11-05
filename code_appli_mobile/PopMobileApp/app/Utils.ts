import AsyncStorage from '@react-native-async-storage/async-storage';

export async function storeData(key: string,value : any){
    try {
      await AsyncStorage.setItem(key, JSON.stringify(value));
    } catch (e) {
      // saving error
      console.log("In storeData : ",e)
    }
  };

export async function getData(key : string){
    try {
      const value = await AsyncStorage.getItem(key);
      if (value !== null) {
        return JSON.parse(value)
      }
    } catch (e) {
      // error reading value
      console.log("in getData : ",e)
    }
  };