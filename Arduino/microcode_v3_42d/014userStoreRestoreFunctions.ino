/*
   Store user confg for all channels
   argument(s) : user number
   returns: nothing
*/
void storeUserCongig(uint8_t user) {
  uint16_t baseAddress = (FRAM_ADDRESS << 8) + STORE0_BASE_ADDRESS + 0x80 * user;

  fram.writeByte(baseAddress, channelModeStatus);
  baseAddress += 1;

  for (uint8_t i = 0; i < 8; i++) {
    fram.writeInt(baseAddress + 2 * i, channelValue[i]);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {
    fram.writeByte(baseAddress +  i, channelCurrentRange[i]);
  }
  baseAddress += 8;

  for (uint8_t i = 0; i < 8; i++) {
    fram.writeInt(baseAddress + 2 * i, channelMaxVoltage[i]);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {
    fram.writeInt(baseAddress + 2 * i, channelMinVoltage[i]);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {
    fram.writeInt(baseAddress + 2 * i, channelMaxCurrent[i]);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {
    fram.writeInt(baseAddress + 2 * i, channelMinCurrent[i]);
  }
  baseAddress += 16;

  userConfigStored |= 0x01 << user;
  fram.writeByte((FRAM_ADDRESS << 8) + USER_CONFIG_BASE_ADDRESS, userConfigStored); // updates userCongigStored copy in FRAM
}// end storeUserCongig(uint8_t user)



/*
   Recall user confg for all channels
   argument(s) : user number
   returns: nothing
*/
void recallUserCongig(uint8_t user) {
  uint16_t baseAddress = (FRAM_ADDRESS << 8) + STORE0_BASE_ADDRESS + 0x80 * user;

  for (uint8_t i = 0; i < 8; i++) switchChannelOff(i);  // all channels off
  channelActiveStatus = 0;
  isChannelLabelChanged = 0xFF;

  channelModeStatus = fram.readByte(baseAddress); // updates channel mode
  baseAddress += 1;
  for (uint8_t i = 0; i < 8; i++) {

    if (boardOnSlotStatus & 0x01 << i)
      setChannelType(i, channelModeStatus & 0x01 << i);
  }

  for (uint8_t i = 0; i < 8; i++) { // updates ChannelValue[i] but NOT DAC values
    channelValue[i] = fram.readInt(baseAddress + 2 * i);
    setChannelValue(i, channelValue[i]);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {// updates range
    setChannelRange(i, fram.readByte(baseAddress +  i));
  }
  baseAddress += 8;


  for (uint8_t i = 0; i < 8; i++) extractDisplayValueFromDACValue(i);
  isChannelValueChanged = 0xFF;

  for (uint8_t i = 0; i < 8; i++) {
    channelMaxVoltage[i] = fram.readInt(baseAddress + 2 * i);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {
    channelMinVoltage[i] = fram.readInt(baseAddress + 2 * i);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {
    channelMaxCurrent[i] = fram.readInt(baseAddress + 2 * i);
  }
  baseAddress += 16;

  for (uint8_t i = 0; i < 8; i++) {
    channelMinCurrent[i] = fram.readInt(baseAddress + 2 * i);
  }
  baseAddress += 16;

}// end recallUserCongig(uint8_t user)
