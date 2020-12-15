/*
  calculates keyboard button location limits
  argument: none
  return: nothing
  changes global variables keyboardXBounds,keyboardYBounds
*/
void buildKeyboardKeyBounds() {
  for (int i = 0; i < 4; i++) {
    keyboardXBounds[2 * i] = 5 + 60 * i;
    keyboardXBounds[2 * i + 1] = keyboardXBounds[2 * i] + 50;
    keyboardYBounds[2 * i] =  70 + 60 * i;
    keyboardYBounds[2 * i + 1] = keyboardYBounds[2 * i] + 50;
  }

#ifdef KBD_DEBUG
  for (int i = 0; i < 8; i++) {
    Serial.print(keyboardXBounds[i], DEC);
    Serial.print("  ");
    Serial.println(keyboardYBounds[i], DEC);
  }
#endif
}

/*
  calculates DAC value and range if applicable
  sourceType =0 -> voltage, =1 -> current
  return: nothing
  changes global variables calculatedDacValue and calculatedRange
*/
void extractFromKeyboardValue(uint8_t sourceType, uint8_t channel) {

  float absval;

  if (keyboardUnit == 0) {// uA or mV
    if (!sourceType) {
      calculatedDacValue = (int)keyboardValue; //voltage

      if (calculatedDacValue > channelMaxVoltage[channel]) calculatedDacValue = channelMaxVoltage[channel];
      if (calculatedDacValue < channelMinVoltage[channel]) calculatedDacValue = channelMinVoltage[channel];

    } else { // current
      if ((int)keyboardValue > channelMaxCurrent[channel]) keyboardValue = (float)channelMaxCurrent[channel];
      if ((int)keyboardValue < channelMinCurrent[channel]) keyboardValue = (float)channelMinCurrent[channel];
      absval = abs(keyboardValue);
      if ( absval <= HIGH_COMPLIANCE_MAX_CURRENT / 1000) {
        calculatedDacValue = (int)(keyboardValue * 1000);
        calculatedRange = 0;
      } else if ( absval <= HIGH_COMPLIANCE_MAX_CURRENT / 100) {
        calculatedDacValue = (int)(keyboardValue * 100);
        calculatedRange = 1;
      } else if ( absval <= HIGH_COMPLIANCE_MAX_CURRENT / 10) {
        calculatedDacValue = (int)(keyboardValue * 10);
        calculatedRange = 2;
      } else {
        calculatedDacValue = (int)(keyboardValue);
        calculatedRange = 3;
      }
    }
  }
  if (keyboardUnit == 1) { // mA or V
    if (!sourceType) {
      calculatedDacValue = (int)(keyboardValue * 1000); //voltage

      if (calculatedDacValue > channelMaxVoltage[channel]) calculatedDacValue = channelMaxVoltage[channel];
      if (calculatedDacValue < channelMinVoltage[channel]) calculatedDacValue = channelMinVoltage[channel];

    } else { // current
      keyboardValue *= 1000; // converted in uA
      
      if ((int)keyboardValue > channelMaxCurrent[channel]) keyboardValue = (float)channelMaxCurrent[channel];
      if ((int)keyboardValue < channelMinCurrent[channel]) keyboardValue = (float)channelMinCurrent[channel];
      
      absval = abs(keyboardValue);
      
      if ( absval <= HIGH_COMPLIANCE_MAX_CURRENT / 1000) {
        calculatedDacValue = (int)(keyboardValue * 1000);
        calculatedRange = 0;
      } else if ( absval <= HIGH_COMPLIANCE_MAX_CURRENT / 100) {
        calculatedDacValue = (int)(keyboardValue * 100);
        calculatedRange = 1;
      } else if ( absval <= HIGH_COMPLIANCE_MAX_CURRENT / 10) {
        calculatedDacValue = (int)(keyboardValue * 10);
        calculatedRange = 2;
      } else {
        calculatedDacValue = (int)keyboardValue;
        calculatedRange = 3;
      }
    }
  }

  // here, calculatedDacValue and calculatedRange are updated


} // end extractFromKeyboardValue()

/*
    Draws a keyboard button at position x0,y0
    argument(s): x0, y0 coordinates in the screen
    return: nothing
*/
void keyboardKeyButton(uint16_t x0, uint16_t y0) {
  tft.fillRoundRect(KBDBUTTON_X + x0, KBDBUTTON_Y + y0, KBDBUTTON_W, KBDBUTTON_H, RADIUS, ILI9341_BLUE);
} // end keyboardKeyButton()

/*
   Puts a label in the keyboard button at x0,y0 position
   Labels are from a table: keyboardButtonVoltLabelList[] for voltage keyboard
                            keyboardButtonAmpLabelList[] for current keyboard
   Argument(s): x0, y0 coordinates in the screen,  index: position of the label in the table, sourceType: to select the table
   Return: nothing
*/
void keyboardButtonLabel(uint16_t x0, uint16_t y0, uint8_t index, uint8_t sourceType) {
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(3 + x0, 8 + y0);
  if (!sourceType) tft.print(keyboardButtonVoltLabelList[index]);
  else tft.print(keyboardButtonAmpLabelList[index]);
} // end keyboardButtonLabel()

/*

*/
void resetKeyboardDisplayZone(uint16_t x0, uint16_t y0) {
  tft.fillRect(KBZONE_X + x0, KBZONE_Y + y0, KBZONE_W, KBZONE_H, ILI9341_BLACK);
  tft.drawRoundRect(KBZONE_X + x0, KBZONE_Y + y0, KBZONE_W, KBZONE_H, RADIUS, ILI9341_WHITE);
} // end resetKeyboardDisplayZone()

/*

*/
TS_Point getTsPoint(void) {
  TS_Point p;
  while (!ts.touched()) {}  // wait until a key is pressed
  while (ts.touched()) {} // wait until the key is released
  while (!ts.bufferEmpty()) {
    p = ts.getPoint(); // Retrieve a  single point
  }

  p.x = map(p.x, ts_minx, ts_maxx, 0, 240);
  p.y = map(p.y, ts_miny, ts_maxy, 0, 320);
  return p;
} // end getTsPoint()

/*

*/
int keyboardWhichKeyPressed(TS_Point p) {
  int  i0 = -1;
  for (uint8_t i = 0; i < 4; i++) {
    if (isInside(p.x, keyboardXBounds[2 * i], keyboardXBounds[2 * i + 1])) i0 = i;
  }

  int j0 = -1;
  for (uint8_t j = 0; j < 4; j++) {
    if (isInside(p.y, keyboardYBounds[2 * j], keyboardYBounds[2 * j + 1])) j0 = j;
  }

#ifdef KBD_DEBUG
  Serial.print(i0, DEC);
  Serial.print("  ");
  Serial.print(j0, DEC);
  Serial.print("  ");
  Serial.println(i0 + 4 * j0, DEC);
#endif

  return i0 + 4 * j0; // negative if out of key
} // end keyboardWhichKeyPressed()

///*
// *
// */
// uint8_t isInside(uint16_t x, uint16_t a, uint16_t b) {
//  if ((x >= a) and (x <= b)) return 1;
//  else return 0;
//} // end isInside()
