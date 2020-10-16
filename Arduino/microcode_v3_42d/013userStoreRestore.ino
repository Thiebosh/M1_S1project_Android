#define USERSTORERECALLBUTTON_X 0
#define USERSTORERECALLBUTTON_Y 0
#define USERSTORERECALLBUTTON_W 240
#define USERSTORERECALLBUTTON_H 30 // espace 5

/*
   Display user store context
   argument(s): none
   returns: nothing
*/
void userStoreMenuDisplay() {
  Serial.println("exec fct userStoreMenuDisplay in 013"); // changement
  tft.fillScreen(ILI9341_BLACK);

  for (int i = 0; i < 8; i++) displayUserStoreMenuLine(0, 5 + 35 * i, i);
  displayUserStoreRecallMenuAbort(0, 5 + 35 * 8);
}// end userStoreMenuDisplay()


/*
   Display user recall context
   argument(s): none
   returns: nothing
*/
void userRecallMenuDisplay() {
  Serial.println("exec fct userRecallMenuDisplay in 013"); // changement
  tft.fillScreen(ILI9341_BLACK);

  for (int i = 0; i < 8; i++) displayUserRecallMenuLine(0, 5 + 35 * i, i);
  displayUserStoreRecallMenuAbort(0, 5 + 35 * 8);
}// end userRecallMenuDisplay()


void displayUserStoreMenuLine(uint16_t x0, uint16_t y0, uint8_t user) {
  Serial.println("exec fct displayUserStoreMenuLine in 013"); // changement
  if (userConfigStored & 0x01 << user) {
    tft.fillRoundRect(USERSTORERECALLBUTTON_X + x0, USERSTORERECALLBUTTON_Y + y0, USERSTORERECALLBUTTON_W, USERSTORERECALLBUTTON_H, RADIUS, ILI9341_BLUE);
  } else {
    tft.drawRoundRect(USERSTORERECALLBUTTON_X + x0, USERSTORERECALLBUTTON_Y + y0, USERSTORERECALLBUTTON_W, USERSTORERECALLBUTTON_H, RADIUS, ILI9341_BLUE);
  }

  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 8 + y0);
  tft.print("Store ");
  tft.print(user);
}

void displayUserStoreRecallMenuAbort(uint16_t x0, uint16_t y0) {
  Serial.println("exec fct displayUserStoreRecallMenuAbort in 013"); // changement
  tft.fillRoundRect(USERSTORERECALLBUTTON_X + x0, USERSTORERECALLBUTTON_Y + y0, USERSTORERECALLBUTTON_W, USERSTORERECALLBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 8 + y0);
  tft.print("Abort");
}

void displayUserRecallMenuLine(uint16_t x0, uint16_t y0, uint8_t user) {
  Serial.println("exec fct displayUserRecallMenuLine in 013"); // changement
  if (userConfigStored & 0x01 << user) {
    tft.fillRoundRect(USERSTORERECALLBUTTON_X + x0, USERSTORERECALLBUTTON_Y + y0, USERSTORERECALLBUTTON_W, USERSTORERECALLBUTTON_H, RADIUS, ILI9341_BLUE);
  } else {
    tft.drawRoundRect(USERSTORERECALLBUTTON_X + x0, USERSTORERECALLBUTTON_Y + y0, USERSTORERECALLBUTTON_W, USERSTORERECALLBUTTON_H, RADIUS, ILI9341_BLUE);
  }

  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 8 + y0);
  tft.print("Recall ");
  tft.print(user);
}
