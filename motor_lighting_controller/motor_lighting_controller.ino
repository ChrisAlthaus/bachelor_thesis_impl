
#include <Stepper-MotorControl.h>
#include <AlaLedLite.h>         // modified Arduino Animation Library http://yaab-arduino.blogspot.de/p/ala.html
#include <Debugger.h> 
#include <String.h>

#include "Modes.h"
#include <SPI.h>

#define BAUD_RATE       9600 

#define LED_PIN         8       // D8
#define MESSAGE_LENGTH 100


MotorControl ctl;
AlaLedLite led;                   // LED Stripe
Debugger debug;                   // Debugger


char message[MESSAGE_LENGTH];
volatile byte pos;
volatile boolean process_it;
int errors =0;

boolean _hasNewTargetPosition = false;

int counter=1;
boolean flag=false;

 


void setup() {
  //Serial.begin(BAUD_RATE);
  
  // have to send on master in, *slave out*
  pinMode(MISO, OUTPUT);
  
  // turn on SPI in slave mode
  SPCR |= _BV(SPE);
  
  // get ready for an interrupt 
  pos = 0;   // buffer empty
  process_it = false;

  // turn on interrupts
  SPI.attachInterrupt();
  SPCR |= _BV(SPIE);
  
  //Pin configuration for stepper motor
  pinMode(DIRECTION_PIN, OUTPUT);
  pinMode(ENABLE_PIN, OUTPUT);
  pinMode(MS1_PIN, OUTPUT);
  pinMode(MS2_PIN, OUTPUT);
  pinMode(STEP_PIN, OUTPUT);
  digitalWrite(DIRECTION_PIN, HIGH);
  digitalWrite(ENABLE_PIN, HIGH);
  digitalWrite(MS1_PIN, HIGH);
  digitalWrite(MS2_PIN, LOW);

  //Initialize sample intervalls for led patterns
  led.initLightPatternIntervalls();
  //Setup led stripes
  led.initBarStripes(LED_PIN);

}

void loop() {
  //check if message is received
  if (process_it){
    message [pos] = 0;
    pos = 0;
    
    process_it = false;
	  //Serial.println("message=");
	  //Serial.println(message);
	  parseMessage();
  } 
  
  // Perfom animations:
  // 1. setAnimation
  // 2. runAnimation
  led.runAnimation();
}

// SPI interrupt routine
ISR (SPI_STC_vect)
{
 byte c = SPDR;  // grab byte from SPI Data Register
  // add to buffer if room
  if (pos < sizeof message)
    {
    message [pos++] = char(c);
    
    //Serial.println("get a byte");
    //Serial.println(c);
    if (c == '\n')
      process_it = true;
      
    }  
}  


void parseMessage() {
  int mode=message[0];
  
  //Serial.println("Parsing message....");
   
  if(mode == MODE.MOVE){
    int targetPosition=message[1];
    String stepMode="HALF";getStepMode(message[2]);
  
    if(isValidPosition(targetPosition)& stepMode != "INVALID"){
      _hasNewTargetPosition = true; // activate movement
      
	    doMovement(targetPosition,stepMode);
    }

    //debug.println("MOVE:", targetPosition); 
    //debug.println(stepMode); 
    
  }else if(mode == MODE.UP){
    int steps=getNumberFromBytes(message[1],message[2]);
    handleMove("UP",steps);
    
    //debug.println("UP", steps); 
  
  }else if(mode == MODE.DOWN){
    int steps=getNumberFromBytes(message[1],message[2]);
    handleMove("DOWN",steps);
     
    //debug.println("DOWN", steps); 
  
  }else if(mode == MODE.INIT){
    String initMode= getInitMode(message[1]);
     //debug.println("Initialization:");
     //debug.println(initMode);
   
    if(initMode=="CALIBRATE"){      
      ctl.calibrate();
      //debug.print("... OK");
      //debug.println("calibrate");
      
    }else{ /* TOP or BOTTOM initialization*/
      int positionSteps=message[2];

      if(initMode=="TOP"){
        //init top position
        if(isValidStepNumber(positionSteps)){
         ctl.setMaxPosition(positionSteps);
        // debug.println("INIT:TOP/", positionSteps);  
        }else{
          //debug.println("No valid step number"); 
        }
         
      }else if(initMode=="BOTTOM"){
        //init bottom position
         if(isValidStepNumber(positionSteps)){
          ctl.setMinPosition(positionSteps);
          //debug.println("INIT:BOTTOM/", positionSteps);  
         }else{
          //debug.println("No valid step number"); 
         }
      }
    }
    
  }else if(mode == MODE.ANI){
    int field= message[1];
	  int animationType=getValueBitMask(field,7,4);
	  int animationMode= getValueBitMask(field,3,0);
    String color=getColorHexValue(message[2],message[3],message[4]);
    
    
	  int rgba[4] = { 0, 0, 0, -1};
	  setRGBA(rgba,color,-1);
	
  	if(animationType == ANIMATIONTYPES.SINGLE){
  		int durationOneCycle=getNumberFromBytes(message[5],message[6]);
  		int field= message[7];
  		String side=getSide(getValueBitMask(field,7,4));
  		int ledNumber=getValueBitMask(field,3,0);
  		
  		setSingleAnimation(animationMode, side, ledNumber, durationOneCycle, rgba);
  		
  		//Serial.println("ANI");
      //Serial.println("animationMode=");
      //Serial.println(animationMode);
      //Serial.println("color=");
      //Serial.println(color);
      //Serial.println("brightness=");
      //Serial.println(brightness);
      //Serial.println("duration=");
      //Serial.println(durationOneCycle);
      //Serial.println("side=");
      //Serial.println(side);
      //Serial.println("led=");
      //Serial.println(ledNumber);      
  	
  	}else if(animationType == ANIMATIONTYPES.OVERALL){ 
  	  int speedAnimation=getNumberFromBytes(message[5],message[6]);
  		int brightness=message[8];
      
      rgba[3]=brightness;
      
  		setAnimation(animationMode, rgba, speedAnimation);
  		
  		//Serial.println("ANI");
      //Serial.println("animationMode=");
      //Serial.println(animationMode);
      //Serial.println("color=");
      //Serial.println(color);
      //Serial.println("brightness=");
      //Serial.println(brightness);
      //Serial.println("speedAnimation=");
      //Serial.println(speedAnimation);
  	}
  	 
  	
   }else if(mode == MODE.LIGHT){  
    int field= message[1];
    String side=getSide(getValueBitMask(field,7,6));
    int operation=getValueBitMask(field,5,4);
    int led=getValueBitMask(message[1],3,0);
    String color=getColorHexValue(message[2],message[3],message[4]);
 
    int rgba[4] = { 0, 0, 0, -1};
    
    // Convert HEX to RGB
    if ( operation == LIGHTOPERATIONS.ADD ) {
  		rgba[0] = hexToDec(color.substring(0,2));
  		rgba[1] = hexToDec(color.substring(2,4));
  		rgba[2] = hexToDec(color.substring(4,6));
		
    }

    
	  // Update Lighting Pattern
    setLighting(side, operation, led, rgba);
    
    
    //Serial.println("LIGHT");
    //Serial.println(field);
    //Serial.println("side=");
  	//Serial.println(side);
  	//Serial.println("operation=");
    //Serial.println(operation);
    //Serial.println("led=");
	  //Serial.println(led);
    //Serial.println("color=");
  	//Serial.println(color);
    //Serial.println("brightness=");
    //Serial.println(rgba[3]);
	
  }else if(mode == MODE.BRIGHTNESS){
    int brightness=message[1];
    led.setBrightness(AlaColor(brightness, brightness, brightness));
    
  }else if(mode == MODE.LEVEL){

    int ledLevel=message[1];
    String color=getColorHexValue(message[2],message[3],message[4]);
    int brightness=message[5];
	
	  int rgba[4] = { 0, 0, 0, -1};
  
	  // Convert HEX to RGB
    rgba[0] = hexToDec(color.substring(0,2));
    rgba[1] = hexToDec(color.substring(2,4));
    rgba[2] = hexToDec(color.substring(4,6));
      
  
    // Update Leds
    setLighting("A",LIGHTOPERATIONS.ADD,ledLevel,rgba);
    setLighting("B",LIGHTOPERATIONS.ADD,ledLevel,rgba);
    setLighting("C",LIGHTOPERATIONS.ADD,ledLevel,rgba);
    setLighting("D",LIGHTOPERATIONS.ADD,ledLevel,rgba);
    
    //Serial.println("LEVEL");
    //Serial.println(ledLevel);
    //Serial.println(color);
    //Serial.println(brightness);
  }

  memset(message,0,MESSAGE_LENGTH);
  
}

void handleMove(String mode,int steps){
  ctl.setStepMode(HALF_STEP);
    if(isValidStepNumber(steps)){  
      if(mode=="UP"){
        ctl.moveUp(steps);
      }else if(mode=="DOWN"){
        ctl.moveDown(steps);
      }
    }else{
      //debug.print("[Default Value: 400 Steps]");
      if(mode=="UP"){
        ctl.moveUp(400);
      }else if(mode=="DOWN"){
        ctl.moveDown(400);
      }
    }
	
    //debug.println("Current Position: ", ctl.getPosition() );
}


void setLighting(String side, int operation, int num, int rgba[4]) {
  
  if (operation == LIGHTOPERATIONS.ADD){
    led.setLed(side, num, ALA_ON, AlaColor(rgba[0], rgba[1], rgba[2]));
  }else if(operation == LIGHTOPERATIONS.REMOVE){
    led.setLed(side, num, ALA_OFF, AlaColor(0, 0, 0));
  }else if (operation == LIGHTOPERATIONS.CLEARSIDE){
    led.setSide(side, ALA_OFF, AlaColor(0, 0, 0));
  }

  led.setCustomLighting(true);
}

void setSingleAnimation(int animation, String side, int ledNumber, int durationOneCycle, int rgba[4]){

  if(animation== ANIMATION.BLINK){
	
	  led.setAnimation(ALA_BLINK, side, ledNumber, durationOneCycle,AlaColor(rgba[0], rgba[1], rgba[2]));
  
  }else if(animation == ANIMATION.BEACON){
	
	  led.setAnimation(ALA_BEACON, side, ledNumber, durationOneCycle,AlaColor(rgba[0], rgba[1], rgba[2]));
  
  }else if(animation == ANIMATION.STAIRCASE){
	
	  led.setAnimation(ALA_STAIRCASE, side, ledNumber, durationOneCycle,AlaColor(rgba[0], rgba[1], rgba[2]));
  
  }else if(animation == ANIMATION.PULSESLOW){
	
	  led.setAnimation(ALA_PULSESLOW, side, ledNumber, durationOneCycle,AlaColor(rgba[0], rgba[1], rgba[2]));
  
  }else if(animation == ANIMATION.TRANSMISSIONFB){
	  led.setAnimation(ALA_TRANSMISSONFB, side, ledNumber, durationOneCycle,AlaColor(rgba[0], rgba[1], rgba[2]));
  
  }else if(animation == ANIMATION.OFF){
	
	  led.setAnimation(ALA_SINGLEANIMATIONOFF, side, ledNumber, durationOneCycle,AlaColor(rgba[0], rgba[1], rgba[2]));
	  return; //maybe no animation->custom lighting = true
  
  }
  
  /*debug.println("//// -------------------- ////");
  debug.println("Animation: ", animation);
  debug.println("Color: ", rgba[0]);
    debug.print(",", rgba[1]);
    debug.print(",", rgba[2]);
  debug.println("Brightness: ", rgba[3]);
  debug.println("Duration: ", durationOneCycle);
  debug.println("Side: ", side);
  debug.println("LED: ", ledNumber);
  debug.println("//// -------------------- ////");*/

  led.setCustomLighting(false);
}

void setAnimation(int animation, int rgba[4], int spd) {

  // Scale and set brightness
  int brightness = getScaledBrightness(rgba[3]);
  led.setBrightness(AlaColor(brightness, brightness, brightness));

  // Special duration-speed mapping for each animation
  int duration;
  
  if (animation == ANIMATION.OFF) {
    
    led.setAnimation(ALA_OFF, 0, AlaColor(0, 0, 0));
	  led.setCustomLighting(true);	//animations off
    return;
     
  } else if (animation == ANIMATION.ON) {
    
    led.setAnimation(ALA_ON, 0, AlaColor(rgba[0], rgba[1], rgba[2]));
     
  } else if (animation == ANIMATION.GLOW) {
  
    duration = map(spd, 0, 100, 20000, 1000);
    led.setAnimation(ALA_GLOW, duration, AlaColor(rgba[0], rgba[1], rgba[2]));
     
  } else if (animation == ANIMATION.UP) {
  
    duration = map(spd, 0, 100, 10000, 800);
    led.setAnimation(ALA_PIXELSMOOTHSHIFTRIGHT, duration, AlaColor(rgba[0], rgba[1], rgba[2]));
     
  } else if (animation == ANIMATION.DOWN) {
    
    duration = map(spd, 0, 100, 10000, 800);
    led.setAnimation(ALA_PIXELSMOOTHSHIFTLEFT, duration, AlaColor(rgba[0], rgba[1], rgba[2]));
    
  } else if (animation == ANIMATION.COM) {
    
    duration = map(spd, 0, 100, 10000, 800);
    led.setAnimation(ALA_COMET, duration, AlaColor(rgba[0], rgba[1], rgba[2]));
    
  } else if (animation == ANIMATION.BOUNCE) {
    
    duration = map(spd, 0, 100, 10000, 800);
    led.setAnimation(ALA_PIXELSMOOTHBOUNCE, duration, AlaColor(rgba[0], rgba[1], rgba[2]));
     
  } else if (animation == ANIMATION.MOVE) {
  
    duration = map(spd, 0, 100, 10000, 800);
    led.setAnimation(ALA_LARSONSCANNER, duration, AlaColor(rgba[0], rgba[1], rgba[2]));
  }
/*
  debug.println("//// -------------------- ////");
  debug.println("Animation: ", animation);
  debug.println("Color: ", rgba[0]);
    debug.print(",", rgba[1]);
    debug.print(",", rgba[2]);
  debug.println("Brightness: ", rgba[3]);
  debug.println("Speed: ", spd);
  debug.println("Duration: ", duration);
  debug.println("//// -------------------- ////");
*/
  led.setCustomLighting(false);
}



//b1=MSB and b3=LSB
String getColorHexValue(byte b1, byte b2, byte b3){
  uint32_t sum= 0;
  int lengthColorHex=6;

  sum |= (uint32_t)b3<<0;
  sum |= (uint32_t)b2<<8;
  sum |= (uint32_t)b1<<16;
  
  String colorValue=String(sum,HEX);
  int length = colorValue.length();

  for(int i=0; i<(lengthColorHex-length);i++){
    colorValue= "0"+colorValue;
  }
  
return colorValue;
}

//Returns integer value of bit position mask
//Bit Positions: 7,6,5,4,3,2,1,0
uint8_t getValueBitMask(uint8_t value, int toMSB, int fromLSB){ 
  uint8_t bitMask = 0;
  
  for(int i=fromLSB; i<toMSB+1;i++){
    bitSet(bitMask,i);
  }
 
  value&= bitMask;
  value>>= fromLSB;

  return value;
}

//Returns integer value of appended bytes
int getNumberFromBytes(uint8_t MSB, uint8_t LSB){
	int result=LSB + (MSB<<8);
	
	return result;
}

// Convert color HEX #FF to RGB 255
unsigned int hexToDec(String hexString) {
  
  unsigned int decValue = 0;
  int nextInt;
  
  for (int i = 0; i < hexString.length(); i++) {
    
    nextInt = int(hexString.charAt(i));
    if (nextInt >= 48 && nextInt <= 57) nextInt = map(nextInt, 48, 57, 0, 9);
    if (nextInt >= 65 && nextInt <= 70) nextInt = map(nextInt, 65, 70, 10, 15);
    if (nextInt >= 97 && nextInt <= 102) nextInt = map(nextInt, 97, 102, 10, 15);
    nextInt = constrain(nextInt, 0, 15);
    
    decValue = (decValue * 16) + nextInt;
  }
  
  return decValue;
}

//Sets rgba array from hex color
void setRGBA(int rgba[4],String color,int brightness){
	// Convert HEX to RGB
    rgba[0] = hexToDec(color.substring(0,2));
    rgba[1] = hexToDec(color.substring(2,4));
    rgba[2] = hexToDec(color.substring(4,6));
    rgba[3] = brightness;
    /*debug.println("R:", rgba[0]);
    debug.print("G:", rgba[1]);
    debug.print("B:", rgba[2]);
    debug.print("Brightness:", rgba[3]);*/ 

}

//function for testing button and ir sensor
void testButtons(){
  debug.println("Button1=",ctl.getButton1Value());
  debug.println("Button2=",ctl.getButton2Value());
  debug.println("IR=",ctl.getIRValue());

  delay(1000);
}

void doMovement(unsigned int targetPosition, String stepMode) {
  
    if (stepMode == "FULL") {    
      ctl.moveTo( targetPosition, SPEED.FULL);    
    } else if (stepMode == "HALF") {
      ctl.moveTo( targetPosition, SPEED.HALF);
    } else if (stepMode == "QUARTER") {
      ctl.moveTo( targetPosition, SPEED.QUARTER);
    } else if (stepMode == "EIGTHTH") {
      ctl.moveTo( targetPosition, SPEED.EIGTHTH);
    }

    _hasNewTargetPosition = false;
  
}


String getStepMode(int speedNumber){
  if(speedNumber == SPEED.FULL){
    return "FULL";
  }else if(speedNumber == SPEED.HALF){
    return "HALF";
  }else if(speedNumber == SPEED.QUARTER){
    return "QUARTER";
  }else if(speedNumber == SPEED.EIGTHTH){
    return "EIGTHTH";
  }
  return "INVALID";
}


String getInitMode(int initModeNumber){
  if(initModeNumber == INIT.TOP){
    return "TOP";
  }else if(initModeNumber == INIT.BOTTOM){
    return "BOTTOM";
  }else if(initModeNumber == INIT.CALIBRATE){
    return "CALIBRATE";
  }
  return "INVALID";
}


String getAnimationMode(int animationNumber){
  if(animationNumber == ANIMATION.BLINK){
    return "BLINK";
  }else if(animationNumber == ANIMATION.BEACON){
    return "BEACON";
  }else if(animationNumber == ANIMATION.STAIRCASE){
    return "STAIRCASE";
  }else if(animationNumber == ANIMATION.PULSESLOW){
    return "PULSESLOW";
  }else if(animationNumber == ANIMATION.TRANSMISSIONFB){
    return "TRANSMISSIONFB";
  }else if(animationNumber == ANIMATION.ON){
    return "ON";
  }else if(animationNumber == ANIMATION.OFF){
    return "OFF";
  }else if(animationNumber == ANIMATION.UP){
    return "UP";
  }else if(animationNumber == ANIMATION.DOWN){
    return "DOWN";
  }else if(animationNumber == ANIMATION.COM){
    return "COM";
  }else if(animationNumber == ANIMATION.BOUNCE){
    return "BOUNCE";
  }else if(animationNumber == ANIMATION.MOVE){
    return "MOVE";
  }else if(animationNumber == ANIMATION.GLOW){
    return "GLOW";
  }
  return "INVALID";
}


String getAnimationType(int animationTypeNumber){
	if(animationTypeNumber == ANIMATIONTYPES.SINGLE){
		return "SINGLE";
	}else if(animationTypeNumber == ANIMATIONTYPES.SINGLE){
		return "OVERALL";
	}
}

String getSide(int sideNumber){
  if(sideNumber == SIDE.A){
    return "A";
  }else if(sideNumber == SIDE.B){
    return "B";
  }else if(sideNumber == SIDE.C){
    return "C";
  }else if(sideNumber == SIDE.D){
    return "D";
  }
  
  return "INVALID";
}

String getOperation(int operationNumber){
  if(operationNumber == LIGHTOPERATIONS.ADD){
    return "+";
  }else if(operationNumber == LIGHTOPERATIONS.REMOVE){
    return "-";
  }else if(operationNumber == LIGHTOPERATIONS.CLEARSIDE){
    return "*";
  }
  
  return "INVALID";
}

int getScaledBrightness(int percent){
  return map(percent,0,100,0,255);
}

boolean isValidPosition(int positionValue){
  return (positionValue>=0 & positionValue <=100);
}

boolean isValidStepNumber(int stepNumber){
   return(stepNumber>=0 & stepNumber <= 16000);
} 





