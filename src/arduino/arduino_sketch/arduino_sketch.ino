/**
 * Author : Jose Luis Álvarez Quiroga
 *
 * This sketch allows Arduino to receive data via serial port
 * and act accordingly
 *
 * Example command
 * Turn ON LED at pin 9
 * 			*1-2-D-W-009-0001
 * Read analogic input at pin 3
 * 			*1-0-A-R-003-0000
 */

#define CONTROLLER_GENERIC 0
#define CONTROLLER_LED_ANALOG 1
#define CONTROLLER_LED_DIGITAL 2
#define CONTROLLER_SERVO 3
#define CONTROLLER_TEMP_SENSOR 4
#define CONTROLLER_HUMIDITY_SENSOR 5


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

String convertToString(int data, int numberOfChars){
	String command;
	int dataLength = String(data).length();
	int dataZeroes = numberOfChars - dataLength;
	if (dataZeroes <= 0) dataZeroes = 0;
	while (dataZeroes > 0) {
		command += "0";
		dataZeroes -= 1;
	}
	command+=String(data);
	return command;
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
		String controllerIdString = getValue(myString, '-', 0);
		String controllerTypeString = getValue(myString, '-', 1);
		String pinTypeString = getValue(myString, '-', 2);
		String commandTypeString = getValue(myString, '-', 3);
		String pinNumberString = getValue(myString, '-', 4);
		String dataString = getValue(myString, '-', 5);

		// Convert to char
		char commandTypeChar = commandTypeString.charAt(0);
		char pinTypeChar = pinTypeString.charAt(0);

		// Convert to int the variables
		int controllerId = controllerIdString.toInt();
		int controllerType = controllerTypeString.toInt();
		int pinNumber = pinNumberString.toInt();
		int data = dataString.toInt();

		// Serial.println("Controller ID   : " + String(controllerId));
		// Serial.println("Controller type : " + String(controllerType));
		// Serial.println("Pin type        : " + String(pinTypeString));
		// Serial.println("Command type    : " + String(commandTypeString));
		// Serial.println("Pin number      : " + String(pinNumber));
		// Serial.println("Data            : " + String(data));


		// Act based on controller type
		switch(controllerType){
			case CONTROLLER_GENERIC:
				switch (commandTypeChar){
					case 'R':
						pinMode(pinNumber, INPUT);
						if (pinTypeChar == 'D'){
							Serial.println(
								"*"
								+ convertToString(controllerId, 2)
								+ "-" + convertToString(digitalRead(pinNumber), 4)
								+ "-xx");
						} else if (pinTypeChar == 'A'){
							Serial.println(
								"*" + 
								convertToString(controllerId, 2) + 
								+ "-" + convertToString(analogRead(pinNumber), 4)
								+ "-xx");
						}
						break;
					case 'W':
						pinMode(pinNumber, OUTPUT);
						// Act according to command type
						if (pinTypeChar == 'D'){
							digitalWrite(pinNumber, data);
						} else if (pinTypeChar == 'A'){
							analogWrite(pinNumber, data);
						}
						break;
					default:
						break;
				}
				break;
			case CONTROLLER_LED_ANALOG:
				// Change pin mode
				pinMode(pinNumber, OUTPUT);
				// Serial.println("analogWrite(" + String(pinNumber) + ", " + String(data) + ")");
				analogWrite(pinNumber, data);
				break;
			case CONTROLLER_LED_DIGITAL:
				// Change pin mode
				pinMode(pinNumber, OUTPUT);
				// Serial.println("digitalWrite(" + String(pinNumber) + ", " + String(data) + ")");
				digitalWrite(pinNumber, data);
				if (data==1){
					Serial.println("*"+convertToString(controllerId,2)+"- ON -xx");	
				} else {
					Serial.println("*"+convertToString(controllerId,2)+"- OFF-xx");
				}
				
				break;
			case CONTROLLER_SERVO:
				break;
			case CONTROLLER_TEMP_SENSOR:
				break;
			case CONTROLLER_HUMIDITY_SENSOR:
				Serial.println("*"+convertToString(controllerId,2)+"-TEST-xx");
				break;
			default:
				break;
		}
		delay(10);
	}
} 





		// String firstValueString = getValue(myString, '-', 0);
		// char commandType = firstValueString.charAt(0);

		// String secondValueString = getValue(myString, '-', 1);
		// char instruction = secondValueString.charAt(0);
		// String thirdValue = getValue(myString, '-', 2);
		// String fourthValue = getValue(myString, '-', 3);
		
		// // TODO not possible to use Analog pins yet
		// int pinNumber = thirdValue.toInt();
		// int value = fourthValue.toInt();

		// // Switch based on instruction (Read or write)
		// switch (instruction){
		// 	case 'R':
		// 		// Change pin mode
		// 		pinMode(pinNumber, INPUT);

		// 		// Act according to command type
		// 		if (commandType =='D'){
		// 			//digitalRead(thirdValueInt);
		// 			//Serial.println("digitalRead(" + String(thirdValueInt) + ")");
		// 			//Serial.println(digitalRead(thirdValueInt));
		// 			// TODO return value with the same format
		// 			Serial.print("*" + myString);
		// 		} else if (commandType == 'A'){
		// 			//analogRead(thirdValueInt);
		// 			//Serial.println("analogRead(" + String(thirdValueInt) + ")");
		// 			//Serial.println(analogRead(thirdValueInt));
		// 			// TODO return value with the same format
		// 			Serial.print("*" + myString);
		// 		}
		// 		break;

		// 	case 'W':
		// 		// Change pin mode
		// 		pinMode(pinNumber, OUTPUT);

		// 		// Act according to command type
		// 		if (commandType == 'D'){
		// 			digitalWrite(pinNumber, value);
		// 			// Serial.print("*" + myString);
		// 		} else if (commandType == 'A'){
		// 			analogWrite(pinNumber, value);
		// 			// Serial.print("*" + myString);
		// 		}
		// 		break;
		// }
		// delay(10);