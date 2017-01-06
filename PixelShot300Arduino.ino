//PixelShot 300 Arduino Code
//2 December 2017
//Nicholas Vadivelu

//*****************DEFINE ALL THE NECESSARY PINS
#define SENSOR          0; //sensor

//Motors
#define STEPS_NEMA8   200; //define number of steps for both motors
#define STEPS_NEMA17  400;
int[4] pins_NEMA8 = {1, 2, 3, 4}; //define the pins for the motors
int[4] pins_NEMA17 = {5, 6, 7, 8};
Stepper nema8(STEPS_NEMA8, pins_NEMA8[0], pins_NEMA8[1], pins_NEMA8[2], pins_NEMA8[3]); //create the steppers
Stepper nema17(STEPS_NEMA17, pins_NEMA17[0], pins_NEMA17[1], pins_NEMA17[2], pins_NEMA17[3]);

//7-segment displays
#define DISPLAY_BRIGHTNESS  1000
#define DIGIT_ON  HIGH
#define DIGIT_OFF  LOW
#define SEGMENT_ON  HIGH
#define SEGMENT_OFF LOW


void setup() { //this code runs once at the start
  Serial.begin(115200); //begings the serial communication at the maximum speed

  //**************ACTIVATE NECESSARY PINS
  pinMode(SENSOR, INPUT);//sensor pin

  //NEMA17 and 8 pins
  for (int i = 0 ; i < 4; i++){ 
    pinMode(pins_NEMA8[i], OUTPUT);
    pinMode(pins_NEMA17[i], OUTPUT);
  }
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
    sevenSegment(); //display for first 7-segment
    sevenSegment(); //display for second 7-segment
    sevenSegment(); //display for third 7-segment
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

sevenSegment(){
  //need explanation
}

