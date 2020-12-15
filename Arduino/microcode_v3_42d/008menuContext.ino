#define MENUBUTTON_X 0
#define MENUBUTTON_Y 0
#define MENUBUTTON_W 240
#define MENUBUTTON_H 34


/*
   Display menu context
   argument(s): none
   returns: nothing
*/
void menuDisplayContext() {
  tft.fillScreen(ILI9341_BLACK);

  menuSourceTypeButton(0, 10);
  menuModifyLimitsButton(0, 50);
  menuUserStoreButton(0, 90);
  menuUserRecallButton(0, 130);
  menuAboutButton(0, 170);
  menuBackButton(0, 210);
}

/*
   buttons definition
   arguments: x0,y0: position of the upper left corner in the screen
   returns: nothing
*/
void menuSourceTypeButton(uint16_t x0, uint16_t y0) {
  tft.fillRoundRect(MENUBUTTON_X + x0, MENUBUTTON_Y + y0, MENUBUTTON_W, MENUBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 10 + y0);
  tft.print("Change Source Type");
}

void menuModifyLimitsButton(uint16_t x0, uint16_t y0) {
  tft.fillRoundRect(MENUBUTTON_X + x0, MENUBUTTON_Y + y0, MENUBUTTON_W, MENUBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 10 + y0);
  tft.print("Modify I/V Limits");
}

void menuUserStoreButton(uint16_t x0, uint16_t y0) {
  tft.fillRoundRect(MENUBUTTON_X + x0, MENUBUTTON_Y + y0, MENUBUTTON_W, MENUBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 10 + y0);
  tft.print("User Config Store");
}

void menuUserRecallButton(uint16_t x0, uint16_t y0) {
  tft.fillRoundRect(MENUBUTTON_X + x0, MENUBUTTON_Y + y0, MENUBUTTON_W, MENUBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 10 + y0);
  tft.print("User Config Recall");
}

void menuAboutButton(uint16_t x0, uint16_t y0) {
  tft.fillRoundRect(MENUBUTTON_X + x0, MENUBUTTON_Y + y0, MENUBUTTON_W, MENUBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 10 + y0);
  tft.print("About");
}

void menuBackButton(uint16_t x0, uint16_t y0) {
  tft.fillRoundRect(MENUBUTTON_X + x0, MENUBUTTON_Y + y0, MENUBUTTON_W, MENUBUTTON_H, RADIUS, ILI9341_BLUE);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 + x0, 10 + y0);
  tft.print("Back");
}

/*
   Display menu about
   argument(s): none
   returns: nothing
*/
void menuDisplayAbout() {

  tft.fillScreen(ILI9341_BLACK);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.setCursor(10 , ABOUT_FIRST_LINE);
  tft.print("Modular I/V source");
  tft.setCursor(10 , ABOUT_FIRST_LINE+20);
  tft.print("Firmware V");
  tft.println(code_version);
  tft.setCursor(10 ,ABOUT_FIRST_LINE+40);
  tft.println("YNCREA-ISEN");
  tft.setCursor(10 , ABOUT_FIRST_LINE+60);
  tft.print("BRS - ");
  tft.println(version_date);
  delay(5000);
  menuDisplayContext();
}
