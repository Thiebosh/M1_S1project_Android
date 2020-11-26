/*
   Processes inputs from touchscreen
   argument(s): none
   returns:
    255: nothing pressed or only context changed (new context displayed)
    0 to 254:  a button requiring further action is pressed
*/

uint8_t getInputFromTouchScreen() {
  Serial.println("exec fct getInputFromTouchScreen in 050"); // changement

  uint8_t x = 255; // init, means no result yet
  uint8_t y = 255;

  if (ts.touched()) { //gets a point if ts touched  during this iteration
    Serial.println("if ts touched in 050"); // changement
    while (ts.touched()) { // changement : block while = while (ts.touched()) {}

      if (hc06.available() > 0) {
        strCmd = hc06.readString();
        Serial.print("I received : ");
        Serial.println(strCmd);
        //hc06.print(strCmd);
        //hc06.println("\n");
        if (strCmd.equals("get")) {
          uint8_t reponse = getChannelValue(0);
          //hc06.print(reponse);

          String c1 = "{\"id\":0,\"isActive\":false,\"currentValue\":2.6,\"minValue\":0,\"maxValue\":5,\"type\":V,\"scale\":m},";
          String c2 = "{\"id\":1,\"isActive\":true,\"currentValue\":3.8,\"minValue\":0,\"maxValue\":5,\"type\":I,\"scale\":u},";
          String c3 = "{\"id\":2,\"isActive\":false,\"currentValue\":6.9,\"minValue\":5,\"maxValue\":10,\"type\":V,\"scale\":m},";
          String c4 = "{\"id\":3,\"isActive\":false,\"currentValue\":1.4,\"minValue\":0,\"maxValue\":5,\"type\":V,\"scale\":m},";
          String c5 = "{\"id\":4,\"isActive\":false,\"currentValue\":6.7,\"minValue\":2,\"maxValue\":7,\"type\":I,\"scale\":k},";
          String c6 = "{\"id\":5,\"isActive\":true,\"currentValue\":2.587,\"minValue\":0,\"maxValue\":5,\"type\":V,\"scale\":_},";
          String c7 = "{\"id\":6,\"isActive\":false,\"currentValue\":102,\"minValue\":50,\"maxValue\":150,\"type\":I,\"scale\":_},";
          String c8 = "{\"id\":7,\"isActive\":true,\"currentValue\":0.25,\"minValue\":0,\"maxValue\":1,\"type\":V,\"scale\":M}";
          String init = "{\"channalList\":["+c1+c2+c3+c4+c5+c6+c7+c8+"]}";
          
          hc06.print(init);
        }
        if (strCmd.equals("set")) {
          setChannelValue(0,(getChannelValue(0)+1)%2);
        }
      }

      /*
        while(hc06.available())
        {
        char data = hc06.read();
        Serial.write(data);
        hc06.write(data);
        }
      */
    }
    Serial.println("after while ts touched in 050"); // changement

    while  (!ts.bufferEmpty()) {
      p = ts.getPoint();
    }

    p.x = map(p.x, TS_MINX, TS_MAXX, 0, 240); // mapped coordinates of point
    p.y = map(p.y, TS_MINY, TS_MAXY, 0, 320);

#ifdef DEBUG_TS
    Serial.print("Debug TS -> p.x= ");
    Serial.print(p.x);
    Serial.print("  p.y = ");
    Serial.println(p.y);
#endif
  } else return 255; //ts not touched during this iteration

  // *****************************************************************************
  // MAIN DISPLAY CONTEXT
  // *****************************************************************************
  /*
      in  context #10000, the function returns either:
      255 and updates isChannelLabelChanged and isChannelActiveStatusChanged accordingly
      255 for all other actions
  */
  if (currentContext == 10000) {  // test if main context

    // checks first which line
    for (uint8_t i = 0; i < 8; i++) {
      if (isInside(p.y, 2 + 35 * i, 2 + 35 * i + CHANNELLABELBUTTON_H)) y = i;
    }
    if (isInside(p.y, 290, 290 + BOTTOMBUTTON_H)) y = 8;

    // then checks which column

    if (isInside(y, 0, 7)) {
      if  (isInside(p.x, 0, CHANNELLABELBUTTON_W)) x = 0;
      if  (isInside(p.x, 57, 57 + VALUEZONE_W)) x = 1;
    }

    if (y == 8) {
      for (uint8_t i = 0; i < 4; i++) {
        if  (isInside(p.x, 0 + 60 * i, 0 + 60 * i + BOTTOMBUTTON_W)) x = i;
      }
    }
    // here, we have the row & column number of the button
#ifdef DEBUG_TS
    Serial.print("Debug TS -> x= ");
    Serial.print(x);
    Serial.print(" y = ");
    Serial.println(y);
#endif

    if (x == 255 || y == 255) return 255; // not a button , exit

    if ((y != 8) && (x == 0)) { // channel status changes
      isChannelLabelChanged = (0x01 << y);
      isChannelActiveStatusChanged = (0x01 << y);
    }

    if ((y != 8) && (x == 1)) { // channel selection status changes
      isChannelValueChanged = (0x01 << y);
      PreviousChannelSelectedStatus = channelSelectedStatus;
      channelSelectedStatus = (0x01 << y);
    }


    if ((y == 8) && (x == 0)) { // menu button
      resetTsInput();
      currentContext = 20000;
      menuDisplayContext();

    }// end menu button

    if ((y == 8) && (x == 1)) { // keyboard button
      resetTsInput();
      currentContext = 11000;
      if (displayKeyboardContext(channelModeStatus & 0x01 << currentSelectedChannel)) { // new keyboard value available

        extractFromKeyboardValue(channelModeStatus & 0x01 << currentSelectedChannel, currentSelectedChannel);
        //here, we have updated global variables calculatedDacValue and calculatedRange
        setChannelValue(currentSelectedChannel, calculatedDacValue);
        setChannelRange(currentSelectedChannel, calculatedRange);
        extractDisplayValueFromDACValue(currentSelectedChannel);
      }

      currentContext = 10000;
      fullChannelDisplayContext();

    }// end keyboard button

    if ((y == 8) && (x == 2)) { // left arrow button
      for (uint8_t i = 0; i < 8; i++) {
        if  (channelSelectedStatus & 0x01 << i) {
          digitSelected [i]++;
          checkDigitSelectedLimits(i); // checks if value do not extend beyhond limits
          isChannelValueChanged = (0x01 << i);
        }
      }
    }// end left arrow button

    if ((y == 8) && (x == 3)) { // rignt arrow button
      for (uint8_t i = 0; i < 8; i++) {
        if  (channelSelectedStatus & 0x01 << i) {
          digitSelected [i]--;
          checkDigitSelectedLimits(i); // checks if value do not extend beyhond limits
          isChannelValueChanged = (0x01 << i);
        }
      }
    }// end right arrowbutton
    return 255; // done
  } //end if (currentContext == 10000)

  // *****************************************************************************
  // MEMU CONTEXT 20000
  // *****************************************************************************

  if (currentContext == 20000) {  // test if menu context
    // checks  which line
    for (uint8_t i = 0; i < 8; i++) {
      if (isInside(p.y, 10 + 40 * i, 10 + 40 * i + MENUBUTTON_H)) y = i;
    }

    if (y == 0) { // change source type
      resetTsInput();
      changeSourceTypeMenuDisplay();
      currentContext = 21000;

    }

    if (y == 1) { // modify limits
      resetTsInput();
      currentContext = 22000;
      fullLimitsMenuDisplay(0);
    }

    if (y == 2) { // user store
      resetTsInput();
      currentContext = 23000;
      userStoreMenuDisplay();
    }

    if (y == 3) { // user recall
      resetTsInput();
      currentContext = 24000;
      userRecallMenuDisplay();
    }

    if (y == 4) { // about
      resetTsInput();
      menuDisplayAbout();
    }

    if (y == 5) { //back
      resetTsInput();
      fullChannelDisplayContext();
      currentContext = 10000;
    }
    return 255; // done
  }//end if (currentContext == 20000)

  // *****************************************************************************
  // SOURCE TYPE CONTEXT 21000
  // *****************************************************************************
  if (currentContext == 21000) {  // test if source type context
    // checks  which line
    for (uint8_t i = 0; i < 9; i++) {
      if (isInside(p.y, 5 + 35 * i, 5 + 35 * i + SOURCETYPEBUTTON_H)) y = i;
    }

    if (isInside(y, 0, 7)) { // change source type
      if (boardOnSlotStatus & 0x01 << y) {
        channelModeStatus = channelModeStatus ^ (0x01 << y); // XOR complements variables when second operator is =1


        setChannelType(y, (channelModeStatus >> y  & 0x01));
        setChannelValue(y, 0);

        channelActiveStatus &= ~(0x01 << y);
        digitSelected[y] = -1; // resets the selected digit
        displaySourceTypeMenuLine(0, 5 + 35 * y, y);

      } // end if (boardOnSlotStatus & 0x01 << y)


    } // end if (isInside(y, 0, 7))

    if (y == 8) { //back
      resetTsInput();
      menuDisplayContext();
      currentContext = 20000;
    }


    return 255; // done
  } // end if (currentContext == 21000)

  // *****************************************************************************
  // LIMITS CONTEXT 22000
  // *****************************************************************************
  if (currentContext == 22000) {  // test if limits context
    int templimit = 0;
    int isValue = 0;
    uint8_t limitInError = 0;
    // Which row?
    for (uint8_t i = 0; i < 8; i++) {
      if (isInside(p.y, 25 + 30 * i, 50 + 30 * i)) y = i;
    }
    if (isInside(p.y, 290, 320)) y = 8;

    // which column?
    if (isInside(y, 0, 7)) {
      if (isInside(p.x, 24, 124)) x = 0;
      if (isInside(p.x, 130, 230)) x = 1;
    } // end if (isInside(y, 0, 7))
    if (y == 8 && isInside(p.x, 0, 100)) x = 0;
    //   if (y == 8 && isInside(p.x, 120, 170)) x = 1;
    if (y == 8 && isInside(p.x, 180, 230)) x = 2;

    if (isInside(y, 0, 7)) { // change value
      resetTsInput();
      if (boardOnSlotStatus & 0x01 << y) {
        currentContext == 22100;
        isValue = displayKeyboardContext(channelModeStatus & 0x01 << y);

        if (isValue) { // a new value is available
          if (keyboardUnit) templimit = (int)(keyboardValue * 1000); // 0: mV or uA, 1: V or mA
          else templimit = (int)keyboardValue;

          if (channelModeStatus & 0x01 << y) { // current
            if (templimit > HIGH_COMPLIANCE_MAX_CURRENT) templimit = HIGH_COMPLIANCE_MAX_CURRENT;
            if (templimit < HIGH_COMPLIANCE_MIN_CURRENT) templimit = HIGH_COMPLIANCE_MIN_CURRENT;
            if (x) {
              if (templimit > channelMinCurrent[y])channelMaxCurrent[y] = templimit;
              else limitInError = 1;
            }
            else {
              if (templimit < channelMaxCurrent[y]) channelMinCurrent[y] = templimit;
              else limitInError = 1;
            }
          } else {// voltage
            if (templimit > DEF_ABSOLUTE_MAX_VALUE) templimit = DEF_ABSOLUTE_MAX_VALUE;
            if (templimit < DEF_ABSOLUTE_MIN_VALUE) templimit = DEF_ABSOLUTE_MIN_VALUE;
            if (x)  {
              if (templimit > channelMinVoltage[y])channelMaxVoltage[y] = templimit;
              else limitInError = 1;
            }
            else  {
              if (templimit < channelMaxVoltage[y]) channelMinVoltage[y] = templimit;
              else limitInError = 1;
            }
          }

          // are values coherent?
        } // end if(isValue)

        fullLimitsMenuDisplay(limitInError);
        currentContext == 22000;

      }// end if (boardOnSlotStatus & 0x01 << y)

    }// end if (isInside(y, 0, 7))


    if (x == 0 && y == 8) { // DEf. Val button
      resetTsInput();
      for (uint8_t i = 0; i < 8; i++) {
        channelMaxVoltage[i] = DEF_MAX_VOLTAGE;
        channelMinVoltage[i] = DEF_MIN_VOLTAGE;
        channelMaxCurrent[i] = HIGH_COMPLIANCE_MAX_CURRENT;
        channelMinCurrent[i] = HIGH_COMPLIANCE_MIN_CURRENT;
      }
      fullLimitsMenuDisplay(0);
      currentContext == 22000;
    }

    if (x == 2 && y == 8) { // OK button
      resetTsInput();
      menuDisplayContext();
      currentContext = 20000;
    }
    return 255; // done
  }// end if (currentContext == 22000)

  // *****************************************************************************
  // USER STORE CONTEXT 23000
  // *****************************************************************************
  if (currentContext == 23000) {  // test if user store context
    // checks  which line
    for (uint8_t i = 0; i < 10; i++) {
      if (isInside(p.y, 5 + 35 * i, 5 + 35 * i + USERSTORERECALLBUTTON_H)) y = i;
    }

    if (isInside(y, 0, 7)) {
      storeUserCongig(y) ;
      resetTsInput();
      menuDisplayContext();
      currentContext = 20000;

    } // end if (isInside(y, 0, 7))

    if (y == 8) { //Abort
      resetTsInput();
      menuDisplayContext();
      currentContext = 20000;
    }


    return 255; // done
  } // end if (currentContext == 2300)

  // *****************************************************************************
  // USER RECALL CONTEXT 24000
  // *****************************************************************************
  if (currentContext == 24000) {  // test if user recall context
    // checks  which line
    for (uint8_t i = 0; i < 10; i++) {
      if (isInside(p.y, 5 + 35 * i, 5 + 35 * i + USERSTORERECALLBUTTON_H)) y = i;
    }

    if (isInside(y, 0, 7)) {

      if (userConfigStored & (0x01 << y)) {// a config was stored here, restore and exit

        recallUserCongig(y) ;
        resetTsInput();
        menuDisplayContext();
        currentContext = 20000;
      } else { // no config stored here, no action and stay in the same menu
        resetTsInput();
      }

    } // end if (isInside(y, 0, 7))

    if (y == 8) { //Abort
      resetTsInput();
      menuDisplayContext();
      currentContext = 20000;
    }

    return 255; // done
  } // end if (currentContext == 2300)

  return 255;

}//end getInputFromTouchScreen()
