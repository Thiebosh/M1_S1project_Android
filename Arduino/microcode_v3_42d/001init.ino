/*
   Inits the whole system
   arguments: none
   returns: 0 = OK,   1= no DAC responding
*/
uint8_t initAll(void) {
  Serial.println("exec fct initAll in 001"); // changement
  tft.begin();
  ts.begin();
  //  Serial.begin(9600);
  Serial.setTimeout(SERIAL_TIMEOUT);
  Wire.begin();
  SPI.begin();

  /*
     STEP1: display microcode version
  */

  tft.fillScreen(ILI9341_BLACK);
  tft.setTextColor(ILI9341_BLUE);
  tft.setTextSize(1);
  tft.setCursor(10, 0);
  tft.print("** Microcode version ");
  tft.print(code_version);
  tft.println(" **");

  /*
     STEP2:  configure IO lines for encoder, switches, onslot signals
  */

  pinMode(FACTORY_DEFAULT_SWITCH, INPUT_PULLUP);
  pinMode(ONSLOT_0, INPUT_PULLUP);
  pinMode(ONSLOT_1, INPUT_PULLUP);
  pinMode(ONSLOT_2, INPUT_PULLUP);
  pinMode(ONSLOT_3, INPUT_PULLUP);
  pinMode(ONSLOT_4, INPUT_PULLUP);
  pinMode(ONSLOT_5, INPUT_PULLUP);
  pinMode(ONSLOT_6, INPUT_PULLUP);
  pinMode(ONSLOT_7, INPUT_PULLUP);





  /*
    STEP3: test FRAM memory
  */
  framMemoryOk = 0;
  tft.println("");
  framMemoryOk = testFramMemory(FRAM_ADDRESS );
  Serial.println(framMemoryOk); // changement : ligne inexiz
  fram.softPageLock(PAGE0); // helps preventing parameters erase
  fram.softPageLock(PAGE1); // helps preventing parameters erase

  /*
     STEP4:  test presence of DACs
  */
  
  testDac(); // block below commented for test purposes

  /*
     tft.println("");
     if  (testDac() > 2) {
       tft.setTextColor(ILI9341_RED);
       tft.setTextSize(2);
       tft.println("");
       tft.print("** ABORT **");
       return 1;  // exit with error code
     }
  */


  /*
     STEP5: detects boards connected on slot
  */
  tft.println("");
  boardOnSlotStatus = detectBoardOnSlot();

  /*
     STEP6: // test boards connected
  */

  ivSourceDetect();



  /*
     STEP7: boot
  */

  tft.println("");
  if (digitalRead(FACTORY_DEFAULT_SWITCH)) {
    tft.print("** Boot (NOT FACTORY)...");
    Serial.println("avant dafaultsRestore if"); // changement ligne inexistante, ici marche
    defaultsRestore(NOT_FACTORY);
    Serial.println("apres dafaultsRestore if"); // changement ligne inexistante, ici ne marche pas
    systemBoot();
    tft.println("done");
  }
  else {
    tft.print("** Restoring factory defaults...");
    defaultsRestore(FACTORY);
    tft.println("done.");
    tft.print("** Resuming boot...");
    systemBoot();
    tft.println("done");
  }

  /*
     STEP8: inits touchscreen limits
  */

  ts_minx = TS_MINX;
  ts_miny = TS_MINY;
  ts_maxx = TS_MAXX;
  ts_maxy = TS_MAXY;


  /*
     STEP9: locks calibration features, unlock TS
  */
  calibrationLocked = 1;
  touchscreenLocked = 0; // changement : touchscreenLocked = 0; SY:TS:LOCK

  /*
    STEP10: terminates
  */

  return 0; // everything is OK

} // end initAll()
