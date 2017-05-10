

//Servo myservo;  // create servo object to control a servo
//// twelve servo objects can be created on most boards
//
//int pos = 0;    // variable to store the servo position
//
//void setup() {
//  myservo.attach(9);  // attaches the servo on pin 9 to the servo object
//}
//
//void loop() {
//  for (pos = 0; pos <= 180; pos += 1) { // goes from 0 degrees to 180 degrees
//    // in steps of 1 degree
//    myservo.write(pos);              // tell servo to go to position in variable 'pos'
//    delay(15);                       // waits 15ms for the servo to reach the position
//  }
//  for (pos = 180; pos >= 0; pos -= 1) { // goes from 180 degrees to 0 degrees
//    myservo.write(pos);              // tell servo to go to position in variable 'pos'
//    delay(15);                       // waits 15ms for the servo to reach the position
//  }
//}


#include <Servo.h>

Servo servoPan;
Servo servoTilt;

char serialChar;
String serialCommand;

void parseCommand(String command) {

  // Serial.println(command);
  char commandCode = command.charAt(0);

  // servo command
  if(commandCode == 'S') {
    executeServoCommand(command);
  }
  else if(commandCode == 'M') {
    executeMotorCommand(command);
  }
  
  
}


int getInt(String cmd, int start, int& end) {

  end = cmd.indexOf(';', start);
  int val = cmd.substring(start, end).toInt();
  //end = e;
  return val;
}

/*
 * Executes a servo command.
 * @param command: command string in format "S<v0>;<v1>;\n" where
 * v0 and v1 are strings with the rotation of servo 0 and 1 respectively.
 */
void executeServoCommand(String command) {

  int end = 0;
  int v0 = getInt(command, 1, end);

  // Serial.print("end: ");
  // Serial.print(end);
  
  int v1 = getInt(command, end +1, end);

  // Serial.print("end: ");
  // Serial.print(end);

  // Serial.print("v0: ");
  // Serial.print(v0);
  // Serial.print(" v1: ");
  // Serial.println(v1);


  servoPan.write(v0);
  servoTilt.write(v1);
}


void executeMotorCommand(String command) {
  
}


void setup() {
  Serial.begin(115200);
  servoPan.attach(6);   // attaches the servo on pin 9 to the servo object
  servoTilt.attach(11); // attaches the servo on pin 10 to the servo object

  // initialize digital pin LED_BUILTIN as an output.
  pinMode(LED_BUILTIN, OUTPUT);
  
  Serial.print("Moni moni...");
}


void loop() {
  //Serial.print("Moni moni...");
  //delay(1000);

//  digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
//  delay(500);                       // wait for a second
//  digitalWrite(LED_BUILTIN, LOW);    // turn the LED off by making the voltage LOW
//  delay(500);                       // wait for a second
}

void serialEvent(){
  
  if (Serial.available()) {
    serialChar = Serial.read();
    if (serialChar == '\n') {
      parseCommand(serialCommand);
      serialCommand = "";
      Serial.flush();
    }
    else {
      serialCommand += serialChar;
    }
  }
}
