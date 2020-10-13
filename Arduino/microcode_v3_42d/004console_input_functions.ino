/*
   check console return code and display arror if any
   argument(s): return code
   return: nothing
*/

void consoleErrorMessage(uint8_t error) {
  switch (error) {
    case 0: // nothing available on console, no message
      break;
    case 1:
      Serial.print("Error code ");
      Serial.print(error);
      Serial.println(": bad first field");
      break;
    case 2:
      Serial.print("Error code ");
      Serial.print(error);
      Serial.println(": bad 2nd field");
      break;
    case 3:
      Serial.print("Error code ");
      Serial.print(error);
      Serial.println(": bad 3rd field");
      break;
    case 4:
      Serial.print("Error code ");
      Serial.print(error);
      Serial.println(": bad 4th field");
      break;
    case 5:
      Serial.print("Error code ");
      Serial.print(error);
      Serial.println(": bad 5th field");
      break;
    case 6:
      Serial.print("Error code ");
      Serial.print(error);
      Serial.println(": bad 6th field");
      break;
    case 90:
      Serial.print("Error code ");
      Serial.print(error);
      Serial.println(": command not yet implemented");
      break;
    case 99: // command OK
      Serial.println("OK");
      break;
    default:
      Serial.print("Unknown error code ");
      break;
  }

}// end consoleErrorMessage()


/*
   converts hex string to uint16_t
   arguments(s): hexa string limited to 4 hex characters (size not tested)
   returns: integer value
*/
uint16_t hexToDec(String hexString) {

  uint16_t decValue = 0;
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
} // end hexToDec()


/*
   calibrates in voltage source mode
   arguments(s): channel (0..7)
   returns: nothing
*/
uint8_t voltageCalibration(uint8_t channel) {
  String phrase;

  float fineOffsetStep = LSB_VALUE / 8;
  float fineGainStep = (DEF_ABSOLUTE_MAX_VALUE - DEF_ABSOLUTE_MIN_VALUE) / pow(2, 17);

  float zeroVolt, minVolt, maxVolt, maxGainError, minGainError;
  int fineOffset, fineGain, coarseGain;

  Serial.println("Voltage mode calibration, condition: no load . Continue [y] or skip [any key]?");

  while (!Serial.available()) {} // wait for string in buffer
  phrase = Serial.readStringUntil('\n'); // get incomming string
  phrase.toUpperCase(); // all uppercase

  if (phrase.substring(0, 1).compareTo("Y") != 0) {
    Serial.println("Voltage mode calibration skipped");
    return 1; // skip step & exit
  }

  // proceed with calibration, reset cal values in FRAM
  fram.writeInt(0x5000 + VOLTAGE_MODE_COARSE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
  fram.writeInt(0x5000 + VOLTAGE_MODE_FINE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
  fram.writeInt(0x5000 + VOLTAGE_MODE_COARSE_GAIN_BASE_ADDRESS + 2 * channel, 0);
  fram.writeInt(0x5000 + VOLTAGE_MODE_FINE_GAIN_BASE_ADDRESS + 2 * channel, 0);

  setChannelType(channel, 0); // offset calibration
  setChannelValue(channel, 0);
  switchChannelOn(channel);

  Serial.println("Enter measured value in mV");
  while (!Serial.available()) {} // wait for string in buffer
  phrase = Serial.readStringUntil('\n'); // get incomming string
  phrase.toUpperCase(); // all uppercase
  zeroVolt = phrase.toFloat();

  fineOffset = (int)(-zeroVolt / fineOffsetStep);
  fram.writeInt(0x5000 + VOLTAGE_MODE_FINE_OFFSET_BASE_ADDRESS + 2 * channel, formatFineOffset(fineOffset));
  setChannelType(channel, 0); // reload new fine offset


  setChannelValue(channel, DEF_ABSOLUTE_MAX_VALUE); // gain calibration
  Serial.println("Enter measured value in mV");
  while (!Serial.available()) {} // wait for string in buffer
  phrase = Serial.readStringUntil('\n'); // get incomming string
  phrase.toUpperCase(); // all uppercase
  maxVolt = phrase.toFloat();
  /*
    maxGainError = DEF_ABSOLUTE_MAX_VALUE - maxVolt;

    fineGain = (int)round(maxGainError / fineGainStep);

    Serial.print("Fine Gain =");
    Serial.println(fineGain);

    if ((fineGain > 31) || (fineGain < -32)) Serial.println("Gain adjustment is not optimal");

    fram.writeInt(0x5000 + VOLTAGE_MODE_FINE_GAIN_BASE_ADDRESS + 2 * channel, formatFineGain(fineGain));
  */
  maxGainError = DEF_ABSOLUTE_MAX_VALUE / maxVolt;
  Serial.print("Gain =");
  Serial.println(maxGainError, 6);

  coarseGain = (int)((maxGainError - 1) * 1e6);
  fineGain = 0; // fine gain no more used for voltage mode


  fram.writeInt(0x5000 + VOLTAGE_MODE_COARSE_GAIN_BASE_ADDRESS  + 2 * channel, coarseGain);
  fram.writeInt(0x5000 + VOLTAGE_MODE_FINE_GAIN_BASE_ADDRESS + 2 * channel, formatFineGain(fineGain));

  setChannelType(channel, 0); // reload new fine gain
  setChannelValue(channel, 0);
  switchChannelOff(channel);
  Serial.println("Step1: voltage mode calibration completed");
  return 0;
} // end voltageCalibration()

/*
   calibrates in current source mode
   arguments(s): channel (0..7), range (0..3)
   returns: nothing
*/
uint8_t currentCalibration(uint8_t channel, uint8_t range) {
  String phrase;

  float fineOffsetStep = LSB_VALUE / 8;
  float fineGainStep = (HIGH_COMPLIANCE_MAX_CURRENT - HIGH_COMPLIANCE_MIN_CURRENT) / pow(2, 17);

  float zeroVolt, minVolt, maxVolt, maxGainError, minGainError;
  int fineOffset, fineGain, coarseOffset, coarseGain;

  Serial.print("Current mode calibration, range ");
  Serial.print(range);
  Serial.println(" , condition:  zero Ohm load . Continue [y] or skip [any key]?");

  while (!Serial.available()) {} // wait for string in buffer
  phrase = Serial.readStringUntil('\n'); // get incomming string
  phrase.toUpperCase(); // all uppercase

  if (phrase.substring(0, 1).compareTo("Y") != 0) {
    Serial.print("Current mode calibration, range ");
    Serial.print(range);
    Serial.println(" skipped");
    return 1; // skip step & exit
  }

  //proceed with calibration, reset cal values in FRAM
  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_COARSE_OFFSET_BASE_ADDRESS + 0x0040 * range + 2 * channel, 0);
  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_FINE_OFFSET_BASE_ADDRESS + 0x0040 * range + 2 * channel, 0);
  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_COARSE_GAIN_BASE_ADDRESS + 0x0040 * range + 2 * channel, 0);
  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_FINE_GAIN_BASE_ADDRESS + 0x0040 * range + 2 * channel, 0);

  setChannelType(channel, 1); // offset calibration
  setChannelValue(channel, 0);
  setChannelRange(channel, range);
  switchChannelOn(channel);

  Serial.println("Enter measured value in uA");
  while (!Serial.available()) {} // wait for string in buffer
  phrase = Serial.readStringUntil('\n'); // get incomming string
  phrase.toUpperCase(); // all uppercase
  zeroVolt = pow(10, 3 - range) * phrase.toFloat();// convert uA int mV

  coarseOffset = 0;
  if (abs(zeroVolt) > 4.0) {
    if (zeroVolt > 0) { // calculates coarse offset so that |fineOffset| is less than 4mV
      do {
        coarseOffset --;
      } while ((zeroVolt - coarseOffset * LSB_VALUE) > 4.0);
    } else { // calculates coarse offset
      do {
        coarseOffset ++;
      } while ((zeroVolt - coarseOffset * LSB_VALUE) < -4.0);
    }
  }
  zeroVolt += coarseOffset * LSB_VALUE; // apply and stores coarse correction
  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_COARSE_OFFSET_BASE_ADDRESS + 0x0040 * range + 2 * channel, coarseOffset);

  fineOffset = (int)(-zeroVolt / fineOffsetStep); // calculates and stores coarse correction
  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_FINE_OFFSET_BASE_ADDRESS + 0x0040 * range  + 2 * channel, formatFineOffset(fineOffset));
  setChannelType(channel, 1); // reload new fine offset
  setChannelRange(channel, range);// reload new coarse offset


  setChannelValue(channel, HIGH_COMPLIANCE_MAX_CURRENT); // gain calibration
  Serial.println("Enter measured value in uA");
  while (!Serial.available()) {} // wait for string in buffer
  phrase = Serial.readStringUntil('\n'); // get incomming string
  phrase.toUpperCase(); // all uppercase
  maxVolt = pow(10, 3 - range) * phrase.toFloat();// convert uA int mV


  maxGainError = HIGH_COMPLIANCE_MAX_CURRENT / maxVolt;
  Serial.print("Gain =");
  Serial.println(maxGainError, 6);

  coarseGain = (int)((maxGainError - 1) * 1e6);
  fineGain = 0; // fine gain no more used for current mode


  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_COARSE_GAIN_BASE_ADDRESS + 0x0040 * range  + 2 * channel, coarseGain);
  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_FINE_GAIN_BASE_ADDRESS + 0x0040 * range  + 2 * channel, formatFineGain(fineGain));

  setChannelType(channel, 1); // reload new fine gain
  setChannelValue(channel, 0);
  switchChannelOff(channel);
  Serial.print("Current mode calibration, range ");
  Serial.print(range);
  Serial.println(" completed");
  return 0;
} // end currentCalibration()

/*
   format value to fine offset in order to avoid problems when the positive value is too high
   argument: raw fine offset
   returns: formatted fine offset
*/
int formatFineOffset( int value) {

  if (value > 127) return 127;
  else if (value < -128) return -128;
  else return value;

} // end formatFineOffset()

/*
   format value to fine gain in order to avoid problems when the positive value is too high
   argument: raw fine gain
   returns: formatted fine gain
*/
int formatFineGain( int value) {

  if (value > 31) return 31;
  else if (value < -32) return -32;
  else return value;

} // end formatFineOffset()

/*
   Interprets  subPhrase[index] with a value potentially containing m or u
   argument: subPhrase index, type (0= voltage, 1 = Current), converted value value in mV or uA depending on the type
   returns: -1: ok >=0: index of field in error
*/

int consoleGetValueWithMultiplier(uint8_t index, uint8_t type, float *returnedValue) {
  int stringLen;
  float multiplier;
  String lastChar;

  stringLen = subPhrase[index].length();
  lastChar = subPhrase[index].substring(stringLen - 1);
  //  Serial.print("lastchar=");
  //  Serial.println(lastChar);
  switch (type) {
    case 0: // voltage
      if (lastChar.compareTo("M") == 0) multiplier = 1;
      else  if (lastChar.compareTo("U") == 0) multiplier = 0.001;
      else  if (lastChar.compareTo("0") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("1") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("2") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("3") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("4") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("5") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("6") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("7") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("8") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("9") == 0) multiplier = 1000;
      else return index++; // bad field
      break;
    case 1: // current
      if (lastChar.compareTo("M") == 0) multiplier = 1000;
      else  if (lastChar.compareTo("U") == 0) multiplier = 1;
      else  if (lastChar.compareTo("0") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("1") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("2") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("3") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("4") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("5") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("6") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("7") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("8") == 0) multiplier = 1e6;
      else  if (lastChar.compareTo("9") == 0) multiplier = 1e6;
      else return index++; // bad field
      break;
  }

  *returnedValue = (subPhrase[index].substring(0, stringLen).toFloat() * multiplier);

  return -1;

}// end consoleGetValueWithMultiplier(uint8_t index, uint8_t type)


void sendChannelStatus(uint8_t channel) {

  float val;

  Serial.print("CH,");
  Serial.print(channel);
  Serial.print(",");
  if (boardOnSlotStatus & 0x01 << channel) {

    if (channelActiveStatus & 0x01 << channel) Serial.print("ON,");
    else Serial.print("OFF,");

    if (channelModeStatus & 0x01 << channel) Serial.print("I,");
    else Serial.print("V,");

    if (channelModeStatus & 0x01 << channel) {
      switch (channelCurrentRange[channel]) {
        case 0:
          val = (float)channelValue[channel] / 1000;
          Serial.print(val, 3);
          break;

        case 1:
          val = (float)channelValue[channel] / 100;
          Serial.print(val, 2);
          break;

        case 2:
          val = (float)channelValue[channel] / 10;
          Serial.print(val, 1);
          break;

        case 3:
          Serial.print(channelValue[channel], DEC);
          break;

      }
    } else {
      Serial.print(channelValue[channel], DEC);
    }

    Serial.print(",");

    Serial.print("UVL,");
    Serial.print(channelMaxVoltage[channel]);
    Serial.print(",");

    Serial.print("LVL,");
    Serial.print(channelMinVoltage[channel]);
    Serial.print(",");

    Serial.print("UIL,");
    Serial.print(channelMaxCurrent[channel]);
    Serial.print(",");

    Serial.print("LIL,");
    Serial.println(channelMinCurrent[channel]);

  } else Serial.println("Empty_Slot");
 


}// end sendChannelStatus()
