import { Platform, SafeAreaView, StyleSheet, StatusBar, Text } from "react-native"
import { storeData, getData } from "../Utils";
import { useEffect } from "react";

function Configuration(){

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