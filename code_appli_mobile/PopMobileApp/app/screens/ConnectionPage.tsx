import { useRef, useState } from "react";
import { Platform, SafeAreaView, StyleSheet, StatusBar, Text, TextInput, View, TouchableOpacity } from "react-native"

function Connection({navigation}: {navigation: any}){

  const serverURL = ""
  let nameInput = useRef("")
  let passwordInput = useRef("")
  const [serverResponse, setServerResponse] = useState(false)

  const handleConnection = async () => {
    //let response = await fetch(serverURL, )
    //response = await response.json()
    let figurine_id = "101112"
    let team_id = "32"
    let response = (nameInput.current === "Jose" && passwordInput.current === "Test") 
    if(response){
      navigation.navigate("Home", {
        userName : nameInput.current,
        figurine_id : figurine_id,
        team_id : team_id})
    }

    setServerResponse(!response)
  }

  return(
    <>
      <SafeAreaView style={styles.container}>
        {serverResponse && <Text style={styles.alertMessage}>Le mot de passe ou le nom utilisateur est incorrect</Text>}
        <TextInput onChangeText={(newName) => {nameInput.current = newName}} placeholder="Name" style={styles.nameInput}></TextInput>
        <TextInput secureTextEntry={true} onChangeText={(newPsw) => {passwordInput.current = newPsw}} placeholder="password" style={styles.passwordInput}></TextInput>
        <TouchableOpacity onPress={handleConnection} style={styles.connectionButton}>
          <Text style={styles.connectionTextButton}>Connection</Text>
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
    justifyContent : "center",
    alignItems:"center",
    padding : 10,
    paddingTop : android ? StatusBar.currentHeight : 0
  },
  alertMessage:{
    width : "100%",
    backgroundColor : "red",
    color : "white",
    fontSize : 15,
    marginBottom : "5%",
    padding : 10
  },
  nameInput:{
    width : "100%",
    height : "10%",
    borderColor:"black",
    borderWidth : 2,
    borderRadius : 5,
    padding : 15,
    marginBottom : "5%"
  },
  passwordInput:{
    width : "100%",
    height : "10%",
    borderColor:"black",
    borderWidth : 2,
    borderRadius : 5,
    padding : 15,
    marginBottom : "5%"
  },
  connectionButton:{
    width : "70%",
    height :"10%",
    display : "flex",
    justifyContent : "center",
    alignItems:"center",
    borderRadius : 15,
    backgroundColor : "blue"
  },
  connectionTextButton:{
    color:"white",
    fontSize:20
  }
})

export default Connection