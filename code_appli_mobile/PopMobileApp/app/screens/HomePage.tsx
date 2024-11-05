import { Platform, SafeAreaView, StyleSheet, StatusBar, Text, TextInput, View, TouchableOpacity } from "react-native"
import { storeData, getData } from "../Utils";
import { useEffect, useState } from "react";

function Home({navigation, route}: {navigation: any, route : any}){
  
  const {userName} = route.params // get params from connection page navigate
  const [selectedStatus, setSelectedStatus] = useState(-1)
  const [selectedSmiley, setSelectedSmiley] = useState(-1)
  const smileys = ["😄","😂","🥳","😎","😮","😡","😢"]
  const status = ["green", "yellow", "red"]

  useEffect(() => {

    async function checkIfConfigDone(){
      const configurationDone = await getData("configDone")
      if(!configurationDone){
        navigation.navigate('Configuration')
      }
    }

    checkIfConfigDone()
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

          <View>
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
          </View>

          
          <View style={styles.buttonContainer}>
            <TouchableOpacity onPress={() => {}} style={styles.connectionButton}>
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
  buttonContainer:{
    width : "100%",
    height : "10%",
    display : "flex",
    justifyContent : "center",
    alignItems : "center",
    marginTop : "10%"
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