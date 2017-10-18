
void setup() {  
    Serial.begin(9600);  
}  
 
void loop() {  
    int sb;
    String myString;   
    
    while (Serial.available()) {
      delay(3);  //delay to allow buffer to fill 
      if (Serial.available() > 0) {
        char c = Serial.read();  //gets one byte from serial buffer
        myString += c; //makes the string readString
      } 
    }

    if (myString.length() > 0) {
      Serial.println(myString); //see what was received
    }

    
} 