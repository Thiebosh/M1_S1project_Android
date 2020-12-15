# define MIN_DELAY_BETWEEN_EDGES 200  // in milliseconds


/*
   Handles action on ALLON switch
*/
void allOnHandler() {
  allOnPressed = 1;
}

/*
   Handles action on ALLOFF switch
*/
void allOffHandler() {
  allOffPressed = 1;
}

/*
   Handles action on rotary knob,
   increments/decrements global variable codeWheelIncrement
*/
void codeWheelHandlerA() {
  if ((millis() - timestampA) > MIN_DELAY_BETWEEN_EDGES) {
    if (digitalRead(ENCODER_B)) codeWheelIncrement = 1;  // B=1
    else codeWheelIncrement = -1;
  }
  timestampA = millis();
}
/*
  void codeWheelHandlerA() {
  //  noInterrupts();
  codewheelB = 0;
  for (int i = 0; i < CODEWHELL_DEBOUNCE; i++) codewheelB += digitalRead(ENCODER_B);

  codewheelB = codewheelB * 2 / CODEWHELL_DEBOUNCE;

  if (codewheelB != 0) codeWheelIncrement = 1;  // B=1
  else codeWheelIncrement = -1;
  delayMicroseconds(10000);

  //  interrupts();
  }
*/

/*

  void codeWheelHandlerA() {
  if (!codeWheelIncrement) semaphore = 1;
  }
*/
/*
  void codeWheelHandlerB() {
  encoderAcurrent=digitalRead(ENCODER_A);

  if ((encoderAlast == LOW) && (encoderAcurrent == HIGH)){
  if (digitalRead(ENCODER_B) == HIGH) codeWheelIncrement = 1;
  else codeWheelIncrement = -1;
  }
  uint32_t i=0;
  while(i <10000)i++;
  }
*/
/*
  void codeWheelHandlerB() {
  if (semaphore)  codeWheelIncrement = 1;
  else codeWheelIncrement = -1;
  }
*/



/*
   Handles action on code wheel switch
*/
void codeWheelHandlerSW() {
  codeWheelSwPressed = 1;
}


/*
   enables all existing channels upon action on ALLON switch
   argument(s): none
   return: nothing
*/

void allOn() {
#ifdef DEBUG_IHM
  Serial.println("ALLON");
#endif
  if (boardOnSlotStatus & 0x01)  switchChannelOn(0);
  if (boardOnSlotStatus & 0x02)  switchChannelOn(1);
  if (boardOnSlotStatus & 0x04)  switchChannelOn(2);
  if (boardOnSlotStatus & 0x08)  switchChannelOn(3);
  if (boardOnSlotStatus & 0x10)  switchChannelOn(4);
  if (boardOnSlotStatus & 0x20)  switchChannelOn(5);
  if (boardOnSlotStatus & 0x40)  switchChannelOn(6);
  if (boardOnSlotStatus & 0x80)  switchChannelOn(7);

}


/*
   disables all existing channels upon action on ALLOFF switch
   argument(s): none
   return: nothing
*/
void allOff() {
#ifdef DEBUG_IHM
  Serial.println("ALLOFF");
#endif
  if (boardOnSlotStatus & 0x01)  switchChannelOff(0);
  if (boardOnSlotStatus & 0x02)  switchChannelOff(1);
  if (boardOnSlotStatus & 0x04)  switchChannelOff(2);
  if (boardOnSlotStatus & 0x08)  switchChannelOff(3);
  if (boardOnSlotStatus & 0x10)  switchChannelOff(4);
  if (boardOnSlotStatus & 0x20)  switchChannelOff(5);
  if (boardOnSlotStatus & 0x40)  switchChannelOff(6);
  if (boardOnSlotStatus & 0x80)  switchChannelOff(7);
}
