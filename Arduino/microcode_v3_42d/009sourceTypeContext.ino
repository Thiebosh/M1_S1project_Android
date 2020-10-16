#define SOURCETYPEBUTTON_X 0
#define SOURCETYPEBUTTON_Y 0
#define SOURCETYPEBUTTON_W 240
#define SOURCETYPEBUTTON_H 30 // espace 5

/*
   Display source type context
   argument(s): none
   returns: nothing
*/
void changeSourceTypeMenuDisplay() {
  Serial.println("exec fct changeSourceTypeMenuDisplay in 009"); // changement
  tft.fillScreen(ILI9341_BLACK);

  for (int i = 0; i < 8; i++) {
    if (boardOnSlotStatus & 0x01 << i) displaySourceTypeMenuLine(0, 5 + 35 * i, i);
    else displaySourceTypeMenuEmpty(0, 5 + 35 * i);
  } // end for
  displaySourceTypeMenuBack(0, 5 + 35 * 8);
}// end changeSourceTypeMenuDisplay()


void displaySourceTypeMenuLine(uint16_t x0, uint16_t y0, uint8_t channel) {
  Serial.println("exec fct displaySourceTypeMenuLine in 009"); // changement
  tft.fillRoundRect(SOURCETYPEBUTTON_X + x0, SOURCETYPEBUTTON_Y + y0, SOURCETYPEBUTTON_W, SOURCETYPEBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 8 + y0);
  tft.print("Change CH");
  tft.print(channel);

  if (channelModeStatus & 0x01 << channel) tft.print(" to V");
  else tft.print(" to I");
}

void displaySourceTypeMenuBack(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct displaySourceTypeMenuBack in 009"); // changement
  tft.fillRoundRect(SOURCETYPEBUTTON_X + x0, SOURCETYPEBUTTON_Y + y0, SOURCETYPEBUTTON_W, SOURCETYPEBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 8 + y0);
  tft.print("Back");
}

void displaySourceTypeMenuEmpty(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct displaySourceTypeMenuEmpty in 009"); // changement
  tft.drawRoundRect(SOURCETYPEBUTTON_X + x0, SOURCETYPEBUTTON_Y + y0, SOURCETYPEBUTTON_W, SOURCETYPEBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 8 + y0);
  tft.print("Not applicable");
}
