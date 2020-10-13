/*
   test DAC boards
   arguments: none
   returns: 0 = all passed, 1 = dac0 failed, 2 = dac1 failed, 3 = both failed
*/
uint8_t testDac(void) {
  uint8_t result = 0;

  tft.print("** Test DAC0... ");
  dac0.write(FUNCTION_REGISTER, FUNCTION_REGISTER_WRITE, 0x00, INDIV_UPDATE); //  enable SDO pin of DAC0
  dac1.write(FUNCTION_REGISTER, FUNCTION_REGISTER_WRITE, 0x01, INDIV_UPDATE); // disable SDO pin of DAC1

  dac0.write(FINE_GAIN_REGISTER, DAC_A, 0x25, INDIV_UPDATE);
  if (dac0.read(FINE_GAIN_REGISTER, DAC_A) == 0x25) {
    dac0.write(FINE_GAIN_REGISTER, DAC_A, 0x00, INDIV_UPDATE); // restores correct value
    dac0.write(FUNCTION_REGISTER, FUNCTION_REGISTER_CLEAR, 0x00, INDIV_UPDATE); // reset all DAC values to 0
    tft.setTextColor(ILI9341_GREEN);
    tft.println("PASSED");
    tft.setTextColor(ILI9341_BLUE);
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("FAILED");
    tft.setTextColor(ILI9341_BLUE);
    result += 1;
  }


#ifdef DEBUG_DACTEST
  Serial.print("Test of dac0 returns: ");
  Serial.println(result);
#endif

  tft.print("** Test DAC1... ");
  dac0.write(FUNCTION_REGISTER, FUNCTION_REGISTER_WRITE, 0x01, INDIV_UPDATE); // disable SDO pin of DAC0
  dac1.write(FUNCTION_REGISTER, FUNCTION_REGISTER_WRITE, 0x00, INDIV_UPDATE); //  enable SDO pin of DAC1

  dac1.write(FINE_GAIN_REGISTER, DAC_A, 0x25, INDIV_UPDATE);
  if (dac1.read(FINE_GAIN_REGISTER, DAC_A) == 0x25) {
    dac1.write(FINE_GAIN_REGISTER, DAC_A, 0x00, INDIV_UPDATE); // restores correct value
    dac1.write(FUNCTION_REGISTER, FUNCTION_REGISTER_CLEAR, 0x00, INDIV_UPDATE); // reset all DAC values to 0
    tft.setTextColor(ILI9341_GREEN);
    tft.println("PASSED");
    tft.setTextColor(ILI9341_BLUE);
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("FAILED");
    tft.setTextColor(ILI9341_BLUE);
    result += 2;
  }

  dac1.write(FUNCTION_REGISTER, FUNCTION_REGISTER_WRITE, 0x01, INDIV_UPDATE); // disable SDO pin of DAC1
  // SDO pin disabled for both DAC0 and DAC1

#ifdef DEBUG_DACTEST
  Serial.print("Test of dac1 returns: ");
  Serial.println(result);
#endif
  return result;
}



/*
  Detect boards on slot
  arguments: none
  returns: boards on slot status , 1 bit per slot, bit 7..0 = slot 7..0, 0: no board, 1: board connected
*/
uint8_t detectBoardOnSlot() {
  uint8_t slotStatus = 0;

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 0: ");
  if (!digitalRead(ONSLOT_0)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x01;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 1: ");
  if (!digitalRead(ONSLOT_1)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x02;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 2: ");
  if (!digitalRead(ONSLOT_2)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x04;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 3: ");
  if (!digitalRead(ONSLOT_3)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x08;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 4: ");
  if (!digitalRead(ONSLOT_4)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x10;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 5: ");
  if (!digitalRead(ONSLOT_5)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x20;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 6: ");
  if (!digitalRead(ONSLOT_6)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x40;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  tft.print("Slot 7: ");
  if (!digitalRead(ONSLOT_7)) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("board connected");
    slotStatus |= 0x80;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("empty slot");
  }

  tft.setTextColor(ILI9341_BLUE);
  return slotStatus;
} // end detectBoardOnSlot()


/*
   Test FRAM memory
   arguments: chip address
   returns: 0: test failed, 1: test passed
*/
uint8_t testFramMemory(uint8_t address) {
  tft.print("** FRAM test... ");
  Wire.beginTransmission(address);
  if (!Wire.endTransmission()) {
    tft.setTextColor(ILI9341_GREEN);
    tft.println("PASSED");
    tft.setTextColor(ILI9341_BLUE);
    return 1;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("FAILED");
    tft.setTextColor(ILI9341_BLUE);
    return 0;
  }
} // end testFramMemory()

/*
   Test connected boards
   arguments: slot address (0..7)
   returns: 0: test failed, 1: test passed
*/
uint8_t testConnectedBoard(uint8_t slot) {
  tft.print("** Test board in slot ");
  tft.print(slot);
  tft.print(" ...");
  Wire.beginTransmission(MCP23xxx_ADDRESS + slot);

  if (!Wire.endTransmission()) {
    //   tft.setTextColor(ILI9341_GREEN);
    //   tft.println("PASSED");
    //    tft.setTextColor(ILI9341_BLUE);
    return 1;
  } else {
    tft.setTextColor(ILI9341_RED);
    tft.println("FAILED");
    tft.setTextColor(ILI9341_BLUE);
    return 0;
  }
} // end testConnectedBoard(uint8_t slot)

/*
  set-up connected boards (low level)
   arguments: slot address (0..7)
   returns: nothing
*/
void setupConnectedBoard(uint8_t slot) {
  Wire.beginTransmission(MCP23xxx_ADDRESS + slot);
  Wire.write(MCP23xxx_GPPU);
  Wire.write(IVSOURCE_JUMPERS_READ); // GP2 input pull-up
  Wire.endTransmission();
}// end setupConnectedBoard(uint8_t slot)

/*
  set-up connected boards (low level)
   arguments: slot address (0..7)
   returns: board type (0..254), read error = 255
*/
uint8_t readConnectedBoard(uint8_t slot) {
  Wire.beginTransmission(MCP23xxx_ADDRESS + slot);
  Wire.write(MCP23xxx_GPIO);
  Wire.endTransmission();

  Wire.requestFrom(MCP23xxx_ADDRESS + slot, (uint8_t)1);

  if (Wire.available())                 // slave may send less than requested
  {
    return Wire.read() & IVSOURCE_JUMPERS_READ;
  }
  else {
    return 255;
  }
}// end readConnectedBoard(uint8_t slot)

/*
  reset IV source
   arguments: slot address (0..7)
   returns: nothing
*/
void ivSourceReset(uint8_t slot) {
  switch (slot) {
    case (0):
      IV00.init();
      break;
    case (1):
      IV01.init();
      break;
    case (2):
      IV02.init();
      break;
    case (3):
      IV03.init();
      break;
    case (4):
      IV04.init();
      break;
    case (5):
      IV05.init();
      break;
    case (6):
      IV06.init();
      break;
    case (7):
      IV07.init();
      break;
    default:
      break;
  }




} // end ivSourceReset(uint8_t slot)

/*
  Restore factory defaults
  arguments: none
  returns: nothing
*/

void defaultsRestore(uint8_t factory) {

  channelModeStatus = DEF_CHANNEL_MODE;
  //channelModeStatus = 0x02;
  channelActiveStatus = DEF_CHANNEL_ON;

  for (int i = 0; i < 8; i++) {
    channelValue[i] = DEF_CHANNEL_VALUE;
    channelCurrentRange[i] = DEF_CURRENT_RANGE;
    channelMaxVoltage[i] = DEF_MAX_VOLTAGE;
    channelMinVoltage[i] = DEF_MIN_VOLTAGE;
    channelMaxCurrent[i] = HIGH_COMPLIANCE_MAX_CURRENT;
    channelMinCurrent[i] = HIGH_COMPLIANCE_MIN_CURRENT;
    channelMaxRange[i] = DEF_MAX_RANGE;
    channelDisplayUnit[i] = DEF_DISPLAY_UNIT;
    channelDisplayValue[i] = DEF_CHANNEL_VALUE;
  }
  /*
     channelCurrentRange[2] = 3;
     channelDisplayValue[2] = 0.589;
     channelCurrentRange[3] = 3;
     channelDisplayValue[3] = 1.234;
  */
  absChannelMaxValue = DEF_ABSOLUTE_MAX_VALUE;
  absChannelMinValue = DEF_ABSOLUTE_MIN_VALUE;

  channelSelectedStatus = DEF_SELECTED_CHANNEL;
  //digitSelected = DEF_SELECTED_DIGIT;
  minLimitSelectedStatus = DEF_MIN_CHANNEL_LIMIT_SELECTED;
  maxLimitSelectedStatus = DEF_MAX_CHANNEL_LIMIT_SELECTED;
  userConfigStored = fram.readByte((FRAM_ADDRESS << 8) + USER_CONFIG_BASE_ADDRESS);

  if (factory) {
    userConfigStored = FACTORY_DEFAULT_USER_CONFIG_STORED;
    if (framMemoryOk) {
      fram.blankPage(PAGE3);  // erase all user configs
      fram.blankPage(PAGE4);
      fram.blankPage(PAGE5);
      fram.blankPage(PAGE6);
      fram.writeByte((FRAM_ADDRESS << 8) + USER_CONFIG_BASE_ADDRESS, FACTORY_DEFAULT_USER_CONFIG_STORED); // reset userCongigStored copy in FRAM
    }
  }
} // end defautlsRestore()


/*
   System boot
   arguments: none
   returns: nothing
*/

void systemBoot() {

} // end systemBoot()


/*
   Detection of IVsource boards
   arguments: none
   returns: nothing
*/
void ivSourceDetect() {
  ivSourcePosition = 0x00;

  for (uint8_t i = 0; i < 8; i++) {
    if (boardOnSlotStatus & (0x01 << i)) {
      if (testConnectedBoard(i)) {
        setupConnectedBoard(i); // minimal, only pull-up on GP2 and GP1
        boardType[i] = readConnectedBoard(i); // reads board type

        if (boardType[i] == 0xFF) {
          tft.setTextColor(ILI9341_RED);
          tft.println("");
          tft.println(" READ ERROR");
          tft.setTextColor(ILI9341_BLUE);
          boardOnSlotStatus &= ~(0x01 << i); // marks slot as empty

        } else if (boardType[i] & IVSOURCE_JUMPERS_READ) {
          tft.setTextColor(ILI9341_GREEN);
          tft.println("");
          tft.println(" IVsource board detected");
          ivSourcePosition |= (0x01 << i);
          ivSourceReset(i);
          tft.setTextColor(ILI9341_BLUE);
        } else {
          tft.setTextColor(ILI9341_RED);
          tft.println("");
          tft.println(" Unknown board type");
          tft.setTextColor(ILI9341_BLUE);
          boardOnSlotStatus &= ~(0x01 << i); // marks slot as empty
        }

      }
    }
  } // end for

}
