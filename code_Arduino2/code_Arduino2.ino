/*
  Arduino Slave for Raspberry Pi Master
  i2c_slave_ard.ino
  Connects to Raspberry Pi via I2C
  
  DroneBot Workshop 2019
  https://dronebotworkshop.com
*/
 
// Include the Wire library for I2C
#include <Wire.h>
#include <LiquidCrystal.h>

// LED on pin 13
const int buttonPin = A1;
const int ledPin = 0; // ping pin
const int REDPin = 3; //PWM PIN
const int GREENPin = 2; //PWM PIN
const int BLUEPin = 1; //PWM PIN
bool smashedHead = false;

// initialize the library with the numbers of the MKR Pin: ( From D0 to D5 )
const int rs = 10, en = 9, d4 = 8, d5 = 7, d6 = 6, d7 = 5;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

// optionnal
// byte heart[8] = {
//   0b00000,
//   0b01010,
//   0b11111,
//   0b11111,
//   0b11111,
//   0b01110,
//   0b00100,
//   0b00000
// };

// byte smiley[8] = {
//   0b00000,
//   0b00000,
//   0b01010,
//   0b00000,
//   0b00000,
//   0b10001,
//   0b01110,
//   0b00000
// };

// byte frownie[8] = {
//   0b00000,
//   0b00000,
//   0b01010,
//   0b00000,
//   0b00000,
//   0b00000,
//   0b01110,
//   0b10001
// };
 
void setup() {
  Serial.begin(9600); //to print analog data

  // Join I2C bus as slave with address 8
  Wire.begin(0x8);
  
  // Call receiveEvent when data received                
  Wire.onReceive(receiveEvent);

  // Call receiveEvent when data received                
  Wire.onRequest(requestEvent);

  // For the button, to click on figurine's head
  pinMode(buttonPin, INPUT_PULLUP);  
  
  // Setup RGB pins as output and turn LEDs off
  pinMode(REDPin, OUTPUT);
  pinMode(GREENPin, OUTPUT);
  pinMode(BLUEPin, OUTPUT);
  // on eteind les pins RGB
  shutDownRGBLED();

  // create a new character
  // lcd.createChar(0, heart);
  // lcd.createChar(1, smiley);
  // lcd.createChar(2, frownie);

  // initialize LCD and set up the number of columns and rows:
  lcd.begin(16, 2);
  lcd.setCursor(0, 0);
}
 
// Function that executes whenever data is received from master
void receiveEvent(int howMany) {
  if (Wire.available() > 0){

    // on recoit dans l'ordre : [action, data=optional]
    int action = Wire.read(); // read the first byte to know the action needed to be done
    print("action = ", action);

    switch(action){

      case 0 :{ // status LED action (WRITE)
        int data = Wire.read(); // n'en a que si on recoit un write et pas un read
        print("data = ", data);
        if(data == 0){ // status = absent donc lumiere rouge
          digitalWrite(REDPin, HIGH);
          digitalWrite(GREENPin, LOW);
          digitalWrite(BLUEPin, LOW);
        }
        else if(data == 1){
          digitalWrite(REDPin, HIGH);
          digitalWrite(GREENPin, HIGH);
          digitalWrite(BLUEPin, LOW);
        }
        else if(data == 2){ // status = present donc lumiere verte
          digitalWrite(GREENPin, 255);
          digitalWrite(REDPin, LOW);
          digitalWrite(BLUEPin, LOW);
        }
        break;}

      case 1 :{ //ping LED action (WRITE)
        int data = Wire.read(); // n'en a que si on recoit un write et pas un read
        print("data = ", data);
        digitalWrite(ledPin, data);
        break;}

      case 4 :{// lcd screen case
        //String text = "coucou les amis";
        //Serial.print(text);
        char myString[16];
        int id = 0;
        while(Wire.available() > 0){
          char data = Wire.read();
          myString[id] = data;
          id++; 
        }
        for(id; id <16; id++){
          myString[id] = ' ';
        }
        lcd.home();
        for (int ch = 0; ch <= sizeof(myString); ch++){
          lcd.print(myString[ch]);
        }
        lcd.autoscroll();
        lcd.setCursor(0,0);
        lcd.noAutoscroll(); //stoppping autoscroll when full text is printed
        break;}

    }
  }
}

void requestEvent(){ // launched when master makes a read (request)
  
  int request = Wire.read(); // read the first byte to know the action needed to be done
  print("request = ", request);

  switch(request){
    case 2:{
      Serial.print(smashedHead);
      Wire.write(smashedHead);
      smashedHead = false;
      break;
    }

    case 3:{ // no use in the project
      Wire.write("hello");
      break;
    }
  }
}

void shutDownRGBLED(){ // shut down rgb LED
  digitalWrite(REDPin, LOW);
  digitalWrite(GREENPin, LOW);
  digitalWrite(BLUEPin, LOW);
}

void print(String text,int data){
  Serial.print(text);Serial.print(data);
  Serial.print("\n");
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