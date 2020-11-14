#define RADIUS 3

#define CHANNELLABELBUTTON_X 0
#define CHANNELLABELBUTTON_Y 0
#define CHANNELLABELBUTTON_W 53
#define CHANNELLABELBUTTON_H 25

#define VALUEZONE_X 0
#define VALUEZONE_Y 0
#define VALUEZONE_W 180
#define VALUEZONE_H 25

#define TFT_GREEN 0x03E0

#define BOTTOMBUTTON_X 0
#define BOTTOMBUTTON_Y 0
#define BOTTOMBUTTON_W 50
#define BOTTOMBUTTON_H 30

#define DIGITSELECTED_X 0
#define DIGITSELECTED_Y 0
#define DIGITSELECTED_W 17
#define DIGITSELECTED_H 25

/*
   Display full context after boot or when back from another context
   argument(s): none
   returns: nothing
*/

void fullChannelDisplayContext() {
  Serial.println("exec fct fullChannelDisplayContext in 007"); // changement
  tft.fillScreen(ILI9341_BLACK);
  for (int i = 0; i < 8; i++) {

    displayChannelLabel(i);
    displayChannelValue(i);
  } // end for

  displayBottomButtons();

}  // end fullChannelDisplayContext()



/*
   Displays the Channel label
   argument(s): channelNumber fom 0 to 7
   returns: nothing
*/
void displayChannelLabel(uint8_t channelNumber) {
  Serial.println("exec fct displayChannelLabel in 007"); // changement
  tft.setTextColor(ILI9341_WHITE);

  if (boardOnSlotStatus & 0x01 << channelNumber) {
    if (channelActiveStatus & 0x01 << channelNumber) channelOnButton(0, 2 + 35 * channelNumber);
    else channelOffButton(0, 2 + 35 * channelNumber);
    tft.setTextSize(3);
    addChannelLabel(2, 4 + 35 * channelNumber, channelNumber);
  } else channelNonexistentButton(0, 2 + 35 * channelNumber);

}// end channelDisplayLabels()

/*
   channel buttons OFF, ON, empty
   arguments: x0, y0: current coordiantes
   returns: nothing
*/
void channelOnButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct channelOnButton in 007"); // changement
  tft.fillRoundRect(CHANNELLABELBUTTON_X + x0, CHANNELLABELBUTTON_Y + y0, CHANNELLABELBUTTON_W, CHANNELLABELBUTTON_H, RADIUS, TFT_GREEN);
}

void channelOffButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct channelOffButton in 007"); // changement
  tft.fillRoundRect(CHANNELLABELBUTTON_X + x0, CHANNELLABELBUTTON_Y + y0, CHANNELLABELBUTTON_W, CHANNELLABELBUTTON_H, RADIUS, ILI9341_RED);
}

void channelNonexistentButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct channelNonexistentButton in 007"); // changement
  tft.fillRoundRect(CHANNELLABELBUTTON_X + x0, CHANNELLABELBUTTON_Y + y0, CHANNELLABELBUTTON_W, CHANNELLABELBUTTON_H, RADIUS, ILI9341_BLACK);
  tft.drawRoundRect(CHANNELLABELBUTTON_X + x0, CHANNELLABELBUTTON_Y + y0, CHANNELLABELBUTTON_W, CHANNELLABELBUTTON_H, RADIUS, ILI9341_WHITE);
}

/*
  prints the channel label Vx: or Ix:
  argument(s): x0, y0: current coordiantes
               channel: channel number
  returns: nothing
*/
void addChannelLabel(uint16_t x0, uint16_t y0, uint8_t channel) {
  Serial.println("exec fct addChannelLabel in 007"); // changement
  tft.setCursor(x0, y0);
  if ((channelModeStatus >> channel) & 0x01) tft.print("I");
  else tft.print("V");
  tft.print(channel, DEC);
  tft.print(":");
}

/*
   Displays the Channel value
   argument(s): channelNumber fom 0 to 7
   returns: nothing
*/
void displayChannelValue(uint8_t channelNumber) {
  Serial.println("exec fct displayChannelValue in 007"); // changement


  if (boardOnSlotStatus & 0x01 << channelNumber) {
    if (channelSelectedStatus & 0x01 << channelNumber) {
      selectedValueZone(55, 2 + 35 * channelNumber);
      if (channelCurrentRange[channelNumber] == 1) {
        //       constrain(digitSelected[channelNumber],-2,1);
        if (digitSelected[channelNumber] < -2) digitSelected[channelNumber] = -2;
        if (digitSelected[channelNumber] > 1) digitSelected[channelNumber] = 1;
        if (digitSelected[channelNumber] < 0) selectedDigitZone(113 - digitSelected[channelNumber] * 18, 2 + 35 * channelNumber);
        else selectedDigitZone(115 - (digitSelected[channelNumber] + 1) * 19, 2 + 35 * channelNumber);

      } else if (channelCurrentRange[channelNumber] == 2) {
        //        constrain(digitSelected[channelNumber], -1, 2);
        if (digitSelected[channelNumber] < -1) digitSelected[channelNumber] = -1;
        if (digitSelected[channelNumber] > 2) digitSelected[channelNumber] = 2;
        if (digitSelected[channelNumber] < 0) selectedDigitZone(131 - digitSelected[channelNumber] * 18, 2 + 35 * channelNumber);
        else selectedDigitZone(133 - (digitSelected[channelNumber] + 1) * 19, 2 + 35 * channelNumber);

      } else {
        if (digitSelected[channelNumber] < -3) digitSelected[channelNumber] = -3;
        if (digitSelected[channelNumber] > 0) digitSelected[channelNumber] = 0;
        //       constrain(digitSelected[channelNumber], -3, 0);
        if (digitSelected[channelNumber] < 0) selectedDigitZone(95 - digitSelected[channelNumber] * 18, 2 + 35 * channelNumber);
        else selectedDigitZone(97 - (digitSelected[channelNumber] + 1) * 19, 2 + 35 * channelNumber);

      }

    }
    else unSelectedValueZone(57, 2 + 35 * channelNumber);
    tft.setTextSize(3);
    addChannelValue(2, 4 + 35 * channelNumber, channelNumber);
  } else unSelectedValueZone(57, 2 + 35 * channelNumber);
}

/*
   Displays the Channel value with unselected background
   argument(s): channelNumber fom 0 to 7
   returns: nothing
*/
void displayUnselectedChannelValue(uint8_t channelNumber) {
  Serial.println("exec fct displayUnselectedChannelValue in 007"); // changement


  if (boardOnSlotStatus & 0x01 << channelNumber) {
    unSelectedValueZone(55, 2 + 35 * channelNumber);
    tft.setTextSize(3);
    addChannelValue(2, 4 + 35 * channelNumber, channelNumber);
  } else unSelectedValueZone(57, 2 + 35 * channelNumber);
}



/*
   channel value zones
   arguments: x0, y0: current coordinates
   returns: nothing
*/

void unSelectedValueZone(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct unSelectedValueZone in 007"); // changement
  tft.fillRect(VALUEZONE_X + x0, VALUEZONE_Y + y0, VALUEZONE_W, VALUEZONE_H, ILI9341_BLACK);
}

void selectedValueZone(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct selectedValueZone in 007"); // changement
  tft.fillRoundRect(VALUEZONE_X + x0, VALUEZONE_Y + y0, VALUEZONE_W, VALUEZONE_H, RADIUS, ILI9341_YELLOW);
}

void selectedDigitZone(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct selectedDigitZone in 007"); // changement
  tft.fillRect(DIGITSELECTED_X + x0, DIGITSELECTED_Y + y0, DIGITSELECTED_W, DIGITSELECTED_H, ILI9341_WHITE);
}

/*
  prints the channel value
  argument(s): x0, y0: current coordiantes
               channel: channel number
  returns: nothing
*/
void addChannelValue(uint16_t x0, uint16_t y0, uint8_t channel) {
  Serial.println("exec fct addChannelValue in 007"); // changement

  float temp;

  if (channelSelectedStatus & 0x01 << channel)  tft.setTextColor(ILI9341_BLACK);
  else  tft.setTextColor(ILI9341_WHITE);

  tft.setCursor(61, 4 + 35 * channel); // print the sign
  if (channelDisplayValue[channel] >= 0) tft.print("+");
  else tft.print("-");



  temp = abs(channelDisplayValue[channel]);
  /*
    Serial.print(channelCurrentRange[channel]);
    Serial.print("   ");
    Serial.print(channelDisplayValue[channel]);
    Serial.print("   ");
    Serial.println(temp);
  */
  if (channelModeStatus & 0x01 << channel ) { // current

    switch (channelCurrentRange[channel]) {
      case 0:
        tft.print(temp, 3);
        tft.setCursor(200, 4 + 35 * channel); // print the unit
        tft.print("uA");
        break;
      case 1:
        if (temp < 10)  tft.print("0");
        tft.print(temp, 2);
        tft.setCursor(200, 4 + 35 * channel); // print the unit
        tft.print("uA");
        break;
      case 2:
        if (temp < 100) tft.print("0");
        if (temp < 10) tft.print("0");
        tft.print(temp, 1);
        tft.setCursor(200, 4 + 35 * channel); // print the unit
        tft.print("uA");
        break;
      case 3:
        tft.print(temp, 3);
        tft.setCursor(200, 4 + 35 * channel); // print the unit
        tft.print("mA");
        break;

    }


  } else { // voltage
    tft.print(temp, 3);
    tft.setCursor(200, 4 + 35 * channel); // print the unit
    tft.print("V");
  }

}// end addChannelValue(uint16_t x0, uint16_t y0, uint8_t channel)

/*
   menu, keyboard arrow left and right buttons
   arguments: x0, y0: current coordinates
   returns: nothing
*/
void menuButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct menuButton in 007"); // changement
  tft.fillRoundRect(BOTTOMBUTTON_X + x0, BOTTOMBUTTON_Y + y0, BOTTOMBUTTON_W, BOTTOMBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(3 + x0, 8 + y0);
  tft.print("Menu");
}
/*****************************************************************/
void keyboardButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct keyboardButton in 007"); // changement
  tft.fillRoundRect(BOTTOMBUTTON_X + x0, BOTTOMBUTTON_Y + y0, BOTTOMBUTTON_W, BOTTOMBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(3 + x0, 8 + y0);
  tft.print("Keyb");
}
/*****************************************************************/
void arrowLeftButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct arrowLeftButton in 007"); // changement
  tft.fillRoundRect(BOTTOMBUTTON_X + x0, BOTTOMBUTTON_Y + y0, BOTTOMBUTTON_W, BOTTOMBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.fillTriangle(15 + x0, 15 + y0, 35 + x0, 5 + y0, 35 + x0, 25 + y0, ILI9341_WHITE);
}
/*****************************************************************/
void arrowRightButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct arrowRightButton in 007"); // changement
  tft.fillRoundRect(BOTTOMBUTTON_X + x0, BOTTOMBUTTON_Y + y0, BOTTOMBUTTON_W, BOTTOMBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.fillTriangle(15 + x0, 5 + y0, 15 + x0, 25 + y0, 35 + x0, 15 + y0, ILI9341_WHITE);
}

/*
   Displays the bottom buttons
   argument(s): none
   returns: nothing
*/

void displayBottomButtons() {
  Serial.println("exec fct displayBottomButtons in 007"); // changement
  menuButton(0, 290);
  keyboardButton(60, 290);
  arrowLeftButton(120, 290);
  arrowRightButton(180, 290);
}
