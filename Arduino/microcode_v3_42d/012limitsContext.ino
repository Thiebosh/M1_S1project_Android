#define LARGEBOTTOMBUTTON_X 0
#define LARGEBOTTOMBUTTON_Y 0
#define LARGEBOTTOMBUTTON_W 110
#define LARGEBOTTOMBUTTON_H 30

#define LIMITSDISPLAYZONE_X 0
#define LIMITSDISPLAYZONE_Y 0
#define LIMITSDISPLAYZONE_W 100
#define LIMITSDISPLAYZONE_H 25

/*
   Displays the limits menu
   argument(s): error 0= normal footer, 1= error footer
   returns: nothing
*/
void fullLimitsMenuDisplay(uint8_t error) {
  Serial.println("exec fct fullLimitsMenuDisplay in 012"); // changement
  tft.fillScreen(ILI9341_BLACK);

  limitsHeader(24, 5);
  for (uint8_t i = 0; i < 8; i++) {
    limitsZones(0, 25, i);
    if (boardOnSlotStatus & 0x01 << i) limitsValues(0, 25, i); // values only if board is present
  }
  if (!error)limitsFooterOK(0, 270);
  else limitsFooterKO(0, 270);
  limitsDisplayBottomButtons();

}// end defineLimitsMenuDisplay();

void limitsHeader(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct limitsHeader in 012"); // changement
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(x0, y0);
  tft.print("Min. val");
  tft.setCursor(x0 + 106, y0);
  tft.print("Max. val");
}

void limitsZones(uint16_t x0, uint16_t y0, uint8_t channel) {
  Serial.println("exec fct limitsZones in 012"); // changement
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(x0, y0 + 5 + 30 * channel);
  tft.print(channel);
  tft.print(":");
  limitsDisplayZone(x0 + 24, y0 + 30 * channel);
  limitsDisplayZone(x0 + 130, y0 + 30 * channel);
}

void limitsValues(uint16_t x0, uint16_t y0, uint8_t channel) {
  Serial.println("exec fct limitsValues in 012"); // changement
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  if (channelModeStatus & 0x01 << channel) { // current
    tft.setCursor(x0 + 28, y0 + 5 + 30 * channel);
    tft.print(channelMinCurrent[channel]);
    tft.print(" uA");
    tft.setCursor(x0 + 134, y0 + 5 + 30 * channel);
    tft.print(channelMaxCurrent[channel]);
    tft.print(" uA");
  } else { // voltage
    tft.setCursor(x0 + 28, y0 + 5 + 30 * channel);
    tft.print((float)channelMinVoltage[channel] / 1000, 2);
    tft.print(" V");
    tft.setCursor(x0 + 134, y0 + 5 + 30 * channel);
    tft.print((float)channelMaxVoltage[channel] / 1000, 2);
    tft.print(" V");
  }
}

void limitsFooterOK(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct limitsFooterOK in 012"); // changement
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(x0, y0);
  tft.print("Select to modify");
}

void limitsFooterKO(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct limitsFooterKO in 012"); // changement
  tft.setTextColor(ILI9341_RED);
  tft.setTextSize(2);
  tft.setCursor(x0, y0);
  tft.print("Bad value entered..");
}

void limitsDisplayZone(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct limitsDisplayZone in 012"); // changement
  tft.drawRoundRect(LIMITSDISPLAYZONE_X + x0, LIMITSDISPLAYZONE_Y + y0, LIMITSDISPLAYZONE_W, LIMITSDISPLAYZONE_H, RADIUS, ILI9341_BLACK);
  tft.drawRoundRect(LIMITSDISPLAYZONE_X + x0, LIMITSDISPLAYZONE_Y + y0, LIMITSDISPLAYZONE_W, LIMITSDISPLAYZONE_H, RADIUS, ILI9341_WHITE);
}


/*
   Displays the bottom buttons
   argument(s): none
   returns: nothing
*/

void limitsDisplayBottomButtons() {
  Serial.println("exec fct limitsDisplayBottomButtons in 012"); // changement
  limitsDefaultButton(0, 290);
  //limitsKeyboardButton(120, 290);
  limitsOkButton(180, 290);
}

void limitsKeyboardButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct limitsKeyboardButton in 012"); // changement
  tft.fillRoundRect(BOTTOMBUTTON_X + x0, BOTTOMBUTTON_Y + y0, BOTTOMBUTTON_W, BOTTOMBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(3 + x0, 8 + y0);
  tft.print("Keyb");
}

void limitsOkButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct limitsOkButton in 012"); // changement
  tft.fillRoundRect(BOTTOMBUTTON_X + x0, BOTTOMBUTTON_Y + y0, BOTTOMBUTTON_W, BOTTOMBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(3 + x0, 8 + y0);
  tft.print(" OK");
}

void limitsDefaultButton(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct limitsDefaultButton in 012"); // changement
  tft.fillRoundRect(LARGEBOTTOMBUTTON_X + x0, LARGEBOTTOMBUTTON_Y + y0, LARGEBOTTOMBUTTON_W, LARGEBOTTOMBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(3 + x0, 8 + y0);
  tft.print("Def. val");
}
