/**
 * Author : Jose Luis Álvarez Quiroga
 *
 * This sketch allows Arduino to receive data via serial port
 * and act accordingly
 *
 * Example command
 * *D-W-008-0001
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
		// Remove fist char (*)
		myString.remove(0,1);

		// Get values from the String
		String firstValueString = getValue(myString, '-', 0);
		char commandType = firstValueString.charAt(0);
		String secondValueString = getValue(myString, '-', 1);
		char instruction = secondValueString.charAt(0);
		String thirdValue = getValue(myString, '-', 2);
		String fourthValue = getValue(myString, '-', 3);
		
		// TODO not possible to use Analog pins yet
		int pinNumber = thirdValue.toInt();
		int value = fourthValue.toInt();

		// Switch based on instruction (Read or write)
		switch (instruction){
			case 'R':
				// Change pin mode
				pinMode(pinNumber, INPUT);

				// Act according to command type
				if (commandType =='D'){
					//digitalRead(thirdValueInt);
					//Serial.println("digitalRead(" + String(thirdValueInt) + ")");
					//Serial.println(digitalRead(thirdValueInt));
					// TODO return value with the same format
					Serial.print("*" + myString);
				} else if (commandType == 'A'){
					//analogRead(thirdValueInt);
					//Serial.println("analogRead(" + String(thirdValueInt) + ")");
					//Serial.println(analogRead(thirdValueInt));
					// TODO return value with the same format
					Serial.print("*" + myString);
				}
				break;

			case 'W':
				// Change pin mode
				pinMode(pinNumber, OUTPUT);

				// Act according to command type
				if (commandType == 'D'){
					digitalWrite(pinNumber, value);
					// Serial.print("*" + myString);
				} else if (commandType == 'A'){
					analogWrite(pinNumber, value);
					// Serial.print("*" + myString);
				}
				break;
		}
		delay(10);
	}
} 