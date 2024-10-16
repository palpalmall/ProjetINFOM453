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
const bool teteFrappe = true;
const int analogPin = 0;
 
void setup() {
  Serial.begin(9600); //to print analog data

  // Join I2C bus as slave with address 8
  Wire.begin(0x3C);
  
  // Call receiveEvent when data received                
  Wire.onReceive(receiveEvent);

  Wire.onRequest(requestEvent);
  
  // Setup pin 13 as output and turn LED off
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
}
 
// Function that executes whenever data is received from master
void receiveEvent(int howMany) {
  while (Wire.available()) { // loop through all but the last
    char c = Wire.read(); // receive byte as a character
    Serial.print(c);
    digitalWrite(ledPin, c);
  }
}

void requestEvent(){
  Wire.write("caca");         // respond with message of 6 bytes as expected by master
}

void loop() {
  delay(200);
  Serial.print(analogRead(analogPin));
  Serial.print("\n");
}