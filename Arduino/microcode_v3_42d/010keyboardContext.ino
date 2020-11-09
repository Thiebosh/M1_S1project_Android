/*
  emulates a keyboard on the screen, key (12) ignored
  argument(s): sourceType =0 -> voltage, =1 -> current
  return: 0 = abort, 1 = value available
  changes global variables keyboardValue and keyboardUnit
*/
uint8_t displayKeyboardContext(uint8_t sourceType) {
  Serial.println("exec fct displayKeyboardContext in 010"); // changement

  TS_Point p;
  int key;
  unsigned int valint, valdec;
  uint8_t decimal, decindex, negate;


  tft.fillScreen(ILI9341_BLACK);
  for (int i = 0; i < 4; i++) {
    for (int j = 0; j < 4; j++) {
      keyboardKeyButton(5 + 60 * j, 70 + 60 * i);
      keyboardButtonLabel(5 + 60 * j, 70 + 60 * i, 4 * i + j, sourceType);
    }
  }


  resetKeyboardDisplayZone(20, 29);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(3);
  key = 0;
  negate = 0;
  valint = 0;
  valdec = 0;
  decimal = 0;
  decindex = 0;


  while (key <= 12) {
    p = getTsPoint();
    key = keyboardWhichKeyPressed(p);

    if (key >= 0) { // negative value means key not recognized

      if (key < 10) {
        if (!decimal) {
          valint = valint * 10 + key;
        } else {
          valdec = valdec * 10 + key;
          decindex++;
        }
      } // end if (key < 10)

      if (key == 10) {
        decimal = 1;
      }

      if (key == 11) {
        negate = !negate;
      }

      // affichage
      resetKeyboardDisplayZone(20, 29);
      tft.setCursor(22, 34);
      keyboardValue = (float)valint + (float)valdec * pow(10.0, -decindex);
      if (negate) keyboardValue = -keyboardValue ;
      tft.print(keyboardValue, decindex);
      
    } // end if (key>=0)
    
  }// end while (key <= 12)


  if (key == 13) keyboardUnit = 0; // uA or mV

  if (key == 14) keyboardUnit = 1; // mA or V

  if (key >= 15) return 0; else return 1;  // value indicating abort or available result

} // end displayKeyboardContext()
