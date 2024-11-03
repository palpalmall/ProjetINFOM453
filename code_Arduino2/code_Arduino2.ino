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

// LED on pin 13
const int ledPin = 6; // onboard pin
const int REDPin = 4; //PWM PIN
const int GREENPin = 3; //PWM PIN
const int BLUEPin = 2; //PWM PIN
const bool teteFrappe = true;
const int analogPin = 0;

Servo myservo;
const int servoPin = 5; // PWM PIN
int pos = 0;
 
void setup() {
  Serial.begin(9600); //to print analog data

  // Join I2C bus as slave with address 8
  Wire.begin(0x3C);
  
  // Call receiveEvent when data received                
  Wire.onReceive(receiveEvent);

  // Call receiveEvent when data received                
  Wire.onRequest(requestEvent);

  
  // Setup pin 13 and RGB pins as output and turn LEDs off
  pinMode(ledPin, OUTPUT);
  pinMode(REDPin, OUTPUT);
  pinMode(GREENPin, OUTPUT);
  pinMode(BLUEPin, OUTPUT);
  // on eteind les pins RGB
  digitalWrite(REDPin, LOW);
  digitalWrite(GREENPin, LOW);
  digitalWrite(BLUEPin, LOW);

  myservo.attach(servoPin);
  myservo.write(0);
}
 
// Function that executes whenever data is received from master
void receiveEvent(int howMany) {
  if (Wire.available() > 0){
  digitalWrite(REDPin, LOW);
  digitalWrite(GREENPin, LOW);
  digitalWrite(BLUEPin, LOW);
  
  // on recoit dans l'ordre : [action, data=optional]
  int action = Wire.read(); // read the first byte to know the action needed to be done
  print("action = ", action);

  switch(action){
    
    case 0 :{ // status LED action (WRITE)
      int data = Wire.read(); // n'en a que si on recoit un write et pas un read
      if(data == 0){ // status = absent donc lumiere rouge
        analogWrite(REDPin, 255);
        digitalWrite(GREENPin, LOW);
        digitalWrite(BLUEPin, LOW);
      }else{ // status = present donc lumiere verte
        analogWrite(GREENPin, 255);
        digitalWrite(REDPin, LOW);
        digitalWrite(BLUEPin, LOW);
      }
      break;}

    case 1 :{ // onboard LED action (WRITE)
      int data = Wire.read(); // n'en a que si on recoit un write et pas un read
      print("data = ", data);
      digitalWrite(ledPin, data);
      break;}
    
    // case 2 :{ // send if the head has been smashed or not (READ)
    //   int data = Wire.read();
    //   print("in it = ", data);
    //   Wire.write(true);
    //   break;}
    // JE NE COMPREND PAS PQ CA ENVOIE BIEN TRUE QUE QUAND CA VIENT DE ONREQUEST ET PAS ONRECEIVE
    }
  }

}

void requestEvent(){
  
  int request = Wire.read(); // read the first byte to know the action needed to be done
  // request vaut tjrs -1 alors que sur le MKR1000, on recoit bien la valeur envoyé par le master
  Serial.print("request= "); Serial.print(request);
  Serial.print("\n");
  Wire.write(true);         // respond with message of 6 bytes as expected by master
  
}

void print(String text,int data){
  Serial.print(text);Serial.print(data);
  Serial.print("\n");
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

void loop() {
  delay(100);
  
  //Serial.print(analogRead(analogPin));
  //Serial.print("\n");
}