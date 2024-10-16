/*
  Arduino Slave for Raspberry Pi Master
  i2c_slave_ard.ino
  Connects to Raspberry Pi via I2C
  
  DroneBot Workshop 2019
  https://dronebotworkshop.com
*/
 
// Include the Wire library for I2C
#include <Wire.h>
 
// LED on pin 6
const int ledPin = 6; // onboard led
const int redLEDPin = 1;
const int yellowLEDPin = 7;
 
void setup() {
  Serial.begin(9600);
  // Join I2C bus as slave with address 8
  Wire.begin(0x8);
  
  // Call receiveEvent when data received                
  Wire.onReceive(receiveEvent);
  
  // Setup pin 6 as output and turn LED off
  pinMode(ledPin, OUTPUT);
  pinMode(redLEDPin, OUTPUT);
  pinMode(yellowLEDPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  digitalWrite(redLEDPin, LOW);
  digitalWrite(yellowLEDPin, LOW);
}
 
// Function that executes whenever data is received from master
void receiveEvent(int howMany) {

  digitalWrite(redLEDPin, LOW);
  digitalWrite(yellowLEDPin, LOW);

  int offset = Wire.read(); // je ne sais pas a quoi sert l'offset qu'il faut envoyer
  int action = Wire.read(); // read the first byte to know the action needed to be done
  Serial.print("action= "); Serial.print(action);
  Serial.print("\n");
  switch(action){

    case 0 : // status LED action
      if(Wire.read() == 0){
        digitalWrite(redLEDPin, HIGH);
        digitalWrite(yellowLEDPin, LOW);
      }else{
        digitalWrite(redLEDPin, LOW);
        digitalWrite(yellowLEDPin, HIGH);
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

void loop() {
  delay(100);
}