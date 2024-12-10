import { useRef, useState } from "react";
import { Platform, SafeAreaView, StyleSheet, StatusBar, Text, TextInput, View, TouchableOpacity} from "react-native"

function Connection({navigation}: {navigation: any}){

  const serverURL = ""
  let figIdInput = useRef("")
  let teamIdInput = useRef("")
  const [serverResponse, setServerResponse] = useState(false)

  const handleConnection = async () => {
    //let response = await fetch(serverURL, )
    //response = await response.json()
    let response = (figIdInput.current === "Dumas" && teamIdInput.current === "Prof") 
    if(response){
      navigation.navigate("Home", {
        figurine_id : figIdInput.current,
        team_id : teamIdInput.current})
    }

    setServerResponse(!response)
  }

  return(
    <>
      <SafeAreaView style={styles.container}>
        {serverResponse && <Text style={styles.alertMessage}>Le mot de passe ou le nom utilisateur est incorrect</Text>}
        <TextInput onChangeText={(newFigId) => {figIdInput.current = newFigId}} placeholder="Your figurine's ID" style={styles.figIdInput}></TextInput>
        <TextInput onChangeText={(newTeamId) => {teamIdInput.current = newTeamId}} placeholder="Your team's ID" style={styles.teamIdInput}></TextInput>
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
  figIdInput:{
    width : "100%",
    height : "10%",
    borderColor:"black",
    borderWidth : 2,
    borderRadius : 5,
    padding : 15,
    marginBottom : "5%"
  },
  teamIdInput:{
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