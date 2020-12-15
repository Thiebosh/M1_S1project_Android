/*
  set type of the corresponding IV board
  arguments: channel 0.. 7
             type 0: volt 1: current
             WARNING: arguments limits not tested, assumed correct
  returns: nothing
*/
void setChannelType(uint8_t channel, uint8_t type) {
  switch (channel) {
    
    case 0:
      IV00.setType(type);
      break;
    case 1:
      IV01.setType(type);
      break;
    case 2:
      IV02.setType(type);
      break;
    case 3:
      IV03.setType(type);
      break;
    case 4:
      IV04.setType(type);
      break;
    case 5:
      IV05.setType(type);
      break;
    case 6:
      IV06.setType(type);
      break;
    case 7:
      IV07.setType(type);
      break;
  }
  channelDisplayValue[channel]=0;
  
} // end setChannelType()

/*
  get type of the corresponding IV board
  arguments: channel 0.. 7

             WARNING: arguments limits not tested, assumed correct
  returns: type 0: volt 1: current
*/
uint8_t getChannelType(uint8_t channel) {
  uint8_t type;
  switch (channel) {

    case 0:
      type = IV00.getType();
      break;
    case 1:
      type = IV01.getType();
      break;
    case 2:
      type = IV02.getType();
      break;
    case 3:
      type = IV03.getType();
      break;
    case 4:
      type = IV04.getType();
      break;
    case 5:
      type = IV05.getType();
      break;
    case 6:
      type = IV06.getType();
      break;
    case 7:
      type = IV07.getType();
      break;
  }
  return type;
} // end getChannelType()
/*
   write value to the corresponding DAC
   arguments: channel 0.. 7
              value in mV between -8000 and 8000
              WARNING: arguments limits not tested, assumed correct
   returns: nothing
*/
void setChannelValue(uint8_t channel, int16_t value) {
  switch (channel) {
    case 0:
      IV00.setValue(value);
      break;
    case 1:
      IV01.setValue(value);
      break;
    case 2:
      IV02.setValue(value);
      break;
    case 3:
      IV03.setValue(value);
      break;
    case 4:
      IV04.setValue(value);
      break;
    case 5:
      IV05.setValue(value);
      break;
    case 6:
      IV06.setValue(value);
      break;
    case 7:
      IV07.setValue(value);
      break;
  }
  channelValue[channel] = value;
} // end setChannelValue(uint8_t channel, int value)

/*
   get value from the corresponding DAC
   arguments: channel 0.. 7

              WARNING: arguments limits not tested, assumed correct
   returns: value in mV between -800 and 8000
*/
int16_t getChannelValue(uint8_t channel) {
  int16_t value;
  switch (channel) {
    case 0:
      value = IV00.getValue();
      break;
    case 1:
      value = IV01.getValue();
      break;
    case 2:
      value = IV02.getValue();
      break;
    case 3:
      value = IV03.getValue();
      break;
    case 4:
      value = IV04.getValue();
      break;
    case 5:
      value = IV05.getValue();
      break;
    case 6:
      value = IV06.getValue();
      break;
    case 7:
      value = IV07.getValue();
      break;
  }
  return value;
} // end getChannelValue(uint8_t channel)

/*
  set range of the corresponding IV board
  arguments: channel 0.. 7
             range 0..3
             WARNING: arguments limits not tested, assumed correct
  returns: nothing
*/
void setChannelRange(uint8_t channel, uint8_t range) {
  switch (channel) {

    case 0:
      IV00.setRange(range);
      break;
    case 1:
      IV01.setRange(range);
      break;
    case 2:
      IV02.setRange(range);
      break;
    case 3:
      IV03.setRange(range);
      break;
    case 4:
      IV04.setRange(range);
      break;
    case 5:
      IV05.setRange(range);
      break;
    case 6:
      IV06.setRange(range);
      break;
    case 7:
      IV07.setRange(range);
      break;
  }
  channelCurrentRange[channel] = range;
} // end setChannelRange(uint8_t channel, uint8_t range)

/*
  get range of the corresponding IV board
  arguments: channel 0.. 7

             WARNING: arguments limits not tested, assumed correct
  returns: range 0..3
*/
uint8_t getChannelRange(uint8_t channel) {
  uint8_t range;
  switch (channel) {

    case 0:
      range = IV00.getRange();
      break;
    case 1:
      range = IV01.getRange();
      break;
    case 2:
      range = IV02.getRange();
      break;
    case 3:
      range = IV03.getRange();
      break;
    case 4:
      range = IV04.getRange();
      break;
    case 5:
      range = IV05.getRange();
      break;
    case 6:
      range = IV06.getRange();
      break;
    case 7:
      range = IV07.getRange();
      break;
  }
  return range;
} // end getChannelRange(uint8_t channel)

/*
   switch the corresponding channel ON
   arguments: channel 0.. 7
              WARNING: arguments limits not tested, assumed correct
   returns: nothing
*/

void switchChannelOn(uint8_t channel) {
  switch (channel) {
    case 0:
      IV00.sourceOn();
      break;
    case 1:
      IV01.sourceOn();
      break;
    case 2:
      IV02.sourceOn();
      break;
    case 3:
      IV03.sourceOn();
      break;
    case 4:
      IV04.sourceOn();
      break;
    case 5:
      IV05.sourceOn();
      break;
    case 6:
      IV06.sourceOn();
      break;
    case 7:
      IV07.sourceOn();
      break;
  }
} // end switchChannelOn(uint8_t channel)


/*
   switch the corresponding IVsource OFF
   arguments: channel 0.. 7
              WARNING: arguments limits not tested, assumed correct
   returns: nothing
*/
void switchChannelOff(uint8_t channel) {
  switch (channel) {
    case 0:
      IV00.sourceOff();
      break;
    case 1:
      IV01.sourceOff();
      break;
    case 2:
      IV02.sourceOff();
      break;
    case 3:
      IV03.sourceOff();
      break;
    case 4:
      IV04.sourceOff();
      break;
    case 5:
      IV05.sourceOff();
      break;
    case 6:
      IV06.sourceOff();
      break;
    case 7:
      IV07.sourceOff();
      break;
  }
} // end switchChannelOff(uint8_t channel)
