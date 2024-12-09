import { Platform, SafeAreaView, StyleSheet, StatusBar, Text, TextInput, View, TouchableOpacity } from "react-native"
import { storeData, getData } from "../Utils";
import { useEffect, useRef, useState } from "react";

function Home({navigation, route}: {navigation: any, route : any}){
  
  const {userName, figurine_id, team_id} = route.params // get params from connection page navigate
  const [selectedStatus, setSelectedStatus] = useState(-1)
  const [selectedSmiley, setSelectedSmiley] = useState(-1)
  const moodInputRef = useRef("")
  const smileys = ["😄","😂","🥳","😎","😮","😡","😢"]
  const status = ["green", "yellow", "red"]

  const sendData = async () => {
    const url = "http://192.168.129.61:8080/"
    const dataToSend = {"status" : status[selectedStatus],
                        "smiley" : smileys[selectedSmiley],
                        "mood" : moodInputRef.current
    }
    const responseStatus = await fetch(url+"status/"+team_id+"/"+figurine_id+"/"+status[selectedStatus], {method: "POST"});
    const responseMood = await fetch(url+"mood/"+team_id+"/"+figurine_id+"/"+moodInputRef.current,   {method: "POST"});
    console.log(responseStatus, responseMood)
  }

  useEffect(() => {

    async function checkIfConfigDone(){
      const configurationDone = await getData("configDone")
      if(!configurationDone){
        navigation.navigate('Configuration')
      }
    }

    //checkIfConfigDone()
    // navigation.navigate('Configuration', {
    //     userName : userName,
    //     figurine_id : figurine_id,
    //     team_id : team_id})
  },[])

  return(
    <>
      <SafeAreaView style={styles.container}>
        
        <View style={styles.sectionTitleContainer}>
            <Text style={styles.sectionTitle}>Customization</Text>
        </View>

        <Text style={[styles.sectionTitle, {marginLeft:"0%", textAlign:"center"}]}>Connected as : {userName}</Text>
        
        
          
          <View>
            <View style={styles.sectionTitleContainer}>
              <Text style={styles.sectionTitle}>Status</Text>
            </View>
            <View style={styles.statusContainer}>
              {status.map((status, index) => {
                return(
                  <TouchableOpacity key={status} onPress={() => {setSelectedStatus(index)}} style={[{backgroundColor : status}, styles.surroundingBox, {borderColor : selectedStatus == index ? "black" : "#e3dada"}]}></TouchableOpacity>
                )
              })}
            </View>
            <View style={styles.statusTextContainer}>
              <Text>En ligne</Text>
              <Text>Absent</Text>
              <Text>Occupé</Text>
            </View>
          </View>

          <View style={styles.moodContainer}>
            <View style={styles.sectionTitleContainer}>
              <Text style={styles.sectionTitle}>Mood</Text>
            </View>

            <View style={styles.smileyContainer}>
              {smileys.map((smiley, index) => {
                return(
                  <TouchableOpacity key={index} onPress={() => {setSelectedSmiley(index)}} style={[styles.smileyBox, styles.surroundingBox, {borderColor : selectedSmiley == index ? "black" : "#e3dada"}]}><Text style={styles.smiley}>{smiley}</Text></TouchableOpacity>
                )
              })}
            </View>

            <TextInput placeholder="Write your mood..." onChangeText={(newMood) => {moodInputRef.current = newMood}} maxLength={16} textAlign="center" style={styles.moodInput}></TextInput>
          </View>

          
          <View style={styles.buttonContainer}>
            <TouchableOpacity onPress={() => sendData()} style={styles.connectionButton}>
              <Text style={styles.connectionTextButton}>Update Status</Text>
            </TouchableOpacity>
          </View>
          
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
  sectionTitleContainer:{
    width : "100%",
    marginVertical : "5%",
    backgroundColor : "#bab1b1"
  },
  sectionTitle:{
    fontWeight : "bold",
    fontSize : 25,
    marginLeft : "5%"
  },
  surroundingBox :{
    width : 40,
    height : 40,
    borderColor : "#e3dada",
    borderWidth : 3,
    borderRadius : 5,
  },
  statusContainer:{
    display : "flex",
    flexDirection : "row",
    justifyContent : "space-evenly",
    alignItems : "center"
  },
  statusTextContainer:{
    display : "flex",
    flexDirection : "row",
    justifyContent : "space-evenly",
    alignItems : "center"
  },
  greenStatus:{
    backgroundColor : "green"
  },
  yellowStatus:{
    backgroundColor : "yellow"
  },
  redStatus:{
    backgroundColor : "red"
  },
  moodContainer:{
    display: "flex",
    alignItems : "center"
  },
  smileyContainer:{
    display : "flex",
    flexWrap : "wrap",
    flexDirection : "row",
    justifyContent : "center",
    alignItems : "center"
  },
  smileyBox:{
    display : "flex",
    flexBasis : "21%",
    margin : "2%",
    justifyContent :"center",
    alignItems : "center"
  },
  smiley:{
    fontSize : 25
  },
  moodInput:{
    width : "80%",
    height : "20%",
    borderColor : "#e3dada",
    borderWidth : 3,
    borderRadius : 5,
    padding : 10,
    marginTop: "5%"
  },
  buttonContainer:{
    width : "100%",
    height : "10%",
    display : "flex",
    justifyContent : "center",
    alignItems : "center",
  },
  connectionButton:{
    width : "70%",
    height :"100%",
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

export default Home