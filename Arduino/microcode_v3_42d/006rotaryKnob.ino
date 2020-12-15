/*
   updates the displayed value for selected channel with respect to rotary knob
   argument(s): channel = 0..7 , other values ignored
   returns: 0 = OK; 1= channel out of range
*/
uint8_t updateDisplayedValueWithRotaryKnob(uint8_t channel) {
  float requestedI;

  if ((channel < 0) || (channel > 7)) return 1;
  //  Serial.print("channelModeStatus= ");
  //  Serial.println(channelModeStatus, BIN);
  if ((channelModeStatus >> channel) & 0x01) { // current mode, unit can be mA or uA depending on range

    channelDisplayValue[channel] += codeWheelIncrement * pow(10, digitSelected[channel]);
    //    Serial.print("incremented channelDisplayValue[channel]= ");
    //    Serial.println(channelDisplayValue[channel], 3);
    // converts in uA
    if (channelCurrentRange[channel] == 3) requestedI = channelDisplayValue[channel] * 1000;
    else requestedI = channelDisplayValue[channel];
    //    Serial.print("requestedI= ");
    //    Serial.println(requestedI, 3);
    if (requestedI > (float)channelMaxCurrent[channel]) requestedI = (float)channelMaxCurrent[channel];
    if (requestedI < (float)channelMinCurrent[channel]) requestedI = (float)channelMinCurrent[channel];
    //    Serial.print("constrained requestedI= ");
    //    Serial.println(requestedI, 3);
    //    Serial.print("input channelCurrentRange[channel]= ");
    //    Serial.println(channelCurrentRange[channel]);
    if (abs(requestedI) <= HIGH_COMPLIANCE_MAX_CURRENT / 1000) {
      channelCurrentRange[channel] = 0;
      channelDisplayValue[channel] = requestedI;
    } else     if (abs(requestedI) <= HIGH_COMPLIANCE_MAX_CURRENT / 100) {
      channelCurrentRange[channel] = 1;
      //     channelDisplayValue[channel] = floor(requestedI * 100) / 100;
      //channelDisplayValue[channel] = (float)( (int)(requestedI * 100) / 100);
      channelDisplayValue[channel] = requestedI;
    } else     if (abs(requestedI) <= HIGH_COMPLIANCE_MAX_CURRENT / 10) {
      channelCurrentRange[channel] = 2;
      //     channelDisplayValue[channel] = floor(requestedI * 10) / 10;
      channelDisplayValue[channel] = requestedI;
      //channelDisplayValue[channel] = (float)( (int)(requestedI * 10) / 10);
      //      Serial.print("requestedI * 10= ");
      //      Serial.println(requestedI * 10,3);
      //      Serial.print("floor(requestedI * 10)= ");
      //      Serial.println(floor(requestedI * 10),3);
    } else  {
      channelCurrentRange[channel] = 3;
      channelDisplayValue[channel] = floor(requestedI) / 1000;
    }
    //    Serial.print("output channelCurrentRange[channel]= ");
    //    Serial.println(channelCurrentRange[channel]);
    //    Serial.print("output channelDisplayValue[channel]= ");
    //    Serial.println(channelDisplayValue[channel]);


  } else {  // voltage mode, unit is always volt

    channelDisplayValue[channel] += codeWheelIncrement * pow(10, digitSelected[channel]);
 
    if ((int)(channelDisplayValue[channel] * 1000) > channelMaxVoltage[channel]) channelDisplayValue[channel] = (float)channelMaxVoltage[channel] / 1000;
    if ((int)(channelDisplayValue[channel] * 1000) < channelMinVoltage[channel]) channelDisplayValue[channel] = (float)channelMinVoltage[channel] / 1000;

  }

  return 0;
}

/*
   determines which channel is selected for value modification
   argument(s): none
   returns: channel number or 255 if no channel selected
*/
uint8_t whichChannelSelectedForValueModification() {
  for (uint8_t i = 0; i < 8; i++) {
    if (channelSelectedStatus & 0x01 << i) return i;
  }
  return 255;
}
