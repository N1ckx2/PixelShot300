//PixelShot 300 Arduino Code
//2 December 2017
//Nicholas Vadivelu

#include <Stepper.h>

//*****************DEFINE ALL THE NECESSARY PINS
#define SENSOR          0 //sensor

//Motors
#define STEPS_NEMA8   200 //define number of steps for both motors
#define STEPS_NEMA17  400
int pins_NEMA8[] = {1, 2, 3, 4}; //define the pins for the motors
int pins_NEMA17[] = {5, 6, 7, 8};

Stepper nema8(STEPS_NEMA8, pins_NEMA8[0], pins_NEMA8[1], pins_NEMA8[2], pins_NEMA8[3]); //create the steppers
Stepper nema17(STEPS_NEMA17, pins_NEMA17[0], pins_NEMA17[1], pins_NEMA17[2], pins_NEMA17[3]);

//7-segment displays
#define DISPLAY_BRIGHTNESS  1000
#define DIGIT_ON  HIGH
#define DIGIT_OFF  LOW

#define A_1 4
#define B1  1
#define C1  2
#define D1  3

#define A_2 4
#define B2  1
#define C2  2
#define D2  3

#define A_3 4
#define B3  1
#define C3  2
#define D3  3

int dig11 = 13; //PWM Display pin 1
int dig12 = 12; //PWM Display pin 2
int dig13 = 11; //PWM Display pin 6
int dig14 = 10; //PWM Display pin 8

int dig21 = 13; //PWM Display pin 1
int dig22 = 12; //PWM Display pin 2
int dig23 = 11; //PWM Display pin 6
int dig24 = 10; //PWM Display pin 8

int dig31 = 13; //PWM Display pin 1
int dig32 = 12; //PWM Display pin 2
int dig33 = 11; //PWM Display pin 6
int dig34 = 10; //PWM Display pin 8

int xPos, yPos, lum;

void setup() { //this code runs once at the start
  Serial.begin(115200); //begings the serial communication at the maximum speed

  //**************ACTIVATE NECESSARY PINS
  pinMode(SENSOR, INPUT);//sensor pin

  //NEMA17 and 8 pins
  for (int i = 0 ; i < 4; i++){ 
    pinMode(pins_NEMA8[i], OUTPUT);
    pinMode(pins_NEMA17[i], OUTPUT);
  }
  xPos = 0;
  yPos = 0;
  lum = 0;

  //set up sensor stuff
  pinMode(A1, OUTPUT);
  pinMode(B1, OUTPUT);
  pinMode(C1, OUTPUT);
  pinMode(D1, OUTPUT);
  pinMode(A2, OUTPUT);
  pinMode(B2, OUTPUT);
  pinMode(C2, OUTPUT);
  pinMode(D2, OUTPUT);
  pinMode(A3, OUTPUT);
  pinMode(B3, OUTPUT);
  pinMode(C3, OUTPUT);
  pinMode(D3, OUTPUT);
  
  pinMode(dig11, OUTPUT);
  pinMode(dig12, OUTPUT);
  pinMode(dig13, OUTPUT);
  pinMode(dig14, OUTPUT);
  pinMode(dig21, OUTPUT);
  pinMode(dig22, OUTPUT);
  pinMode(dig23, OUTPUT);
  pinMode(dig24, OUTPUT);
  pinMode(dig31, OUTPUT);
  pinMode(dig32, OUTPUT);
  pinMode(dig33, OUTPUT);
  pinMode(dig34, OUTPUT);
}

void loop() { //this code loops
  Serial.println(analogRead(SENSOR)); //reads input from the light senso
  
  if (Serial.available()) { //if there is data to be sent to the arduino from serial
    int maxBuff = 14; //size of buffer for three four-digit displays and two stepper motors
    int i = 0; //index variable
    char buff[maxBuff]; //creates a char array to hold input
    while(Serial.available()){ //while there is more data
      unsigned char c = Serial.read(); //gets character input
      buff[i] = c; //stores character in buff
      if (i++ > maxBuff+1) break; //stops code if index gets too high
    }
    turnNEMA8(buff[0]); //turn the motors 
    turnNEMA17(buff[1]);

    xPos = (buff[2] - '0')*1000 + (buff[3] - '0')*100 + (buff[4] - '0')*10 + buff[5] - '0';
    yPos = (buff[6] - '0')*1000 + (buff[7] - '0')*100 + (buff[8] - '0')*10 + buff[9] - '0';
    lum = (buff[10] - '0')*1000 + (buff[11] - '0')*100 + (buff[12] - '0')*10 + buff[13] - '0';
    
    displayNumber(xPos, dig11, dig12, dig13, dig14, A_1, B1, C1, D1);
    displayNumber(yPos, dig21, dig22, dig23, dig24, A_2, B2, C2, D2);
    displayNumber(lum, dig31, dig32, dig33, dig34, A_3, B3, C3, D3);
  }
}

void turnNEMA8 (char dir) {
  if (dir == '0'){ //dir == 0 means no turning
    return;
  }
  //NEED TO FIGURE OUT HOW TO QUARTERSTEP; FOR NOW JUST ONE STEP
  nema8.step(1*(dir == '1' ? 1:-1));
}

void turnNEMA17 (char dir) {
  if (dir == '0') {
    return;
  }
  //NEED TO FITURE OUT HOW TO HALFSTEP; FOR NOW JUST ONE STEP
  nema17.step(1*(dir == '1' ? 1:-1));
}

void displayNumber(int toDisplay, int digit1, int digit2, int digit3, int digit4, int A, int B, int C, int D) {
#define DISPLAY_BRIGHTNESS  1000
#define DIGIT_ON  HIGH
#define DIGIT_OFF  LOW
  long beginTime = millis();
  for(int digit = 4 ; digit > 0 ; digit--) {
    //Turn on a digit for a short amount of time
    switch(digit) {
    case 1:
      digitalWrite(digit1, DIGIT_ON);
      break;
    case 2:
      digitalWrite(digit2, DIGIT_ON);
      break;
    case 3:
      digitalWrite(digit3, DIGIT_ON);
      break;
    case 4:
      digitalWrite(digit4, DIGIT_ON);
      break;
    }
    //Turn on the right segments for this digit
    lightNumber(toDisplay % 10, A, B, C, D);
    toDisplay /= 10;
    delayMicroseconds(DISPLAY_BRIGHTNESS); 
    //Display digit for fraction of a second (1us to 5000us, 500 is pretty good)
    //Turn off all segments
    lightNumber(10, A, B, C, D); 
    //Turn off all digits
    digitalWrite(digit1, DIGIT_OFF);
    digitalWrite(digit2, DIGIT_OFF);
    digitalWrite(digit3, DIGIT_OFF);
    digitalWrite(digit4, DIGIT_OFF);
  }
  while( (millis() - beginTime) < 10) ; 
  //Wait for 20ms to pass before we paint the display again
}

//Given a number, turns on those segments
//If number == 10, then turn off number
void lightNumber(int numberToDisplay, int A, int B, int C, int D) {
  #define SEGMENT_ON  HIGH
  #define SEGMENT_OFF LOW
  
  switch (numberToDisplay){
  case 0:
    digitalWrite(A, SEGMENT_OFF);
    digitalWrite(B, SEGMENT_OFF);
    digitalWrite(C, SEGMENT_OFF);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 1:
    digitalWrite(A, SEGMENT_ON);
    digitalWrite(B, SEGMENT_OFF);
    digitalWrite(C, SEGMENT_OFF);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 2:
    digitalWrite(A, SEGMENT_OFF);
    digitalWrite(B, SEGMENT_ON);
    digitalWrite(C, SEGMENT_OFF);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 3:
    digitalWrite(A, SEGMENT_ON);
    digitalWrite(B, SEGMENT_ON);
    digitalWrite(C, SEGMENT_OFF);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 4:
    digitalWrite(A, SEGMENT_OFF);
    digitalWrite(B, SEGMENT_OFF);
    digitalWrite(C, SEGMENT_ON);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 5:
    digitalWrite(A, SEGMENT_ON);
    digitalWrite(B, SEGMENT_OFF);
    digitalWrite(C, SEGMENT_ON);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 6:
    digitalWrite(A, SEGMENT_OFF);
    digitalWrite(B, SEGMENT_ON);
    digitalWrite(C, SEGMENT_ON);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 7:
    digitalWrite(A, SEGMENT_ON);
    digitalWrite(B, SEGMENT_ON);
    digitalWrite(C, SEGMENT_ON);
    digitalWrite(D, SEGMENT_OFF);
    break;
  case 8:
    digitalWrite(A, SEGMENT_OFF);
    digitalWrite(B, SEGMENT_OFF);
    digitalWrite(C, SEGMENT_OFF);
    digitalWrite(D, SEGMENT_ON);
    break;
  case 9:
    digitalWrite(A, SEGMENT_ON);
    digitalWrite(B, SEGMENT_OFF);
    digitalWrite(C, SEGMENT_OFF);
    digitalWrite(D, SEGMENT_ON);
    break;
  case 10:
    digitalWrite(A, SEGMENT_OFF);
    digitalWrite(B, SEGMENT_OFF);
    digitalWrite(C, SEGMENT_OFF);
    digitalWrite(D, SEGMENT_OFF);
    break;
  }
}

