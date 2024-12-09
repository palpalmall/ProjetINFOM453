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
const int buttonPin = A1; // optionnal
const int ledPin = 1; // ping pin
const int REDPin = 4; //PWM PIN
const int GREENPin = 3; //PWM PIN
const int BLUEPin = 2; //PWM PIN
bool smashedHead = false;

 
void setup() {
  Serial.begin(4800); //to print analog data

  // Join I2C bus as slave with address 8
  Wire.begin(0x3C);
  
  // Call receiveEvent when data received                
  Wire.onReceive(receiveEvent);

  // Call receiveEvent when data received                
  Wire.onRequest(requestEvent);

  // For the button, to click on figurine's head
  pinMode(buttonPin, INPUT_PULLUP);  
  
  // Setup pin 1 and RGB pins as output and turn LEDs off
  pinMode(ledPin, OUTPUT);
  pinMode(REDPin, OUTPUT);
  pinMode(GREENPin, OUTPUT);
  pinMode(BLUEPin, OUTPUT);
  // on eteind les pins RGB
  shutDownRGBLED();
  digitalWrite(ledPin, LOW); //on eteind la onboard led
}
 
// Function that executes whenever data is received from master
void receiveEvent(int howMany) {
  if (Wire.available() > 0){
    shutDownRGBLED();

    // on recoit dans l'ordre : [action, data=optional]
    int action = Wire.read(); // read the first byte to know the action needed to be done
    print("action = ", action);

    switch(action){

      case 0 :{ // status LED action (WRITE)
        int data = Wire.read(); // n'en a que si on recoit un write et pas un read
        print("data = ", data);
        if(data == 0){ // status = absent donc lumiere rouge
          analogWrite(REDPin, 255);
          analogWrite(GREENPin, LOW);
          analogWrite(BLUEPin, LOW);
        }
        else if(data == 1){
          analogWrite(REDPin, 255);
          analogWrite(GREENPin, 255);
          analogWrite(BLUEPin, LOW);
        }
        else if{ // status = present donc lumiere verte
          analogWrite(GREENPin, 255);
          analogWrite(REDPin, LOW);
          analogWrite(BLUEPin, LOW);
        }
        break;}

      case 1 :{ // ping LED (WRITE)
        int data = Wire.read(); // n'en a que si on recoit un write et pas un read
        print("data = ", data);
        pingLED();
        break;}
    }
  }
}

void requestEvent(){ // launched when master makes a read (request)
  
  int request = Wire.read(); // read the first byte to know the action needed to be done
  print("request = ", request);

  switch(request){
    case 2:{ // ask for a smashed head
      Wire.write(smashedHead);
      smashedHead = false;
      break;
    }

    case 3:{ // test, no use in project
      Wire.write("hello");
      break;
    }
  }
}

void shutDownRGBLED(){ // shut down rgb LED
  analogWrite(REDPin, LOW);
  analogWrite(GREENPin, LOW);
  analogWrite(BLUEPin, LOW);
}

void print(String text,int data){
  Serial.print(text);Serial.print(data);
  Serial.print("\n");
}

void blinkLED(){
  digitalWrite(ledPin, HIGH);
  delay(500);
  digitalWrite(ledPin, LOW);
  delay(500);
}

void pingLED(){
  for (int i = 0; i < 3; i++){
    blinkLED();
  }
}

void buttonPressed(){
  if (digitalRead(buttonPin) == LOW)
  {
    smashedHead = true;
  }
}

void loop() {
  delay(100);
  buttonPressed();
}