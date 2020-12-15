//{id,b,val,u,mi,M,s} 
double allChannels[8][7] = {
  {0,0,9.6,0,-2,5,1},
  {1,1,3.8,1,-6,5,2},
  {2,0,6.9,0,5,10,1},
  {3,0,1.4,0,0,5,1},
  {4,0,6.7,1,2,7,1},
  {5,1,2.587,0,-0.54,5,0},
  {6,0,1.02,1,0.5,1.50,2},
  {7,1,0.25,0,0,1,0}
};

double allChannels0[8][7] = {
  {0,1,9.6,0,-2,5,1},
  {1,0,3.8,1,-6,5,2},
  {2,1,6.9,0,5,10,1},
  {3,0,1.4,0,0,5,1},
  {4,1,6.7,1,2,7,1},
  {5,0,2.587,0,-0.54,5,0},
  {6,1,1.02,1,0.5,1.50,2},
  {7,0,0.25,0,0,1,0}
};

double allChannels1[5][7] = {
  {0,0,3.8,0,-2,5,1},
  {2,0,6.9,0,5,10,1},
  {3,0,1.4,0,0,5,1},
  {4,1,6.7,1,2,7,1},
  {7,1,0.25,0,0,1,0}
};

double allChannels5[4][7] = {
  {4,0,6.7,1,2,7,1},
  {5,1,2.7,0,-0.4,5,0},
  {6,0,1.2,1,0.5,1.50,2},
  {7,0,0.666,0,0,1,0}
};
/*
  CreateHashMap (simuChannel0, char *, int, 7);
  simuChannel0["id"] = 0;
  simuChannel0["isActive"] = false;
  simuChannel0["currentValue"] = 9.6;
  simuChannel0["unit"] = 'V';
  simuChannel0["minVoltValue"] = -2;
  simuChannel0["maxVoltValue"] = 5;
  simuChannel0["scale"] = 'm';
  
  CreateHashMap (simuChannel1, char *, int, 7);
  simuChannel1["id"] = 1;
  simuChannel1["isActive"] = true;
  simuChannel1["currentValue"] = 3.8;
  simuChannel1["unit"] = 'I';
  simuChannel1["minAmpereValue"] = -6;
  simuChannel1["maxAmpereValue"] = 5;
  simuChannel1["scale"] = 'u';
  
  CreateHashMap (simuChannel2, char *, int, 7);
  simuChannel2["id"] = 2;
  simuChannel2["isActive"] = false; 
  simuChannel2["currentValue"] = 6.9;
  simuChannel2["unit"] = 'V'; 
  simuChannel2["minVoltValue"] = 5;
  simuChannel2["maxVoltValue"] = 10;
  simuChannel2["scale"] = 'm';
  
  CreateHashMap (simuChannel3, char *, int, 7);
  simuChannel3["id"] = 3;
  simuChannel3["isActive"] = false; 
  simuChannel3["currentValue"] = 1.4;
  simuChannel3["unit"] = 'V';
  simuChannel3["minVoltValue"] = 0;
  simuChannel3["maxVoltValue"] = 5;
  simuChannel3["scale"] = 'm';
  
  CreateHashMap (simuChannel4, char *, int, 7);
  simuChannel4["id"] = 4;
  simuChannel4["isActive"] = false;
  simuChannel4["currentValue"] = 6.7;
  simuChannel4["unit"] = 'I'; 
  simuChannel4["minAmpereValue"] = 2;
  simuChannel4["maxAmpereValue"] = 7;
  simuChannel4["scale"] = 'm';
  
  CreateHashMap (simuChannel5, char *, int, 7);
  simuChannel5["id"] = 5;
  simuChannel5["isActive"] = true; 
  simuChannel5["currentValue"] = 2.587; 
  simuChannel5["unit"] = 'V';
  simuChannel5["minVoltValue"] = -0.54;
  simuChannel5["maxVoltValue"] = 5;
  simuChannel5["scale"] = '_';
  
  CreateHashMap (simuChannel6, char *, int, 7);
  simuChannel6["id"] = 6;
  simuChannel6["isActive"] = false; 
  simuChannel6["currentValue"] = 1.02;
  simuChannel6["unit"] = 'I'; 
  simuChannel6["minAmpereValue"] = 0.5;
  simuChannel6["maxAmpereValue"] = 1.50;
  simuChannel6["scale"] = 'u';
  
  CreateHashMap (simuChannel7, char *, int, 7);
  simuChannel7["id"] = 7;
  simuChannel7["isActive"] = true;
  simuChannel7["currentValue"] = 0.25;
  simuChannel7["unit"] = 'V';
  simuChannel7["minVoltValue"] = 0; 
  simuChannel7["maxVoltValue"] = 1; 
  simuChannel7["scale"] = '_';
*/

void jsonDeserialization(char* json){
  const size_t capacity = JSON_ARRAY_SIZE(2) + JSON_OBJECT_SIZE(3) + 30;
  DynamicJsonDocument doc(capacity);
  //char json[] = "{\"sensor\":\"gps\",\"time\":1351824120,\"data\":[48.756080,2.302038]}";//Using a char[] enables the "zero-copy" mode {"sensor":"gps","time":1351824120,"data":[48.756080,2.302038]}
  
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
void initPlz(void){
  
  // alternative à serialisation : 
  // Convert the document to an object
  //JsonObject obj = doc.to<JsonObject>();

/*
  // check if channel 0 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel0(capacity);
  channel0["id"] = 0; // get channel id
  channel0["isActive"] = false; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){true}else{false}
  channel0["currentValue"] = 9.6; // getChannelValue(0)
  channel0["unit"] = 'V'; // get unit
  channel0["minVoltValue"] = -2;
  channel0["maxVoltValue"] = 5;
  channel0["scale"] = 'm';

  // check if channel 1 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel1(capacity);
  channel1["id"] = 1; // get channel id
  channel1["isActive"] = true; // get state of channel 1 : if (channelActiveStatus & 0x01 << 1){true}else{false}
  channel1["currentValue"] = 3.8; // getChannelValue(1)
  channel1["unit"] = 'I'; // get unit
  channel1["minAmpereValue"] = -6;
  channel1["maxAmpereValue"] = 5;
  channel1["scale"] = 'u';

  // check if channel 2 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel2(capacity);
  channel2["id"] = 2;
  channel2["isActive"] = false; // if (channelActiveStatus & 0x01 << 2){true}else{false}
  channel2["currentValue"] = 6.9; // getChannelValue(2)
  channel2["unit"] = 'V'; // get unit
  channel2["minVoltValue"] = 5;
  channel2["maxVoltValue"] = 10;
  channel2["scale"] = 'm';

  // check if channel 3 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel3(capacity);
  channel3["id"] = 3;
  channel3["isActive"] = false; // if (channelActiveStatus & 0x01 << 3){true}else{false}
  channel3["currentValue"] = 1.4; // getChannelValue(3)
  channel3["unit"] = 'V'; // get unit
  channel3["minVoltValue"] = 0;
  channel3["maxVoltValue"] = 5;
  channel3["scale"] = 'm';
  
  // check if channel 4 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel4(capacity);
  channel4["id"] = 4;
  channel4["isActive"] = false; // if (channelActiveStatus & 0x01 << 4){true}else{false}
  channel4["currentValue"] = 6.7; // getChannelValue(4)
  channel4["unit"] = 'I'; // get unit
  channel4["minAmpereValue"] = 2;
  channel4["maxAmpereValue"] = 7;
  channel4["scale"] = 'm';
  
  // check if channel 5 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel5(capacity);
  channel5["id"] = 5;
  channel5["isActive"] = true; // if (channelActiveStatus & 0x01 << 5){true}else{false}
  channel5["currentValue"] = 2.587; // getChannelValue(5)
  channel5["unit"] = 'V'; // get unit
  channel5["minVoltValue"] = -0.54;
  channel5["maxVoltValue"] = 5;
  channel5["scale"] = '_';
  
  // check if channel 6 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel6(capacity);
  channel6["id"] = 6;
  channel6["isActive"] = false; // if (channelActiveStatus & 0x01 << 6){true}else{false}
  channel6["currentValue"] = 1.02; // getChannelValue(6)
  channel6["unit"] = 'I'; // get unit
  channel6["minAmpereValue"] = 0.5;
  channel6["maxAmpereValue"] = 1.50;
  channel6["scale"] = 'u';
  
  // check if channel 7 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel7(capacity);
  channel7["id"] = 7;
  channel7["isActive"] = true; // if (channelActiveStatus & 0x01 << 7){true}else{false}
  channel7["currentValue"] = 0.25; // getChannelValue(7)
  channel7["unit"] = 'V'; // if(getChannelType(7)==0){channel7["unit"] = 'V'; channel7["minVoltValue"] = channelMinVoltage[7]; channel7["maxVoltValue"] = channelMaxVoltage[7];}
                        // else{channel7["unit"] = 'I'; channel7["minAmpereValue"] = channelMinCurrent[7]; channel7["maxAmpereValue"] = channelMaxCurrent[7];}
  channel7["minVoltValue"] = 0; // channelMinVoltage[7]
  channel7["maxVoltValue"] = 1; // channelMaxVoltage[7]
  channel7["scale"] = '_';
  
  const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(1);
  DynamicJsonDocument channelList(capacity);
  JsonArray data = channelList.createNestedArray("channelList");
  data.add(channel0);
  data.add(channel1);
  data.add(channel2);
  data.add(channel3);
  data.add(channel4);
  data.add(channel5);
  data.add(channel6);
  data.add(channel7);
*/

  const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(1);
  DynamicJsonDocument channelList(capacity);
  JsonArray data = channelList.createNestedArray("channelList");
  
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel(capacityChannel);
  for(int i = 0; i < 8; i++){
    // check if channel i exists or is empty
    channel["id"] = allChannels[i][0];
    channel["isActive"] = (allChannels[i][1]==0) ? false : true;  // if (channelActiveStatus & 0x01 << i){true}else{false} || (channelActiveStatus & 0x01 << i) ? true : false;
    channel["currentValue"] = allChannels[i][2];                  // getChannelValue(i);
    channel["unit"] = (allChannels[i][3]==0) ? 'V' : 'I';             // if(getChannelType(i)==0){channel["unit"] = 'V'; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                  // else{channel["unit"] = 'I'; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
    channel["minVoltValue"] = allChannels[i][4];                  // channelMinVoltage[i];
    channel["maxVoltValue"] = allChannels[i][5];                  // channelMaxVoltage[i];
    if(allChannels[i][6]==0){channel["scale"] = '_';}
    else if(allChannels[i][6]==1){channel["scale"] = 'm';}
    else if(allChannels[i][6]==2){channel["scale"] = 'u';}
    data.add(channel);
  }
  
  serializeJson(channelList, Serial);
  serializeJson(channelList, hc06);
}


void getAllStores(void){
  const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(1);
  DynamicJsonDocument getStores(capacity);
  JsonArray data = getStores.createNestedArray("getStores");
  data.add(1); // if (userConfigStored & 0x01 << 0){ data.add(1); } else{ data.add(0); }
  data.add(1); // if (userConfigStored & 0x01 << 1){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 2){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 3){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 4){ data.add(1); } else{ data.add(0); }
  data.add(1); // if (userConfigStored & 0x01 << 5){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 6){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 7){ data.add(1); } else{ data.add(0); }

  serializeJson(getStores, Serial);
  serializeJson(getStores, hc06);
}


void getStore(uint8_t store_number){
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel(capacityChannel);
  switch(store_number){
    case 0:
    {
      const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(1);
      DynamicJsonDocument store(capacity);
      JsonArray data = store.createNestedArray("store0");
      for(int i = 0; i < 8; i++){
        // check if channel i exists or is empty
        channel["id"] = allChannels0[i][0];
        channel["isActive"] = (allChannels0[i][1]==0) ? false : true;  // if (channelActiveStatus & 0x01 << i){true}else{false} || (channelActiveStatus & 0x01 << i) ? true : false;
        channel["currentValue"] = allChannels0[i][2];                  // getChannelValue(i);
        channel["unit"] = (allChannels0[i][3]==0) ? 'V' : 'I';             // if(getChannelType(i)==0){channel["unit"] = 'V'; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                      // else{channel["unit"] = 'I'; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel["minVoltValue"] = allChannels0[i][4];                  // channelMinVoltage[i];
        channel["maxVoltValue"] = allChannels0[i][5];                  // channelMaxVoltage[i];
        if(allChannels0[i][6]==0){channel["scale"] = '_';}
        else if(allChannels0[i][6]==1){channel["scale"] = 'm';}
        else if(allChannels0[i][6]==2){channel["scale"] = 'u';}
        data.add(channel);
      }
      serializeJson(store, Serial);
      serializeJson(store, hc06);
    }
    break;
    case 1:
    {
      const size_t capacity = JSON_ARRAY_SIZE(5) + JSON_OBJECT_SIZE(1);
      DynamicJsonDocument store(capacity);
      JsonArray data = store.createNestedArray("store1");
      for(int i = 0; i < 5; i++){
        // check if channel i exists or is empty
        channel["id"] = allChannels1[i][0];
        channel["isActive"] = (allChannels1[i][1]==0) ? false : true;  // if (channelActiveStatus & 0x01 << i){true}else{false} || (channelActiveStatus & 0x01 << i) ? true : false;
        channel["currentValue"] = allChannels1[i][2];                  // getChannelValue(i);
        channel["unit"] = (allChannels1[i][3]==0) ? 'V' : 'I';             // if(getChannelType(i)==0){channel["unit"] = 'V'; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                      // else{channel["unit"] = 'I'; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel["minVoltValue"] = allChannels1[i][4];                  // channelMinVoltage[i];
        channel["maxVoltValue"] = allChannels1[i][5];                  // channelMaxVoltage[i];
        if(allChannels1[i][6]==0){channel["scale"] = '_';}
        else if(allChannels1[i][6]==1){channel["scale"] = 'm';}
        else if(allChannels1[i][6]==2){channel["scale"] = 'u';}
        data.add(channel);
      }
      serializeJson(store, Serial);
      serializeJson(store, hc06);
    }
    break;
    case 5:
    {
      const size_t capacity = JSON_ARRAY_SIZE(4) + JSON_OBJECT_SIZE(1);
      DynamicJsonDocument store(capacity);
      JsonArray data = store.createNestedArray("store5");
      for(int i = 0; i < 4; i++){
        // check if channel i exists or is empty
        channel["id"] = allChannels5[i][0];
        channel["isActive"] = (allChannels5[i][1]==0) ? false : true;  // if (channelActiveStatus & 0x01 << i){true}else{false} || (channelActiveStatus & 0x01 << i) ? true : false;
        channel["currentValue"] = allChannels5[i][2];                  // getChannelValue(i);
        channel["unit"] = (allChannels5[i][3]==0) ? 'V' : 'I';             // if(getChannelType(i)==0){channel["unit"] = 'V'; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                      // else{channel["unit"] = 'I'; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel["minVoltValue"] = allChannels5[i][4];                  // channelMinVoltage[i];
        channel["maxVoltValue"] = allChannels5[i][5];                  // channelMaxVoltage[i];
        if(allChannels5[i][6]==0){channel["scale"] = '_';}
        else if(allChannels5[i][6]==1){channel["scale"] = 'm';}
        else if(allChannels5[i][6]==2){channel["scale"] = 'u';}
        data.add(channel);
      }
      serializeJson(store, Serial);
      serializeJson(store, hc06);
    }
    break;
  }
}
