    #include <LiquidCrystal.h>

    // initialize the library with the numbers of the MKR Pin: ( From D0 to D5 )
    LiquidCrystal lcd(0, 1, 2, 3, 4, 5);

    void setup() {
    // setup the LCD’s number of columns and rows:
    lcd.begin(16, 2); // If you have 20×4 LCD user (20,4)
    }
    void loop() {

    lcd.setCursor(0, 0); // Lcd first row is 0
    lcd.print("ArduinoAir");
    lcd.setCursor(0, 1);// Lcd second row is 1
    lcd.print("Pollution");
    }