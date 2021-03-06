//PixelShot 300 Arduino Code
//2 December 2017
//Nicholas Vadivelu

#include <Utility.h>
#include <TimedAction.h>

//*****************DEFINE ALL THE NECESSARY PINS
#define SENSOR          0 //sensor

//Motors
#define STEPS_NEMA8   200 //define number of steps for both motors
#define STEPS_NEMA17  400
int pins_NEMA8[] = {1, 2, 3, 4}; //define the pins for the motors
int pins_NEMA17[] = {5, 6, 7, 8};
int A = 0; //define ABCD pins to use in the doStep methods
int B = 0;
int C = 0;
int D = 0;

long duty = 50; //three variables used in doStep
int waitMicroSeconds = 500;
int pulseCount = 5;

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

int xPos, yPos, lum; //position of the sensor

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

void turnNEMA8 (char dir) {
  if (dir == '0'){ //dir == 0 means no turning, 1 CW, 
    return;
  }
  //pins that need to be affected are from the nema 8
  A = pins_NEMA8[0];
  B = pins_NEMA8[1];
  C = pins_NEMA8[2];
  D = pins_NEMA8[3];
  
  do16Steps(1, dir == '1');
  A = B = C = D = 0;
}

void turnNEMA17 (char dir) {
  if (dir == '0') {
    return;
  }

  //pins from nema 17
  A = pins_NEMA17[0];
  B = pins_NEMA17[1];
  C = pins_NEMA17[2];
  D = pins_NEMA17[3];
  
  do8Steps(1, dir == '1');
  A = B = C = D = 0;
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

//***********************steps needed for microstepping
void one(){
  digitalWrite(A, HIGH);   
  digitalWrite(B, LOW);   
  digitalWrite(C, HIGH);   
  digitalWrite(D, LOW);   
}

void two(){
  digitalWrite(A, HIGH);   
  digitalWrite(B, LOW);   
  digitalWrite(C, LOW);   
  digitalWrite(D, HIGH);   
}

void three(){
  digitalWrite(A, LOW);   
  digitalWrite(B, HIGH);   
  digitalWrite(C, LOW);   
  digitalWrite(D, HIGH);   
}

void four(){
  digitalWrite(A, LOW);   
  digitalWrite(B, HIGH);   
  digitalWrite(C, HIGH);   
  digitalWrite(D, LOW);   
}


void oneB(){
  digitalWrite(A, HIGH);   
  digitalWrite(B, LOW);   
  digitalWrite(C, LOW);   
  digitalWrite(D, LOW);   
}

void twoB(){
  digitalWrite(A, LOW);   
  digitalWrite(B, LOW);   
  digitalWrite(C, LOW);   
  digitalWrite(D, HIGH);   
}

void threeB(){
  digitalWrite(A, LOW);   
  digitalWrite(B, HIGH);   
  digitalWrite(C, LOW);   
  digitalWrite(D, LOW);   
}

void fourB(){
  digitalWrite(A, LOW);   
  digitalWrite(B, LOW);   
  digitalWrite(C, HIGH);   
  digitalWrite(D, LOW);   
}


// main routine to microstep
void doStep(int st){
  
  long dt1 = waitMicroSeconds * duty / 100;
  long dt2 = waitMicroSeconds * (100-duty) / 100;

  for (int j = 0; j < pulseCount; j++){
    switch (st){
    case 1: one();break;
    case 2: two();break;
    case 3: three();break;
    case 4: four();break;
    case 11: oneB();break;
    case 12: twoB();break;
    case 13: threeB();break;
    case 14: fourB();break;

    case 21: one();break;
    case 22: two();break;
    case 23: three();break;
    case 24: four();break;
    case 31: oneB();break;
    case 32: twoB();break;
    case 33: threeB();break;
    case 34: fourB();break;

    }

    delayMicroseconds(dt1);

    switch (st){
    case 1: one();break;
    case 2: two();break;
    case 3: three();break;
    case 4: four();break;
    case 11: oneB();break;
    case 12: twoB();break;
    case 13: threeB();break;
    case 14: fourB();break;

    case 21: oneB();break;
    case 22: twoB();break;
    case 23: threeB();break;
    case 24: fourB();break;
    case 31: two();break;
    case 32: three();break;
    case 33: four();break;
    case 34: one();break;
    }
    delayMicroseconds(dt2);
    
  }
}

// disable motor
void motorOff(){
  /* Important note:
       Turning off the motor will make it go into a 'rest' state. 
       When using microsteps (or even full steps), this may not be the last active step. 
       So using this routine may change the position of the motor a bit.
  */
  
  digitalWrite(A, LOW);   
  digitalWrite(B, LOW);   
  digitalWrite(C, LOW);   
  digitalWrite(D, LOW);   
}

// full stepping 4 steps :
void do4Steps(int cnt, boolean forwards){
  for (int i = 0; i < cnt; i++){
    duty = 50;
    if (forwards)
      {for (int j = 1; j <= 4; j++){doStep(j);}}
    else
      {for (int j = 4; j >= 1; j--){doStep(j);}}

  }
}

// half stepping 8 steps :
void do8Steps(int cnt, boolean forwards){
  const int list[] = {1,11,2,12,3,13,4,14};
  for (int i = 0; i < cnt; i++){
    duty = 50;
    if (forwards)
      {for (int j = 0; j <= 7; j++){doStep(list[j]);}}
    else
      {for (int j = 7; j >= 0; j--){doStep(list[j]);}}
  }
}


// microstepping 16 steps :
void do16Steps(int cnt, boolean forwards){

  const int list[] = {1,21,11,31,2,22,12,32,3,23,13,33,4,24,14,34};
  for (int i = 0; i < cnt; i++){
    duty = 50;
    if (forwards)
      {for (int j = 0; j <= 15; j++){doStep(list[j]);}}
    else
      {for (int j = 15; j >= 0; j--){doStep(list[j]);}}
  }  
}



void readAndMove() {
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

    //get numeric values from the character input
    xPos = (buff[2] - '0')*1000 + (buff[3] - '0')*100 + (buff[4] - '0')*10 + buff[5] - '0';
    yPos = (buff[6] - '0')*1000 + (buff[7] - '0')*100 + (buff[8] - '0')*10 + buff[9] - '0';
    lum = (buff[10] - '0')*1000 + (buff[11] - '0')*100 + (buff[12] - '0')*10 + buff[13] - '0';
  }
}

//create a couple timers that will fire repeatedly every x ms
TimedAction readMoveThread = TimedAction(700,readAndMove);

void loop() { //this code loops
    readMoveThread.check();
    
    //display the numbesr
    displayNumber(xPos, dig11, dig12, dig13, dig14, A_1, B1, C1, D1);
    displayNumber(yPos, dig21, dig22, dig23, dig24, A_2, B2, C2, D2);
    displayNumber(lum, dig31, dig32, dig33, dig34, A_3, B3, C3, D3);
}
