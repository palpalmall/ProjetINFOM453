    #include <LiquidCrystal.h>

    // initialize the library with the numbers of the MKR Pin: ( From D0 to D5 )
    LiquidCrystal lcd(0, 1, 2, 3, 4, 5);

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

void setup() {

  // initialize LCD and set up the number of columns and rows:

  lcd.begin(16, 2);
 

  // create a new character

  lcd.createChar(0, heart);

  // create a new character

  lcd.createChar(1, smiley);

  // create a new character
  
  lcd.createChar(3, armsDown);

  // create a new character

  lcd.createChar(4, armsUp);



  // set the cursor to the top left

  lcd.setCursor(0, 0);


}

void loop() {
  lcd.setCursor(0, 1);
  lcd.write(byte(1));

  lcd.setCursor(4, 1);
  lcd.write(byte(4));

  lcd.setCursor(8, 1);
  lcd.write(byte(3));

  lcd.setCursor(12, 1);
  lcd.write(byte(4));

  lcd.setCursor(15, 1);
  lcd.write(byte(1));


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

//Blinking
  delay(1250);
  lcd.noDisplay();
  delay(500);
  lcd.display();
  delay(500);
  lcd.noDisplay();
  delay(500);
  lcd.display();
  delay(500);


  //clearing for next loop
  lcd.clear();
}