/*
  Arduino Slave for Raspberry Pi Master
  i2c_slave_ard.ino
  Connects to Raspberry Pi via I2C
  
  DroneBot Workshop 2019
  https://dronebotworkshop.com
*/
 
// Include the Wire library for I2C
#include <Wire.h>
 
// LED on pin 13
const int ledPin = 13; 
const int REDPin = 3;
const int GREENPin = 5;
const int BLUEPin = 6;
const bool teteFrappe = true;
const int analogPin = 0;
 
void setup() {
  Serial.begin(9600); //to print analog data

  // Join I2C bus as slave with address 8
  Wire.begin(0x3C);
  
  // Call receiveEvent when data received                
  Wire.onReceive(receiveEvent);

  //Wire.onRequest(requestEvent);
  
  // Setup pin 13 and RGB pins as output and turn LEDs off
  pinMode(ledPin, OUTPUT);
  pinMode(REDPin, OUTPUT);
  pinMode(GREENPin, OUTPUT);
  pinMode(BLUEPin, OUTPUT);
  // on eteind les pins RGB
  digitalWrite(REDPin, LOW);
  digitalWrite(GREENPin, LOW);
  digitalWrite(BLUEPin, LOW);
}
 
// Function that executes whenever data is received from master
void receiveEvent(int howMany) {
  
  digitalWrite(REDPin, LOW);
  digitalWrite(GREENPin, LOW);
  digitalWrite(BLUEPin, LOW);
  
  // on recoit dans l'ordre : [offset, action, valeur]
  int offset = Wire.read(); // je ne sais pas a quoi sert l'offset qu'il faut envoyer
  int action = Wire.read(); // read the first byte to know the action needed to be done
  Serial.print("action= "); Serial.print(action);
  Serial.print("\n");

  switch(action){

    case 0 : // status LED action
      if(Wire.read() == 0){ // status = absent donc lumiere rouge
        analogWrite(REDPin, 255);
        digitalWrite(GREENPin, LOW);
        digitalWrite(BLUEPin, LOW);
      }else{ // status = present donc lumiere verte
        analogWrite(GREENPin, 255);
        digitalWrite(REDPin, LOW);
        digitalWrite(BLUEPin, LOW);
      }
      break;

    case 1 : // onboard LED action
      char c = Wire.read(); // receive byte as a character
      digitalWrite(ledPin, c);
      break;
  }

  // while (Wire.available()) { // loop through all
  //   char c = Wire.read(); // receive byte as a character
  //   digitalWrite(ledPin, c);
  // }
}

// void requestEvent(){
//   Wire.write("caca");         // respond with message of 6 bytes as expected by master
// }

void loop() {
  delay(200);
  //Serial.print(analogRead(analogPin));
  //Serial.print("\n");
}