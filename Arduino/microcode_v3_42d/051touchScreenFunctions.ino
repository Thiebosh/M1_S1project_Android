/* determines if x is inside the a,b reange, bounds included
   arguments: x value to test, a,b bounds, all uint16_t
   returns: 0 = outside bounds, 1= inside bounds
*/
uint8_t isInside(uint16_t x, uint16_t a, uint16_t b) {
  if ((x >= a) and (x <= b)) return 1;
  else return 0;
} // end isInside()

/* determines if digitSelected is'nt beyhond limits
   arguments: channel number
   returns: nothing,  modifies the global variable digitSelected if necessary
*/
void checkDigitSelectedLimits(uint8_t channel) {

  if (!(channelModeStatus & 0x01 << channel)) { // voltage mode
    if (digitSelected[channel] > 0) digitSelected[channel] = 0;
    if (digitSelected[channel] < -3) digitSelected[channel] = -3;
  } else { // current mode, limits depend on range
    switch ( channelCurrentRange[channel]) {
      case 0:
        if (digitSelected[channel] > 0) digitSelected[channel] = 0;
        if (digitSelected[channel] < -3) digitSelected[channel] = -3;
        break;
      case 1:
        if (digitSelected[channel] > 1) digitSelected[channel] = 1;
        if (digitSelected[channel] < -2) digitSelected[channel] = -2;
        break;
      case 2:
        if (digitSelected[channel] > 2) digitSelected[channel] = 2;
        if (digitSelected[channel] < -1) digitSelected[channel] = -1;
        break;
      case 3:
        if (digitSelected[channel] > 0) digitSelected[channel] = 0;
        if (digitSelected[channel] < -3) digitSelected[channel] = -3;
        break;
    } // end case

  } // end if/else

}// end checkDigitSelectedLimits(uint8_t channel)

/* resets the ts points acquired . Necessary when changing the menu inside getInputFromTouchScreen()
   arguments: none
   returns: nothing,  modifies x,y,p.x,p.y
*/
void resetTsInput() {
  p.x = 500; // outside screen
  p.y = 500;
}
