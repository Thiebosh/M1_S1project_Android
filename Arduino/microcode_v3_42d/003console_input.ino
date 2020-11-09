
/*
   interprets inputs from console
   arguments: none
   returns: nothing
*/
uint8_t parseInputFromConsole(void) {
  Serial.println("exec fct parseInputFromConsole in 003"); // changement : ligne inexistante
  String phrase, temp, temp2;
  int semicolonPos, colonPos[15];
  uint8_t fieldType;

  uint8_t channel;
  int value;
  uint8_t range, type, framByteData;
  uint16_t framAddress;
  int framIntData;
  int dummy;
  float floatValue;

  if (Serial.available()) { // look for string in buffer


    phrase = Serial.readStringUntil('\n'); // get incomming string
    phrase.toUpperCase(); // all uppercase
    semicolonPos = phrase.indexOf(';'); // look for comment
    if (semicolonPos > 0) phrase = phrase.substring(0, semicolonPos); // removes any comment
    if (semicolonPos == 0)return 0;  // exit if string only contains ';'

    // look for colons, colonPos[i] different from -1 are valid positions
    uint8_t i = 0;
    uint8_t debut = 0;
    do {
      colonPos[i] = phrase.indexOf(':', debut);
      debut = colonPos[i] + 1;
      i++;
    } while (colonPos[i - 1] != -1);
    // split phrase into subPhrase
    i = 0;
    debut = 0;
    while (colonPos[i] != -1) {
      subPhrase[i] = phrase.substring(debut, colonPos[i]);
      debut = colonPos[i] + 1;
      i++;
    }
    subPhrase[i] = phrase.substring(debut);

    // check first subPhrase, determines type of first field
    if (subPhrase[0].substring(0, 2).compareTo("CH") == 0) fieldType = 0;
    else if (subPhrase[0].substring(0, 2).compareTo("SY") == 0) fieldType = 1;
    else if (subPhrase[0].substring(0, 2).compareTo("TE") == 0) fieldType = 2;
    else if (subPhrase[0].substring(0, 2).compareTo("CO") == 0) fieldType = 3;
    else return 1; // bad first field, exit

    // process first field

    /* ************* CH ***************/
    switch (fieldType) { // 1st switch
      case 0: // command CH
        // process second field
        channel = (uint8_t)subPhrase[1].toInt(); // get channel number
        if (channel < 0 || channel > 7) return 2; // bad second field, exit

        // test third field
        if (subPhrase[2].substring(0, 1).compareTo("V") == 0) fieldType = 0;
        else if (subPhrase[2].substring(0, 1).compareTo("I") == 0) fieldType = 1;
        else if (subPhrase[2].substring(0, 2).compareTo("ON") == 0) fieldType = 2;
        else if (subPhrase[2].substring(0, 3).compareTo("OFF") == 0) fieldType = 3;
        else if (subPhrase[2].substring(0, 3).compareTo("UVL") == 0) fieldType = 4;
        else if (subPhrase[2].substring(0, 3).compareTo("LVL") == 0) fieldType = 5;
        else if (subPhrase[2].substring(0, 3).compareTo("UIL") == 0) fieldType = 6;
        else if (subPhrase[2].substring(0, 3).compareTo("LIL") == 0) fieldType = 7;
        else if (subPhrase[2].substring(0, 4).compareTo("DEFL") == 0) fieldType = 8;
        else if (subPhrase[2].substring(0, 1).compareTo("?") == 0) fieldType = 9;
        else return 3; // bad 3rd field, exit

        // process fourth field
        switch (fieldType) {  // 2nd switch

          case 0:
            dummy = consoleGetValueWithMultiplier(3, 0, &floatValue);
            if (dummy == -1) { // OK
              if (channelModeStatus & 0x01 << channel) {
                switchChannelOff(channel);
                channelActiveStatus &= ~(0x01 << channel);
                setChannelType(channel, 0); // change current to voltage
                channelModeStatus &= ~(0x01 << channel);
                isChannelLabelChanged |= 0x01 << channel;
              }
              if ((int16_t) floatValue > channelMaxVoltage[channel]) floatValue = (float)channelMaxVoltage[channel];
              if ((int16_t) floatValue < channelMinVoltage[channel]) floatValue = (float)channelMinVoltage[channel];
              setChannelValue(channel, (int16_t) floatValue);
              extractDisplayValueFromDACValue(channel);
              isChannelValueChanged |= 0x01 << channel; // because unit changes
            } else return dummy;
            break;

          case 1:
            dummy = consoleGetValueWithMultiplier(3, 1, &floatValue);
            if (dummy == -1) { // OK
              if (!(channelModeStatus & 0x01 << channel)) {
                switchChannelOff(channel);
                channelActiveStatus &= ~(0x01 << channel);
                setChannelType(channel, 1); // change voltage to current
                channelModeStatus |= (0x01 << channel);
                isChannelLabelChanged |= 0x01 << channel;
              }
              keyboardUnit = 0 ; //uA

              keyboardValue = floatValue;
              //              Serial.print("keyboardValue= ");
              //              Serial.println(keyboardValue, 3);
              extractFromKeyboardValue(1, channel); // yields calculatedDacValue and calculatedRange
              setChannelValue(channel, calculatedDacValue);
              setChannelRange(channel, calculatedRange);
              //              Serial.print("calculatedDacValue= ");
              //              Serial.println(calculatedDacValue);
              //              Serial.print("calculatedRange= ");
              //              Serial.println(calculatedRange);
              extractDisplayValueFromDACValue(channel);
              isChannelValueChanged |= 0x01 << channel; // because unit changes
            } else return dummy;

            break;

          case 2:
            switchChannelOn(channel);
            channelActiveStatus |= 0x01 << channel;
            isChannelLabelChanged |= 0x01 << channel;
            break;

          case 3:
            switchChannelOff(channel);
            channelActiveStatus &= ~(0x01 << channel);
            isChannelLabelChanged |= 0x01 << channel;
            break;

          case 4:
            dummy = consoleGetValueWithMultiplier(3, 0, &floatValue);
            if (dummy == -1) {
              channelMaxVoltage[channel] = (int) floatValue;
              if (channelMaxVoltage[channel] > DEF_ABSOLUTE_MAX_VALUE ) channelMaxVoltage[channel] = DEF_ABSOLUTE_MAX_VALUE;
              if (channelMaxVoltage[channel] < DEF_ABSOLUTE_MIN_VALUE ) channelMaxVoltage[channel] = DEF_ABSOLUTE_MIN_VALUE;
              currentContext = 22000;
              fullLimitsMenuDisplay(0);
            } else return dummy;
            break;

          case 5:
            dummy = consoleGetValueWithMultiplier(3, 0, &floatValue);
            if (dummy == -1) {
              channelMinVoltage[channel] = (int) floatValue;
              if (channelMinVoltage[channel] > DEF_ABSOLUTE_MAX_VALUE ) channelMinVoltage[channel] = DEF_ABSOLUTE_MAX_VALUE;
              if (channelMinVoltage[channel] < DEF_ABSOLUTE_MIN_VALUE ) channelMinVoltage[channel] = DEF_ABSOLUTE_MIN_VALUE;
              currentContext = 22000;
              fullLimitsMenuDisplay(0);
            } else return dummy;
            break;

          case 6:
            dummy = consoleGetValueWithMultiplier(3, 1, &floatValue);
            Serial.println(floatValue, 3);
            if (dummy == -1) {
              channelMaxCurrent[channel] = (int) floatValue;
              Serial.println(channelMaxCurrent[channel]);
              if (channelMaxCurrent[channel] > HIGH_COMPLIANCE_MAX_CURRENT ) channelMaxCurrent[channel] = HIGH_COMPLIANCE_MAX_CURRENT;
              if (channelMaxCurrent[channel] < HIGH_COMPLIANCE_MIN_CURRENT ) channelMaxCurrent[channel] = HIGH_COMPLIANCE_MIN_CURRENT;
              Serial.println(channelMaxCurrent[channel]);
              currentContext = 22000;
              fullLimitsMenuDisplay(0);
            } else return dummy;
            break;

          case 7:
            dummy = consoleGetValueWithMultiplier(3, 1, &floatValue);
            if (dummy == -1) {
              channelMinCurrent[channel] = (int) floatValue;
              if (channelMinCurrent[channel] > HIGH_COMPLIANCE_MAX_CURRENT ) channelMinCurrent[channel] = HIGH_COMPLIANCE_MAX_CURRENT;
              if (channelMinCurrent[channel] < HIGH_COMPLIANCE_MIN_CURRENT ) channelMinCurrent[channel] = HIGH_COMPLIANCE_MIN_CURRENT;
              currentContext = 22000;
              fullLimitsMenuDisplay(0);
            } else return dummy;
            break;

          case 8:
            channelMaxVoltage[channel] = DEF_MAX_VOLTAGE;
            channelMinVoltage[channel] = DEF_MIN_VOLTAGE;
            channelMaxCurrent[channel] = HIGH_COMPLIANCE_MAX_CURRENT;
            channelMinCurrent[channel] = HIGH_COMPLIANCE_MIN_CURRENT;
            break;

          case 9:
            channel = (uint8_t)subPhrase[1].toInt(); // get channel number
            if (channel < 0 || channel > 7) return 2; // bad second field, exit
            sendChannelStatus(channel);
            break;
        }

        break;

      /* ************* SY ***************/
      case 1: // command SY
        // test 2nd field
        if (subPhrase[1].substring(0, 3).compareTo("STO") == 0) fieldType = 0;
        else if (subPhrase[1].substring(0, 3).compareTo("REC") == 0) fieldType = 1;
        else if (subPhrase[1].substring(0, 3).compareTo("CAL") == 0) fieldType = 2;
        else if (subPhrase[1].substring(0, 2).compareTo("TS") == 0) fieldType = 3;
        else if (subPhrase[1].substring(0, 4).compareTo("VER?") == 0) fieldType = 4;
        else return 2; // bad 2nd field, exit

        // process 2nd field
        switch (fieldType) {  // 1st switch
          case 0:
            channel = (uint8_t)subPhrase[2].toInt(); // get channel number
            if (channel < 0 || channel > 7) return 3; // bad 3rd field, exit
            else {
              //              Serial.print("Store config ");
              //              Serial.println(channel, DEC);
              storeUserCongig(channel);
            }
            break;

          case 1:
            channel = (uint8_t)subPhrase[2].toInt(); // get channel number
            if (channel < 0 || channel > 7) return 3; // bad 3rd field, exit
            else {
              //              Serial.print("Recall config ");
              //              Serial.println(channel, DEC);
              recallUserCongig(channel);
            }
            break;

          case 2:
            if (subPhrase[2].substring(0, 4).compareTo("LOCK") == 0) {
              calibrationLocked = 1;
              Serial.println("SYS->CAL commands are locked");
            }
            else if (subPhrase[2].substring(0, 4).compareTo("UNLO") == 0) {
              calibrationLocked = 0;
              Serial.println("SYS->CAL commands are unlocked");
            }
            else if (!calibrationLocked) {

              channel = (uint8_t)subPhrase[2].toInt(); // get channel number
              if (channel < 0 || channel > 7) return 3; // bad 3rd field, exit
              Serial.print("Calibrating channel ");
              Serial.print(channel, DEC);
              Serial.println( ":");

              voltageCalibration(channel);
              currentCalibration(channel, 0);
              currentCalibration(channel, 1);
              currentCalibration(channel, 2);
              currentCalibration(channel, 3);

            } else if (calibrationLocked) Serial.println("ERROR: CAL must be unlocked first");
            else return 3; // bad 3rd field, exit

            break;

          case 3:
            if (subPhrase[2].substring(0, 4).compareTo("LOCK") == 0) {
              touchscreenLocked = 1;
              Serial.println("Touchscreen is locked");
            }
            else if (subPhrase[2].substring(0, 4).compareTo("UNLO") == 0) {
              touchscreenLocked = 0;
              Serial.println("Touchscreen is unlocked");
            }
            break;

          case 4:
            Serial.print("Firmware V");
            Serial.print (code_version);
            Serial.print(" - ");
            Serial.println(version_date);
            break;
        }
        break;

      /* ************* TE ***************/
      case 2: // command TE

        // process second field
        channel = (uint8_t)subPhrase[1].toInt(); // get channel number
        if (channel < 0 || channel > 7) return 2; // bad second field, exit

        // test third field
        if (subPhrase[2].substring(0, 3).compareTo("VAL") == 0) fieldType = 0;
        else if (subPhrase[2].substring(0, 3).compareTo("RAN") == 0) fieldType = 1;
        else if (subPhrase[2].substring(0, 2).compareTo("ON") == 0) fieldType = 2;
        else if (subPhrase[2].substring(0, 3).compareTo("OFF") == 0) fieldType = 3;
        else if (subPhrase[2].substring(0, 3).compareTo("TYP") == 0) fieldType = 4;
        else if (subPhrase[2].substring(0, 3).compareTo("FRA") == 0) fieldType = 5;
        else return 3; // bad 3rd field, exit

        // process third field
        switch (fieldType) {  // 2nd switch

          case 0:
            value = subPhrase[3].toInt(); // get value
            if (value < -8000 || value > 8000) return 4; // bad 4th field, exit
            setChannelValue(channel, value);  // set value
            Serial.print("Channel ");
            Serial.print(channel, DEC);
            Serial.print(" value is set to ");
            Serial.print(value, DEC);
            Serial.println(" mV");
            extractDisplayValueFromDACValue(channel);
            isChannelValueChanged |= 0x01 << channel; // because displayed value or unit change
            break;

          case 1:
            range = subPhrase[3].toInt(); // get range
            if (range < 0 || range > 3) return 4; // bad 4th field, exit
            setChannelRange(channel, range);  // set range
            Serial.print("Channel ");
            Serial.print(channel, DEC);
            Serial.print(" range is set to ");
            Serial.println(range, DEC);
            extractDisplayValueFromDACValue(channel);
            isChannelValueChanged |= 0x01 << channel; // because displayed value or unit may change
            break;

          case 2:
            switchChannelOn(channel);
            Serial.print("Channel ");
            Serial.print(channel, DEC);
            Serial.println(" is ON");
            channelActiveStatus |= 0x01 << channel;
            isChannelLabelChanged |= 0x01 << channel;
            break;

          case 3:
            switchChannelOff(channel);
            Serial.print("Channel ");
            Serial.print(channel, DEC);
            Serial.println(" is OFF");
            channelActiveStatus &= ~(0x01 << channel);
            isChannelLabelChanged |= 0x01 << channel;
            break;

          case 4:
            if (subPhrase[3].compareTo("V") == 0) {
              setChannelType(channel, 0);
              Serial.print("Channel ");
              Serial.print(channel, DEC);
              Serial.println(" set to voltage source ");
              channelModeStatus &= ~(0x01 << channel);
              isChannelLabelChanged |= 0x01 << channel;
              isChannelValueChanged |= 0x01 << channel; // because unit changes
            }
            else if (subPhrase[3].compareTo("I") == 0) {
              setChannelType(channel, 1);
              Serial.print("Channel ");
              Serial.print(channel, DEC);
              Serial.println(" set to current source ");
              channelModeStatus |= (0x01 << channel);
              isChannelLabelChanged |= 0x01 << channel;
              isChannelValueChanged |= 0x01 << channel; // because unit changes
            }
            break;

          case 5:
            // test 4th field
            if (subPhrase[3].substring(0, 3).compareTo("WRI") == 0) fieldType = 0;
            else if (subPhrase[3].substring(0, 3).compareTo("WRB") == 0) fieldType = 1;
            else if (subPhrase[3].substring(0, 3).compareTo("RDI") == 0) fieldType = 2;
            else if (subPhrase[3].substring(0, 3).compareTo("RDB") == 0) fieldType = 3;
            else if (subPhrase[3].substring(0, 3).compareTo("RES") == 0) fieldType = 4;
            else return 4; // bad 4th field, exit

            // process 4th field


            switch (fieldType) { // 3rd switch

              case 0:
                if (subPhrase[4].substring(0, 2).compareTo("0X") == 0) {
                  framAddress = hexToDec(subPhrase[4].substring(2, subPhrase[4].length()));
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Write 2 bytes to FRAM, starting from address = ");
                  Serial.print(subPhrase[4]);
                } else {
                  framAddress = subPhrase[4].toInt();
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Write 2 bytes to FRAM, starting from address = ");
                  Serial.print(framAddress);
                }
                framIntData =  subPhrase[5].toInt();
                if (framIntData < -32768 || framIntData > 32767) return 6; // bad 6h field
                Serial.print(" Data = ");
                Serial.println(framIntData);

                fram.writeInt(framAddress, framIntData);
                break;

              case 1:
                if (subPhrase[4].substring(0, 2).compareTo("0X") == 0) {
                  framAddress = hexToDec(subPhrase[4].substring(2, subPhrase[4].length()));
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Write 1 byte to FRAM, Address = ");
                  Serial.print(subPhrase[4].substring(2, subPhrase[4].length()));
                } else {
                  framAddress = subPhrase[4].toInt();
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Write 1 byte to FRAM, Address = ");
                  Serial.print(framAddress);
                }
                framByteData =  subPhrase[5].toInt();
                if (framByteData < 0 || framByteData > 255) return 6; // bad 6h field
                Serial.print(" Data = ");
                Serial.println(framByteData);

                fram.writeByte(framAddress, framByteData);
                break;


              case 2:
                if (subPhrase[4].substring(0, 2).compareTo("0X") == 0) {
                  framAddress = hexToDec(subPhrase[4].substring(2, subPhrase[4].length()));
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Read 2 bytes from FRAM, starting from address = ");
                  Serial.print(subPhrase[4].substring(2, subPhrase[4].length()));
                } else {
                  framAddress = subPhrase[4].toInt();
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Read 2 bytes from FRAM, starting from address = ");
                  Serial.print(framAddress);
                }

                Serial.print(" Data = ");
                Serial.println(fram.readInt(framAddress));
                break;


              case 3:
                if (subPhrase[4].substring(0, 2).compareTo("0X") == 0) {
                  framAddress = hexToDec(subPhrase[4].substring(2, subPhrase[4].length()));
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Read 1 byte from FRAM at address = ");
                  Serial.print(subPhrase[4].substring(2, subPhrase[4].length()));
                } else {
                  framAddress = subPhrase[4].toInt();
                  if (framAddress < 0x5000 || framAddress > 0x57FF) return 5; // bad 5h field
                  Serial.print("Read 1 byte from FRAM at address = ");
                  Serial.print(framAddress);
                }

                Serial.print(" Data = ");
                Serial.println(fram.readByte(framAddress));
                break;

              case 4:
                if (subPhrase[4].substring(0, 3).compareTo("ALL") == 0) {
                  for (int i = 0; i < 0x140; i++) fram.writeByte(0x5000 + i, 0);
                  Serial.println("Correction coefficients in FRAM erased for ALL channels");
                } else if (subPhrase[4].substring(0, 4).compareTo("THIS") == 0) {

                  fram.writeInt(0x5000 + VOLTAGE_MODE_COARSE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + VOLTAGE_MODE_FINE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + VOLTAGE_MODE_COARSE_GAIN_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + VOLTAGE_MODE_FINE_GAIN_BASE_ADDRESS + 2 * channel, 0);

                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_COARSE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_FINE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_COARSE_GAIN_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE0_FINE_GAIN_BASE_ADDRESS + 2 * channel, 0);

                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE1_COARSE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE1_FINE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE1_COARSE_GAIN_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE1_FINE_GAIN_BASE_ADDRESS + 2 * channel, 0);

                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE2_COARSE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE2_FINE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE2_COARSE_GAIN_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE2_FINE_GAIN_BASE_ADDRESS + 2 * channel, 0);

                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE3_COARSE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE3_FINE_OFFSET_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE3_COARSE_GAIN_BASE_ADDRESS + 2 * channel, 0);
                  fram.writeInt(0x5000 + CURRENT_MODE_RANGE3_FINE_GAIN_BASE_ADDRESS + 2 * channel, 0);

                  Serial.print("Correction coefficients in FRAM erased for hannel ");
                  Serial.println(channel, DEC);

                } else return 5; // bad 5h field

                break;

            } // end 3rd switch

            break;


        } // end 2nd switch

        break;

      /* ************* CO ***************/
      case 3: // command CO
        return 90;
        break;
    } // end 1st switch


    return 99; // something was received to be processed

  } // end if (Serial.available())
  return 0; // no incomming string

}// end parseInputFromConsole(void)
