//PixelShot 300 Arduino Code
//2 December 2017
//Nicholas Vadivelu

#define LED1 13
#define POT_PIN 0


void setup() { //this code runs once at the start
  Serial.begin(115200); //begings the serial communication at the maximum speed

  pinMode(LED1, OUTPUT); 
  pinMode(POT_PIN, INPUT);
}

void loop() { //this code loops
  Serial.println(analogRead(POT_PIN)); //reads input from the POT_PIN
  
  if (Serial.available()) { //if there is data to be sent to the arduino from serial
    int maxBuff = 1; //size of buffer
    int i = 0; //index variable
    char buff[maxBuff]; //creates a char array to hold input
    while(Serial.available()){ //while there is more data
      unsigned char c = Serial.read(); //gets character input
      buff[i] = c; //stores character in buff
      if (i++ > maxBuff+1) break; //stops code if index gets too high
    }
    
    digitalWrite(LED1, (buff[0]-'0'));
  }
  delay(1);
}
