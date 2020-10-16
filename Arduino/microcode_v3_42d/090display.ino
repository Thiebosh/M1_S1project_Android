
/*
  from displayed value and unit, calculates DAC value and range if applicable
  sourceType =0 -> voltage, =1 -> current
  return: nothing
  changes global variables calculatedValue and calculatedRange
*/
/*
void extractFromDisplayedValue(float value, uint8_t unit, uint8_t sourceType) {

  float absval;

  if (unit == 0) {// uA or mV
    if (!sourceType) calculatedValue = (int)value; //voltage
    else { // current
      absval = abs(value);
      if ( absval <= 8) {
        calculatedValue = (int)(value * 1000);
        calculatedRange = 0;
      } else if ( absval <= 80) {
        calculatedValue = (int)(value * 100);
        calculatedRange = 1;
      } else if ( absval <= 800) {
        calculatedValue = (int)(value * 10);
        calculatedRange = 2;
      } else {
        calculatedValue = (int)(value);
        calculatedRange = 3;
      }
    }
  }
  if (unit == 1) { // mA or V
    if (!sourceType) calculatedValue = (int)(value * 1000); //voltage
    else { // current
      absval = abs(value * 1000);
      if ( absval <= 8) {
        calculatedValue = (int)(value * 1000000);
        calculatedRange = 0;
      } else if ( absval <= 80) {
        calculatedValue = (int)(value * 100000);
        calculatedRange = 1;
      } else if ( absval <= 800) {
        calculatedValue = (int)(value * 10000);
        calculatedRange = 2;
      } else {
        calculatedValue = (int)(value * 1000);
        calculatedRange = 3;
      }
    }
  }
  // here, calculatedValue and calculatedRange are updated
} // end extractFromDisplayedValue()

*/

/*
  from DAC value and range if applicable, calculates  displayed value and unit
  return: nothing
  changes global variable channelDisplayValue
*/

void extractDisplayValueFromDACValue(uint8_t channel) {
  Serial.println("exec fct extractDisplayValueFromDACValue in 090"); // changement

  if (channelModeStatus & 0x01 << channel ) {

    switch (channelCurrentRange[channel]) {
      case 0:
        channelDisplayValue[channel] = (float)channelValue[channel] / 1000;
        break;
      case 1:
        channelDisplayValue[channel] = (float)channelValue[channel] / 100;
        break;
      case 2:
        channelDisplayValue[channel] = (float)channelValue[channel] / 10;
        break;
      case 3:
        channelDisplayValue[channel] = (float)channelValue[channel] / 1000;
        break;

    }

  }  else { // voltage
    channelDisplayValue[channel] = (float)channelValue[channel] / 1000;
  }


}// end extractDisplayValueFromDACValue(uint8_t channel)
