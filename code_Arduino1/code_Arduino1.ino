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
#include <LiquidCrystal.h>

// LED on pin 13
const int buttonPin = A1;
const int ledPin = 6; // onboard pin
const int REDPin = 4; //PWM PIN
const int GREENPin = 3; //PWM PIN
const int BLUEPin = 2; //PWM PIN
bool teteFrappe = false;
//const int analogPin = 0;

// initialize the library with the numbers of the MKR Pin: ( From D0 to D5 )
LiquidCrystal lcd(5, 6, 7, 8, 9, 10);

byte heart[8] = {
  0b00000,
  0b01010,
  0b11111,
  0b11111,
  0b11111,
  0b01110,
  0b00100,
  0b00000
};

byte smiley[8] = {
  0b00000,
  0b00000,
  0b01010,
  0b00000,
  0b00000,
  0b10001,
  0b01110,
  0b00000
};

byte armsDown[8] = {
  0b00100,
  0b01010,
  0b00100,
  0b00100,
  0b01110,
  0b10101,
  0b00100,
  0b01010
};

byte armsUp[8] = {
  0b00100,
  0b01010,
  0b00100,
  0b10101,
  0b01110,
  0b00100,
  0b00100,
  0b01010
};

String text = "coucou les amis";

Servo myservo;
const int servoPin = 5; // PWM PIN
int pos = 0;
 
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
  
  // Setup pin 13 and RGB pins as output and turn LEDs off
  pinMode(ledPin, OUTPUT);
  pinMode(REDPin, OUTPUT);
  pinMode(GREENPin, OUTPUT);
  pinMode(BLUEPin, OUTPUT);
  // on eteind les pins RGB
  shutDownRGBLED();
  digitalWrite(ledPin, LOW); //on eteind la onboard led

  myservo.attach(servoPin);
  myservo.write(0);

  // initialize LCD and set up the number of columns and rows:
  lcd.begin(16, 2);
  lcd.clear();
  // // create a new character
  // lcd.createChar(0, heart);
  // // create a new character
  // lcd.createChar(1, smiley);
  // // create a new character
  // lcd.createChar(3, armsDown);
  // // create a new character
  // lcd.createChar(4, armsUp);
  // // set the cursor to the top left
  // lcd.setCursor(0, 0);
  // delay(1250);
  // //clearing for next loop
  // lcd.clear();

  //resetting cursor
  lcd.home();
  
  //printing text
  for (int ch = 0; ch <= text.length(); ch++){
    lcd.print(text[ch]);
    delay(250);
  }
  lcd.autoscroll();
  lcd.setCursor(0,0);
  lcd.noAutoscroll(); //stoppping autoscroll when full text is printed
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

      case 4 :{
        int data = Wire.read();
        testServo();
        break;}

    }
  }
}

void requestEvent(){ // launched when master makes a read (request)
  
  int request = Wire.read(); // read the first byte to know the action needed to be done
  print("request = ", request);

  switch(request){
    case 2:{
      Wire.write(teteFrappe);
      teteFrappe = false;
      break;
    }

    case 3:{
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

void buttonPressed(){
  if (digitalRead(buttonPin) == LOW)
  {
    teteFrappe = true;
  }
}

void loop() {
  delay(100);
  buttonPressed();
  //Serial.print(analogRead(analogPin));
  //Serial.print("\n");
}