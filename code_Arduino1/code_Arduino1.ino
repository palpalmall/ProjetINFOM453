/*
  Arduino Slave for Raspberry Pi Master
  i2c_slave_ard.ino
  Connects to Raspberry Pi via I2C
  
  DroneBot Workshop 2019
  https://dronebotworkshop.com
*/
 
// Include the Wire library for I2C
#include <Wire.h>
#include <Servo.h>
 
// LED on pin 6
const int ledPin = 6; // onboard led
const int redLEDPin = 1; //normal pin (not analog nor pwm)
const int yellowLEDPin = 7; //normal pin (not analog nor pwm)

Servo myservo;
const int servoPin = 5;
int pos = 0;
 
void setup() {
  Serial.begin(9600);
  // Join I2C bus as slave with address 8
  Wire.begin(0x8);
  
  // Call receiveEvent when data received                
  Wire.onReceive(receiveEvent);

  // Call receiveEvent when data received                
  Wire.onRequest(requestEvent);
  
  // Setup pin 6 as output and turn LED off
  pinMode(ledPin, OUTPUT);
  pinMode(redLEDPin, OUTPUT);
  pinMode(yellowLEDPin, OUTPUT);
  digitalWrite(ledPin, LOW); //on eteind la onboard led
  //on eteind les led rouge et jaune
  digitalWrite(redLEDPin, LOW);
  digitalWrite(yellowLEDPin, LOW);

  myservo.attach(servoPin);
  myservo.write(0);
}
 
// Function that executes whenever data is received from master
void receiveEvent(int howMany) {
  if (Wire.available()>0){
    digitalWrite(redLEDPin, LOW);
    digitalWrite(yellowLEDPin, LOW);

    // on recoit dans l'ordre : [action, data=optional]
    int action = Wire.read(); // read the first byte to know the action needed to be done
    print("action = ", action);

    switch(action){

      case 0 :{ // status LED action
        int data = Wire.read(); // n'en a que si on recoit un write et pas un read
        if(data == 0){ // status = absent donc lumiere rouge
          digitalWrite(redLEDPin, HIGH);
          digitalWrite(yellowLEDPin, LOW);
        }else{
          digitalWrite(redLEDPin, LOW);
          digitalWrite(yellowLEDPin, HIGH);
        }
        break;}

      case 1 :{ // onboard LED action (WRITE)
        int data = Wire.read(); // n'en a que si on recoit un write et pas un read
        print("data = ", data);
        digitalWrite(ledPin, data);
        break;}

      case 4 :{
        int data = Wire.read();
        testServo();
        break;}

    }
  }
}

void requestEvent(){
  
  int request = Wire.read(); // read the first byte to know the action needed to be done
  print("request = ", request);

  switch(request){
    case 2:{
      Wire.write(true);
      break;
    }

    case 3:{
      Wire.write("hello");
      break;
    }
  }
}

void testServo(){
  for (pos = 0; pos <= 180; pos += 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
    myservo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(5);                       // waits 15ms for the servo to reach the position
  }
  for (pos = 180; pos >= 0; pos -= 1) { // goes from 180 degrees to 0 degrees
    myservo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(5);                       // waits 15ms for the servo to reach the position
  }
}


void print(String text,int data){
  Serial.print(text);Serial.print(data);
  Serial.print("\n");
}

void loop() {
  delay(100);
}