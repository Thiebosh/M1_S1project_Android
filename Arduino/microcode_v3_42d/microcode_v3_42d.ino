/*
  Les modifications sont commentées par = changement : ce qu'il y avait à l'origine
  Title: microcode V3.x
  Author: BRS 04/2020
  Summary:
  V2.0: implements hardware boot tests and configuration
  V2.1: IVsource library + data inputs through console (TEST keyword)
  V2.2: added FRAM operations to TEST keyword,
        source trimming parameters in FRAM,
        IVsource library enhanced to version 2 to address operations with FRAM memory,
        added SYSTEM:CAL keyword
        updated pin location with respect to CPU_board & IHM_board
        Test FRAM before DACs
  V2.3: add support for ALLON,  ALLOFF and rotary switch  buttons
        add support for code wheel
        changed userCongigStored to  userConfigStored
        changed channelSelecteStatus to channelSelectedStatus
        changed  channelSelectedStatus meaning
        added delay before initAll() call
        created ivSourceDetect() in step 6
        test modified in ivSourceDetect(): else if (boardType[i] ==  IVSOURCE_JUMPERS_READ) changed to else if (boardType[i] & IVSOURCE_JUMPERS_READ) {
        removed defaultsRestore(NOT_FACTORY) from systemoot()
        modified init step 7
        modified testDac() function: SDO pin disabled and enabled on demand  to allow more than one DAc on the same bus (SDO is NOT highZ when DAC is not selected)
        reset of corresponding bit in boardOnSlotStatus if read error or unknown board detected during boot
  v3.0: starts main channel display context 3 seconds after boot
        main context definition
        All on / all off buttons updates TFT
        on/off and V/I selection through console updates TFT
        value and range updates TFT
        channel and digit selection for value modificatrion
        menu -> change source type
        menu -> about
        value change using knob
        !!! bug on selected digit when decreasing range
  v3.1: !!! bug on selected digit when decreasing range not yet fixed
        !!! rotary encoder management to improve
        keyboard entry added for channel values
        ALLON/ALLOFF buttons modified: must be pressed at least 2 seconds for activation
  V3.2: !!! bug on selected digit when decreasing range not yet fixed
        !!! rotary encoder management to improve
        added imit values modification menu
        HIGH compliance mode activated by default, not mofifiable
        Consistency of min/max bouindaries entered
        fixed display problem with keyboard
  V3.3: store user context
  V3.31: recall user context
  V3.32: console input SYS->STORE, SYS->RECALL
        suppressed ALLON/ALLOFF activation min duration
  V3.4: CH console input 
        CH:x:ON, CH:x:OFF, CH:x:V:value, CH:x:I:value
  V3.41: CH:x:?, CH:x:UIL,CH:x:LIL,CH:x:UVL,CH:x:LVL, CH:x:DEFL
        fixed bug on selected digit when decreasing range 
  V3.42:  lock/unlock touchscreen via console input
  V3.42b:  Changed DEF_ABSOLUTE_Mxx_VALUE to HIGH_COMPLIANCE_Mxx_CURRENT in current source calibration procedure
            added Serial.setTimeout(60000) for 60 second timeout 
  V3.42c: corrected bug: default current limits values in change limits menu
        corrected bug: keyboard return with back resets value in change limits menu  
  v3.42d: added Abort button in user store and recall menu
        corrected bug: possible to recall a nonexisting stored config in change user recall  menu     
*/

/*
// debug flags changement : block ci dessous commenté
#define DEBUG_ROTARY
#define DEBUG_SWROT
#define DEBUG_ONOFF // changement : ligne commentée
#define DEBUG_DACTEST
#define DEBUG_TS
#define DEBUG_NVIC
//#define KBD_DEBUG
*/

//Code version to be updated accordingly
char* code_version = "3.42d";
char* version_date = "28 may 2020";

#include <Adafruit_GFX.h>    // Core graphics library
#include "Adafruit_ILI9341.h" // Hardware-specific library
#include <Adafruit_STMPE610.h>
#include <Wire.h>
#include <SPI.h>
#include <math.h>
#include <FM24CL16B.h>
#include <IVsource_v2.h>
#include <AD5764R.h>

#include <ArduinoJson.h>

/*hardware chip select pins*/
#define TFT_DC 9
#define TFT_CS 10
#define RT_CS 8

#define _CS_DAC0 40
#define _LDAC0 43
#define _CS_DAC1 41
#define _LDAC1 42

/*
   HMI interface
*/
#define SW_ALLON 52
#define SW_ALLOFF 53
#define ENCODER_A 50
#define ENCODER_B 49
#define ENCODER_SW 51
#define DEBOUNCE_TIME_MS 200
#define DEBOUNCE_ROT_MS 500
#define ONOFF_DELAY 1000

#define TYPE_V 0
#define TYPE_I 1

#define FRAM_ADDRESS 0x50 // I2C address

#define hc06 Serial1 // changement : define module bluetooth
int intCmd = 0;
int rep = 9;
String strCmd = "";
String str = "";

/* objects definition */
Adafruit_ILI9341 tft = Adafruit_ILI9341(TFT_CS, TFT_DC);  // TFT screen
Adafruit_STMPE610 ts = Adafruit_STMPE610(RT_CS);     // touchpad
FM24CL16B fram; // FRAM non-volatile memory

AD5764R dac0(_CS_DAC0, _LDAC0);
AD5764R dac1(_CS_DAC1, _LDAC1);

IVsource IV00(dac0, fram, 0, FRAM_ADDRESS); // with IVsource_v2 library
IVsource IV01(dac0, fram, 1, FRAM_ADDRESS);
IVsource IV02(dac0, fram, 2, FRAM_ADDRESS);
IVsource IV03(dac0, fram, 3, FRAM_ADDRESS);
IVsource IV04(dac1, fram, 4, FRAM_ADDRESS);
IVsource IV05(dac1, fram, 5, FRAM_ADDRESS);
IVsource IV06(dac1, fram, 6, FRAM_ADDRESS);
IVsource IV07(dac1, fram, 7, FRAM_ADDRESS);

/* FRAM related variables */
uint8_t framMemoryOk; // 0: failed, 1: passed

/* Board related variable */
uint8_t boardOnSlotStatus;  // 1 bit per slot (MSB=slot7... LSB=slot0) bit=1: slot populated, bit=0: slot empty
uint8_t boardType[8]; // if boardType[i] = 0x06, IVsource connected to slot i. Other values ignored
uint8_t ivSourcePosition; //1 bit per slot (MSB=slot7... LSB=slot0) bit=1: slot populated with IVsource, bit=0: other board

/* global variable related to console input */
String subPhrase[15];
uint8_t dummy, temp; // dummy and temporary variables
uint8_t calibrationLocked;
uint8_t touchscreenLocked;

/* channel settings variables */
uint8_t channelModeStatus; // is channel V or I, 1 bit per channel, bit 7..0 = channel 7..0, 0= voltage, 1 = current
uint8_t channelActiveStatus; // is channel active, 1 bit per channel, bit 7..0 = channel 7..0, 0= OFF, 1 = ON
int channelValue[8]; // DAC signed value in mV
uint8_t channelCurrentRange[8]; // 0=+/-8uA, 1=+/-80uA, 2=+/-800uA, 3=+/-8mA,
int channelMaxVoltage[8], channelMinVoltage[8]; // signed value in mV
int channelMaxCurrent[8], channelMinCurrent[8]; // signed value in uA, DAC value depends on range
uint8_t channelMaxRange[8]; // to define the real value of current limit
int absChannelMaxValue, absChannelMinValue; // value in mV, both V and I

/* selection status of display areas*/
uint8_t channelSelectedStatus; // is channel seclected for value modification, 1 bit per channel, bit 7..0 = channel 7..0, 0= not selected, 1 = selected
uint8_t PreviousChannelSelectedStatus; // is channel seclected for value modification, 1 bit per channel, bit 7..0 = channel 7..0, 0= not selected, 1 = selected
int8_t digitSelected[8]; // 0=units, 1=tens, 2=hundreds,  -1=1/10, -2=1/100, -3=1/1000
uint8_t minLimitSelectedStatus; // is channel seclected for min limit modification, 1 bit per channel, bit 7..0 = channel 7..0
uint8_t maxLimitSelectedStatus; // is channel seclected for max limit modification, 1 bit per channel, bit 7..0 = channel 7..0
uint8_t currentSelectedChannel;

/* user config storage */
uint8_t userConfigStored; //is a config stored at this location, 1 bit per location, bit 7..0 = location 7..0, 0= no conf stored, 1 = conf stored

/* display & touchpad variables */
int ts_minx, ts_miny, ts_maxx, ts_maxy;
float channelDisplayValue[8]; // displayed signed value
uint8_t channelDisplayUnit [8]; // 0: uA, 1: V or mA (mV not used for display)
uint8_t tsPressed;
TS_Point p; // raw coordinates of touched point


/* interrupt variables */
volatile uint8_t allOnPressed;
volatile uint8_t allOffPressed;
volatile uint8_t codeWheelSwPressed;
volatile uint8_t codewheelB;
volatile int codeWheelIncrement;
volatile unsigned long timestampA;
volatile uint32_t nvicIABR[2];
volatile  unsigned long onoffStartTime;

/* variables related to contexts */
unsigned int currentContext; // context is a 5 digit number excepted 0. It is read as x x x x x
uint8_t isChannelLabelChanged; //is channel label changed, 1 bit per channel, bit 7..0 = channel 7..0, 0=not changed, 1=changed
uint8_t isChannelValueChanged; //is channel value (or unit) changed, 1 bit per channel, bit 7..0 = channel 7..0, 0=not changed, 1=changed
char valueAsString[10]; // for display purposes only
uint8_t isChannelActiveStatusChanged; // is channel active changed, 1 bit per channel, bit 7..0 = channel 7..0,  0=not changed, 1=changed

/* variables for rotry encoder */
//uint8_t encoderAcurrent=LOW;
//uint8_t encoderAlast = encoderAcurrent;

/* variables for keyboard */
char keyboardButtonVoltLabelList[16][5] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "+/-", "", "mV", "V", "Back"};
char keyboardButtonAmpLabelList[16][5] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "+/-", "", "uA", "mA", "Back"};
uint16_t keyboardXBounds[8], keyboardYBounds[8];
float keyboardValue = 0;  // raw kbd value to be processed with extractFromKeyboardValue()
uint8_t keyboardUnit = 0; // 0: mV or uA, 1: V or mA
int calculatedDacValue = 0; //  value for DAC in mV
uint8_t calculatedRange = 0; // 0..3 in current mode, not applicable in V mode

void setup() {

   
  Serial.begin(9600); // for early debug purposes
  hc06.begin(9600);   // changement : ligne inexistante
  
  /*
     configures interrupts
  */

  pinMode(SW_ALLOFF, INPUT_PULLUP);
  pinMode(SW_ALLON, INPUT_PULLUP);
  pinMode(ENCODER_A, INPUT_PULLUP);
  pinMode(ENCODER_B, INPUT_PULLUP);
  pinMode(ENCODER_SW, INPUT_PULLUP);



#ifdef DEBUG_NVIC
  // lecture ISER
  Serial.print("NVIC->ISER[0]= ");
  Serial.println(NVIC->ISER[0], HEX);
  Serial.print("NVIC->ISER[1]= ");
  Serial.println(NVIC->ISER[1], HEX);
  Serial.println(" ");
#endif

  //  attachInterrupt(digitalPinToInterrupt(ENCODER_B), codeWheelHandlerB, FALLING);
  attachInterrupt(digitalPinToInterrupt(ENCODER_A), codeWheelHandlerA, FALLING);
  attachInterrupt(digitalPinToInterrupt(ENCODER_SW), codeWheelHandlerSW, FALLING);
  attachInterrupt(digitalPinToInterrupt(SW_ALLON), allOnHandler, FALLING);
  attachInterrupt(digitalPinToInterrupt(SW_ALLOFF), allOffHandler, FALLING);


  delay(3000); // necessay to allow correct display at boot
  currentContext = 0;   // boot context
  /*
     init process
  */
  if (initAll()) while (1); // if initAll <> 0 -> abort condition, no further execution
  /*
     resets parasitic interrupts
  */
  allOnPressed = 0; // changement : allOnPressed = 0
  allOffPressed = 0;
  codeWheelIncrement = 0;
  codeWheelSwPressed = 0;
  timestampA = 0;

#ifdef DEBUG_NVIC
  nvicIABR[0] = 0;
  nvicIABR[1] = 0;
#endif

  delay(3000);

  /*
     display of the main context
  */
  currentContext = 10000; // main display context,
  fullChannelDisplayContext();
  isChannelLabelChanged = 0;
  isChannelValueChanged = 0;
  PreviousChannelSelectedStatus = 0;
  for (uint8_t i = 0; i < 8; i++) digitSelected [i] = -1;

  /*
     for keyboard
  */
  buildKeyboardKeyBounds();


  //Deserialisation
  /*
  {
    const size_t capacity = JSON_ARRAY_SIZE(2) + JSON_OBJECT_SIZE(3) + 30;
    DynamicJsonDocument doc(capacity);
    char json[] = "{\"sensor\":\"gps\",\"time\":1351824120,\"data\":[48.756080,2.302038]}";//Using a char[] enables the "zero-copy" mode
    
    DeserializationError error = deserializeJson(doc, json);
  
    if (error) {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }
    
    const char* sensor = doc["sensor"];
    long time = doc["time"];
    double latitude = doc["data"][0];
    double longitude = doc["data"][1];
  
    Serial.println(sensor);
    Serial.println(time);
    Serial.println(latitude, 6);
    Serial.println(longitude, 6);
  }

  //Serialisation
  {
    const size_t capacity = JSON_ARRAY_SIZE(2) + JSON_OBJECT_SIZE(3);
    DynamicJsonDocument doc(capacity);
    
    doc["sensor"] = "gps";
    doc["time"] = 1351824120;
    //alternative : doc["value"].set(42); permet de vérifier la bonne insersion : retourne true si ok, false si plus de place
    
    JsonArray data = doc.createNestedArray("data");
    data.add(48.75608);
    data.add(2.302038);
    
    serializeJson(doc, Serial);
  }
  */
  // alternative à serialisation : 
  // Convert the document to an object
  //JsonObject obj = doc.to<JsonObject>();
  
} // end setup()

void loop() {
  /*
    determines which channel is selected for value modification
  */
  currentSelectedChannel = whichChannelSelectedForValueModification();

  /*
     processes inputs from console if any
  */
  consoleErrorMessage(parseInputFromConsole());
  /*
     to do: refresh DAC and range value = extractFromDisplayedValue
     refresh TFT display
  */


  /*
     processes ALLON & ALLOFF switches
  */

  if (allOnPressed) {
//    onoffStartTime = millis();
#ifdef DEBUG_ONOFF
    Serial.println(onoffStartTime);
#endif
//    while (!digitalRead(SW_ALLON)) {}
#ifdef DEBUG_ONOFF
    Serial.println(millis());
#endif
//    if ((millis() - onoffStartTime) > ONOFF_DELAY) {
      allOn();
      //      delay(DEBOUNCE_TIME_MS);
      channelActiveStatus = 0xFF;
      isChannelLabelChanged = 0xFF; // all channels changed

#ifdef DEBUG_ONOFF
      Serial.println("Channel ON ");
#endif
//    }
    //    delay(DEBOUNCE_TIME_MS);
    allOnPressed = 0;



  }

  if (allOffPressed) {
 //   onoffStartTime = millis();
#ifdef DEBUG_ONOFF
    Serial.println(onoffStartTime);
#endif

//   while (!digitalRead(SW_ALLOFF)) {}
//    if ((millis() - onoffStartTime) > ONOFF_DELAY) {
      allOff();
      delay(DEBOUNCE_TIME_MS);
      channelActiveStatus = 0;
      isChannelLabelChanged = 0xFF; // all channels changed

#ifdef DEBUG_ONOFF
      Serial.println("Channel OFF ");
#endif
//    }
    allOffPressed = 0;
    delay(DEBOUNCE_TIME_MS);
  }

  /*
     processes rotary knob
  */
  if (codeWheelIncrement) {
#ifdef DEBUG_ROTARY
    Serial.print("codeWheelIncrement = ");
    Serial.print(codeWheelIncrement);
#endif

    dummy = updateDisplayedValueWithRotaryKnob(currentSelectedChannel);
    isChannelValueChanged |= 0x01 << currentSelectedChannel;

    if (channelModeStatus & 0x01 << currentSelectedChannel) { // current mode
      /* TBD */
      setChannelRange(currentSelectedChannel, channelCurrentRange[currentSelectedChannel]);
      switch (channelCurrentRange[currentSelectedChannel]) {
        case 0:
          setChannelValue(currentSelectedChannel, (int16_t)(channelDisplayValue[currentSelectedChannel] * 1000));
          break;
        case 1:
          setChannelValue(currentSelectedChannel, (int16_t)(channelDisplayValue[currentSelectedChannel] * 100));
          break;
        case 2:
          setChannelValue(currentSelectedChannel, (int16_t)(channelDisplayValue[currentSelectedChannel] * 10));
          break;
        case 3:
          setChannelValue(currentSelectedChannel, (int16_t)(channelDisplayValue[currentSelectedChannel] * 1000));
          break;
      }

    } else { // voltage mode
      setChannelValue(currentSelectedChannel, (int16_t)(channelDisplayValue[currentSelectedChannel] * 1000));
    }

#ifdef DEBUG_ROTARY
    Serial.print("  channelDisplayValue[");
    Serial.print(currentSelectedChannel);
    Serial.print("] = ");
    Serial.println(channelDisplayValue[currentSelectedChannel], 3);
#endif
    //   delay(DEBOUNCE_ROT_MS);
    codeWheelIncrement = 0;
    //   semaphore = 0;
  }

  /*
     processes rotary knob switch
  */
  if (codeWheelSwPressed) {
#ifdef DEBUG_SWROT
    Serial.println("codeWheel SW ");
#endif
    delay(DEBOUNCE_TIME_MS);
    codeWheelSwPressed = 0;
  }

  /*
    Processes inputs from touchscreen if not locked
    returns:
    255: nothing pressed or only context changed (new context displayed)
    0 to 254:  a button requiring further action is pressed
  */
Serial.println("Before getInputFromTouchScreen"); // changement
if (!touchscreenLocked) tsPressed = getInputFromTouchScreen();
Serial.println("ts pressed : "); // changement : ligne inexistante


  /*
     action &  display update of the main context
  */

  if (currentContext == 10000) {  // test if main context

    for (uint8_t i = 0; i < 8; i++) { // checks which channel was changed
      if (isChannelActiveStatusChanged & 0x01 << i) {
        channelActiveStatus = channelActiveStatus ^ (0x01 << i); // XOR complements variables where second operator is =1
        if (channelActiveStatus & 0x01 << i)switchChannelOn(i);
        else switchChannelOff(i);

      }
    }

    for (uint8_t i = 0; i < 8; i++) { // test for label change
      if (isChannelLabelChanged & 0x01 << i)  displayChannelLabel(i);
      if (PreviousChannelSelectedStatus & 0x01 << i) displayUnselectedChannelValue(i);
      if (isChannelValueChanged & 0x01 << i)  displayChannelValue(i);

    }// end for (uint8_t i = 0; i < 8; i++) { // test for label change

  } // if (currentContext == 10000) {  // test if main context

  isChannelLabelChanged = 0;  // label(s) changed
  isChannelValueChanged = 0; // vlue(s) changed
  isChannelActiveStatusChanged = 0;
  PreviousChannelSelectedStatus = 0;

} // end loop()
