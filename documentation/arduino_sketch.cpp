/**
 * Author : Jose Luis Álvarez Quiroga
 *
 * This sketch allows Arduino to receive data via serial port
 * and act accordingly
 *
 * Example
 * DIGITAL-8-1
 */


String getValue(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = { 0, -1 };
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }
  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}



void setup() {  
  Serial.begin(9600);
}

void loop() {  
  String myString;
  
  // Read data from serial port
  while (Serial.available()) {
    delay(3);  //delay to allow buffer to fill 
    if (Serial.available() > 0) {
      char c = Serial.read();  //gets one byte from serial buffer
      myString += c; //fills myString with the read value
    } 
  }

  // Interpret data
  if (myString.length() >0) {
    myString.remove(0,1);
    //Serial.println(myString); //see what was received
    String firstValue = getValue(myString, '-', 0);
    String secondValue = getValue(myString, '-', 1);
    String thirdValue = getValue(myString, '-', 2);
    int secondValueInt = secondValue.toInt();

    // Read from pin
    if (thirdValue.equals("")){
      pinMode(secondValueInt, INPUT);
      return;
    }

    // Write to a pin
    int thirdValueInt = thirdValue.toInt();
    pinMode(secondValueInt, OUTPUT);

    if (firstValue.equals("D")){
      digitalWrite(secondValueInt, thirdValueInt);
      //Serial.println("digitalWrite(" + String(secondValueInt) + ", " + String(thirdValueInt) + ")");
    } else if (firstValue.equals("A")){
      analogWrite(secondValueInt, thirdValueInt);
      //Serial.println("analogWrite(" + String(secondValueInt) + ", " + String(thirdValueInt) + ")");
    }

    Serial.println("*" + firstValue + "-" + secondValue + "-" + thirdValue);
    //Serial.println(firstValue + " " + secondValueInt + " " + thirdValueInt);
    delay(10);
  }
} 