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
		String firstValueString = getValue(myString, '-', 0);
		char firstValue = firstValueString.charAt(0);
		String secondValueString = getValue(myString, '-', 1);
		char secondValue = secondValueString.charAt(0);
		String thirdValue = getValue(myString, '-', 2);
		String fourthValue = getValue(myString, '-', 3);
		
		// TODO not possible to use Analog pins yet
		int thirdValueInt = thirdValue.toInt();
		int fourthValueInt = fourthValue.toInt();


		switch (secondValue){
			case 'R':
				pinMode(thirdValueInt, INPUT);
				if (firstValue =='D'){
					//digitalRead(thirdValueInt);
					//Serial.println("digitalRead(" + String(thirdValueInt) + ")");
					//Serial.println(digitalRead(thirdValueInt));
					// TODO modify this print
					Serial.print("*" + myString);
				} else if (firstValue == 'A'){
					//analogRead(thirdValueInt);
					//Serial.println("analogRead(" + String(thirdValueInt) + ")");
					//Serial.println(analogRead(thirdValueInt));
					// TODO modify this print
					Serial.print("*" + myString);
				}
				break;

			case 'W':
				pinMode(thirdValueInt, OUTPUT);
				if (firstValue == 'D'){
					digitalWrite(thirdValueInt, fourthValueInt);
					// Serial.println("digitalWrite(" + String(thirdValueInt) + ", " + String(fourthValueInt) + ")");
					Serial.print("*" + myString);
				} else if (firstValue == 'A'){
					analogWrite(thirdValueInt, fourthValueInt);
					// Serial.println("analogWrite(" + String(thirdValueInt) + ", " + String(fourthValueInt) + ")");
					Serial.print("*" + myString);
				}
				break;
		}

		//Serial.println("*" + String(firstValue) + "-" + secondValue + "-" + thirdValue);
		//Serial.println(firstValue + " " + secondValueInt + " " + thirdValueInt);
		delay(10);
	}
} 