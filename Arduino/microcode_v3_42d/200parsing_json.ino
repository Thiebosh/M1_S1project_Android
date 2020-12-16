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
  {0,9.6,0,-2,5,1},
  {1,3.8,1,-6,5,2},
  {2,6.9,0,5,10,1},
  {3,1.4,0,0,5,1},
  {4,6.7,1,2,7,1},
  {5,2.587,0,-0.54,5,0},
  {6,1.02,1,0.5,1.50,2},
  {7,0.25,0,0,1,0}
};

double allChannels1[5][7] = {
  {0,3.8,0,-2,5,1},
  {2,6.9,0,5,10,1},
  {3,1.4,0,0,5,1},
  {4,6.7,1,2,7,1},
  {7,0.25,0,0,1,0}
};

double allChannels5[4][7] = {
  {4,6.7,1,2,7,1},
  {5,2.7,0,-0.4,5,0},
  {6,1.2,1,0.5,1.50,2},
  {7,0.666,0,0,1,0}
};

String attributes[9] = { "id", "isActive", "currentValue", "unit", "minVoltValue", "minAmpereValue", "maxVoltValue", "maxAmpereValue", "scale" };
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
  simuChannel1["isActive"] = "true";
  simuChannel1["currentValue"] = 3.8;
  simuChannel1["unit"] = "I";
  simuChannel1["minAmpereValue"] = -6;
  simuChannel1["maxAmpereValue"] = 5;
  simuChannel1["scale"] = "u";
  
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
  simuChannel4["unit"] = "I"; 
  simuChannel4["minAmpereValue"] = 2;
  simuChannel4["maxAmpereValue"] = 7;
  simuChannel4["scale"] = 'm';
  
  CreateHashMap (simuChannel5, char *, int, 7);
  simuChannel5["id"] = 5;
  simuChannel5["isActive"] = "true"; 
  simuChannel5["currentValue"] = 2.587; 
  simuChannel5["unit"] = 'V';
  simuChannel5["minVoltValue"] = -0.54;
  simuChannel5["maxVoltValue"] = 5;
  simuChannel5["scale"] = "_";
  
  CreateHashMap (simuChannel6, char *, int, 7);
  simuChannel6["id"] = 6;
  simuChannel6["isActive"] = false; 
  simuChannel6["currentValue"] = 1.02;
  simuChannel6["unit"] = "I"; 
  simuChannel6["minAmpereValue"] = 0.5;
  simuChannel6["maxAmpereValue"] = 1.50;
  simuChannel6["scale"] = "u";
  
  CreateHashMap (simuChannel7, char *, int, 7);
  simuChannel7["id"] = 7;
  simuChannel7["isActive"] = "true";
  simuChannel7["currentValue"] = 0.25;
  simuChannel7["unit"] = 'V';
  simuChannel7["minVoltValue"] = 0; 
  simuChannel7["maxVoltValue"] = 1; 
  simuChannel7["scale"] = "_";
*/
 /*
  * Deserialisation à la réception d'un document json
  */

void jsonDeserialize(String cmd){
  Serial.println("jsonDeserialize");
  int brace_count = 1;
  for(int i = 1; i < cmd.length(); i++){
    if(cmd.charAt(i) == '{'){ brace_count++; }
    else if(cmd.charAt(i) == '}'){ brace_count--;}
    if(brace_count==0){
      //  if(i!=cmd.length()-1) // verif si c la fin de la string totale reçue
      //execJsonCommand(cmd.substring(0, i+1));
      Serial.println("count 0");
      jsonDeserialize(cmd.substring(i+1));
    }
  }
}


void execJsonCommand(String json){
  Serial.println("execJsonCommand");
  const size_t capacity = 1024;
  DynamicJsonDocument doc(capacity);
  //char json[] = "{\"sensor\":\"gps\",\"time\":1351824120,\"data\":[48.756080,2.302038]}";//Using a char[] enables the "zero-copy" mode {"sensor":"gps","time":1351824120,"data":[48.756080,2.302038]}
  
  DeserializationError error = deserializeJson(doc, json);

  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    return;
  }

  JsonObject obj = doc.to<JsonObject>();
  //int errors = 0;
  uint8_t channel = -2;
  for(int i = 0; i < 9; i++){
    if(obj[attributes[i]] && obj["id"]){
      channel = obj["id"];
      switch(i){
        case 1:
          //channelActiveStatus & 0x01 << channel;
          allChannels[channel][1] = (obj["isActive"]=="false") ? 0 : 1;
          Serial.println("case isActive");
          break;
        case 2:
          //setChannelValue(channel, obj["currentValue"];
          allChannels[channel][2] = obj["currentValue"];
          Serial.println("case currentValue");
          break;
        case 3:
          //setChannelType(channel, (obj["unit"]=="V") ? 0 : 1);
          allChannels[channel][3] = (obj["unit"]=="V") ? 0 : 1;
          Serial.println("case unit");
          break;
        case 4:
          allChannels[channel][4] = obj["minVoltValue"];
          Serial.println("case minVoltValue");
          break;
        case 5:
          allChannels[channel][4] = obj["minAmpereValue"];
          Serial.println("case minAmpereValue");
          break;
        case 6:
          allChannels[channel][5] = obj["maxVoltValue"];
          Serial.println("case maxVoltValue");
          break;
        case 7:
          allChannels[channel][5] = obj["maxAmpereValue"];
          Serial.println("case maxAmpereValue");
          break;
        case 8:
          {
            if(obj["scale"]=="_"){ allChannels[channel][6] = 0; Serial.println("case scale _"); }
            else if(obj["scale"]=="m"){ allChannels[channel][6] = 1; Serial.println("case scale m"); }
            else if(obj["scale"]=="u"){ allChannels[channel][6] = 2; Serial.println("case scale u"); }
          }
          break;
      }
    }
  }
  //Serial.println("");
  for(int i = 0; i < 7; i++){
    Serial.print(allChannels[channel][i]);
    Serial.print(" , ");
  }
  Serial.println("");

  /*
  const char* sensor = doc["sensor"];
  long time = doc["time"];
  double latitude = doc["data"][0];
  double longitude = doc["data"][1];

  Serial.println(sensor);
  Serial.println(time);
  Serial.println(latitude, 6);
  Serial.println(longitude, 6);
  */
}

  //Serialisation
void initPlz(void){
  
  // alternative à serialisation : 
  // Convert the document to an object
  //JsonObject obj = doc.to<JsonObject>();

/*
  // check if channel 0 exists or is empty
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel0(capacityChannel);
  DynamicJsonDocument channel1(capacityChannel);
  DynamicJsonDocument channel2(capacityChannel);
  DynamicJsonDocument channel3(capacityChannel);
  DynamicJsonDocument channel4(capacityChannel);
  DynamicJsonDocument channel5(capacityChannel);
  DynamicJsonDocument channel6(capacityChannel);
  DynamicJsonDocument channel7(capacityChannel);

  
  channel0["id"] = allChannels[0][0]; // get channel id
  channel0["isActive"] = (allChannels[0][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{false}
  channel0["currentValue"] = allChannels[0][2]; // getChannelValue(0)
  if(allChannels[0][3]==0){channel0["unit"] = "V"; channel0["minVoltValue"] = allChannels[0][4]; channel0["maxVoltValue"] = allChannels[0][5];}
  else{channel0["unit"] = "I"; channel0["minAmpereValue"] = allChannels[0][4]; channel0["maxAmpereValue"] = allChannels[0][5];}
  if(allChannels[0][6]==0){ channel0["scale"]="_"; }
  else if(allChannels[0][6]==1){ channel0["scale"]="m"; }
  else if(allChannels[0][6]==2){ channel0["scale"]="u"; }
  //channel0["unit"] = "V"; // get unit
  //channel0["minVoltValue"] = -2;
  //channel0["maxVoltValue"] = 5;
  //channel0["scale"] = "m";


  // check if channel 1 exists or is empty
  channel1["id"] = allChannels[1][0]; // get channel id
  channel1["isActive"] = (allChannels[1][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{"false"}
  channel1["currentValue"] = allChannels[1][2]; // getChannelValue(0)
  if(allChannels[1][3]==0){channel1["unit"] = "V"; channel1["minVoltValue"] = allChannels[1][4]; channel1["maxVoltValue"] = allChannels[1][5];}
  else{channel1["unit"] = "I"; channel1["minAmpereValue"] = allChannels[1][4]; channel1["maxAmpereValue"] = allChannels[1][5];}
  if(allChannels[1][6]==0){ channel1["scale"]="_"; }
  else if(allChannels[1][6]==1){ channel1["scale"]="m"; }
  else if(allChannels[1][6]==2){ channel1["scale"]="u"; }
  //channel1["unit"] = "V"; // get unit
  //channel1["minVoltValue"] = -2;
  //channel1["maxVoltValue"] = 5;
  //channel1["scale"] = "m";


  // check if channel 2 exists or is empty
  channel2["id"] = allChannels[2][0]; // get channel id
  channel2["isActive"] = (allChannels[2][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{"false"}
  channel2["currentValue"] = allChannels[2][2]; // getChannelValue(0)
  if(allChannels[2][3]==0){channel2["unit"] = "V"; channel2["minVoltValue"] = allChannels[2][4]; channel2["maxVoltValue"] = allChannels[2][5];}
  else{channel2["unit"] = "I"; channel2["minAmpereValue"] = allChannels[2][4]; channel2["maxAmpereValue"] = allChannels[2][5];}
  if(allChannels[2][6]==0){ channel2["scale"]="_"; }
  else if(allChannels[2][6]==1){ channel2["scale"]="m"; }
  else if(allChannels[2][6]==2){ channel2["scale"]="u"; }
  //channel2["unit"] = "V"; // get unit
  //channel2["minVoltValue"] = -2;
  //channel2["maxVoltValue"] = 5;
  //channel2["scale"] = "m";



  // check if channel 3 exists or is empty
  channel3["id"] = allChannels[3][0]; // get channel id
  channel3["isActive"] = (allChannels[3][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{"false"}
  channel3["currentValue"] = allChannels[3][2]; // getChannelValue(0)
  if(allChannels[3][3]==0){channel3["unit"] = "V"; channel3["minVoltValue"] = allChannels[3][4]; channel3["maxVoltValue"] = allChannels[3][5];}
  else{channel3["unit"] = "I"; channel3["minAmpereValue"] = allChannels[3][4]; channel3["maxAmpereValue"] = allChannels[3][5];}
  if(allChannels[3][6]==0){ channel3["scale"]="_"; }
  else if(allChannels[3][6]==1){ channel3["scale"]="m"; }
  else if(allChannels[3][6]==2){ channel3["scale"]="u"; }
  //channel3["unit"] = "V"; // get unit
  //channel3["minVoltValue"] = -2;
  //channel3["maxVoltValue"] = 5;
  //channel3["scale"] = "m";


  // check if channel 4 exists or is empty
  channel4["id"] = allChannels[4][0]; // get channel id
  channel4["isActive"] = (allChannels[4][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{"false"}
  channel4["currentValue"] = allChannels[4][2]; // getChannelValue(0)
  if(allChannels[4][3]==0){channel4["unit"] = "V"; channel4["minVoltValue"] = allChannels[4][4]; channel4["maxVoltValue"] = allChannels[4][5];}
  else{channel4["unit"] = "I"; channel4["minAmpereValue"] = allChannels[4][4]; channel4["maxAmpereValue"] = allChannels[4][5];}
  if(allChannels[4][6]==0){ channel4["scale"]="_"; }
  else if(allChannels[4][6]==1){ channel4["scale"]="m"; }
  else if(allChannels[4][6]==2){ channel4["scale"]="u"; }
  //channel4["unit"] = "V"; // get unit
  //channel4["minVoltValue"] = -2;
  //channel4["maxVoltValue"] = 5;
  //channel4["scale"] = "m";


  // check if channel 5 exists or is empty
  channel5["id"] = allChannels[5][0]; // get channel id
  channel5["isActive"] = (allChannels[5][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{"false"}
  channel5["currentValue"] = allChannels[5][2]; // getChannelValue(0)
  if(allChannels[5][3]==0){channel5["unit"] = "V"; channel5["minVoltValue"] = allChannels[5][4]; channel5["maxVoltValue"] = allChannels[5][5];}
  else{channel5["unit"] = "I"; channel5["minAmpereValue"] = allChannels[5][4]; channel5["maxAmpereValue"] = allChannels[5][5];}
  if(allChannels[5][6]==0){ channel5["scale"]="_"; }
  else if(allChannels[5][6]==1){ channel5["scale"]="m"; }
  else if(allChannels[5][6]==2){ channel5["scale"]="u"; }
  //channel5["unit"] = "V"; // get unit
  //channel5["minVoltValue"] = -2;
  //channel5["maxVoltValue"] = 5;
  //channel5["scale"] = "m";


  // check if channel 6 exists or is empty
  channel6["id"] = allChannels[6][0]; // get channel id
  channel6["isActive"] = (allChannels[6][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{"false"}
  channel6["currentValue"] = allChannels[6][2]; // getChannelValue(0)
  if(allChannels[6][3]==0){channel6["unit"] = "V"; channel6["minVoltValue"] = allChannels[6][4]; channel6["maxVoltValue"] = allChannels[6][5];}
  else{channel6["unit"] = "I"; channel6["minAmpereValue"] = allChannels[6][4]; channel6["maxAmpereValue"] = allChannels[6][5];}
  if(allChannels[6][6]==0){ channel6["scale"]="_"; }
  else if(allChannels[6][6]==1){ channel6["scale"]="m"; }
  else if(allChannels[6][6]==2){ channel6["scale"]="u"; }
  //channel6["unit"] = "V"; // get unit
  //channel6["minVoltValue"] = -2;
  //channel6["maxVoltValue"] = 5;
  //channel6["scale"] = "m";


  // check if channel 7 exists or is empty
  channel7["id"] = allChannels[7][0]; // get channel id
  channel7["isActive"] = (allChannels[7][1]==0) ? "false" : "true"; // get state of channel 0 : if (channelActiveStatus & 0x01 << 0){"true"}else{"false"}
  channel7["currentValue"] = allChannels[7][2]; // getChannelValue(0)
  if(allChannels[7][3]==0){channel7["unit"] = "V"; channel7["minVoltValue"] = allChannels[7][4]; channel7["maxVoltValue"] = allChannels[7][5];}
  else{channel7["unit"] = "I"; channel7["minAmpereValue"] = allChannels[7][4]; channel7["maxAmpereValue"] = allChannels[7][5];}
  if(allChannels[7][6]==0){ channel7["scale"]="_"; }
  else if(allChannels[7][6]==1){ channel7["scale"]="m"; }
  else if(allChannels[7][6]==2){ channel7["scale"]="u"; }
  //channel7["unit"] = "V"; // get unit
  //channel7["minVoltValue"] = -2;
  //channel7["maxVoltValue"] = 5;
  //channel7["scale"] = "m";

  
  const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(8);
  DynamicJsonDocument channelList(capacity);
  JsonArray data = channelList.createNestedArray("channelList");
  data.add(channel1);
  data.add(channel1);
  data.add(channel2);
  data.add(channel3);
  data.add(channel4);
  data.add(channel5);
  data.add(channel6);
  data.add(channel7);
*/

  //const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(8*7);
  const size_t capacity = 1024;
  DynamicJsonDocument channelList(capacity);
  JsonArray data = channelList.createNestedArray("channelList");
  
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel(capacityChannel);
  for(int i = 0; i < 8; i++){
    // check if channel i exists or is empty
    channel["id"] = allChannels[i][0];
    channel["isActive"] = (allChannels[i][1]==0) ? "false" : "true";  // (channelActiveStatus & 0x01 << i) ? "true" : "false";
    channel["currentValue"] = allChannels[i][2];                  // getChannelValue(i);
    channel["unit"] = (allChannels[i][3]==0) ? "V" : "I";             // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                  // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
    channel["minVoltValue"] = allChannels[i][4];                  // channelMinVoltage[i];
    channel["maxVoltValue"] = allChannels[i][5];                  // channelMaxVoltage[i];
    if(allChannels[i][6]==0){channel["scale"] = "_";}
    else if(allChannels[i][6]==1){channel["scale"] = "m";}
    else if(allChannels[i][6]==2){channel["scale"] = "u";}
    data.add(channel);
  }

  serializeJson(channelList, Serial);
  Serial.println("");
  serializeJson(channelList, hc06);
}


void getAllStores(void){
  const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(1);
  DynamicJsonDocument getStores(capacity);
  JsonArray data = getStores.createNestedArray("init_stores");
  data.add(1); // if (userConfigStored & 0x01 << 0){ data.add(1); } else{ data.add(0); }
  data.add(1); // if (userConfigStored & 0x01 << 1){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 2){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 3){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 4){ data.add(1); } else{ data.add(0); }
  data.add(1); // if (userConfigStored & 0x01 << 5){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 6){ data.add(1); } else{ data.add(0); }
  data.add(0); // if (userConfigStored & 0x01 << 7){ data.add(1); } else{ data.add(0); }

  serializeJson(getStores, Serial);
  Serial.println("");
  serializeJson(getStores, hc06);
}


void getStore(uint8_t store_number){
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  DynamicJsonDocument channel(capacityChannel);
  switch(store_number){
    case 0:
    {
      const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(8*7);
      DynamicJsonDocument store(capacity);
      JsonArray data = store.createNestedArray("store_get_0");
      for(int i = 0; i < 8; i++){
        // check if channel i exists or is empty
        channel["id"] = allChannels0[i][0];
        //channel["isActive"] = (allChannels0[i][1]==0) ? "false" : "true";  // if (channelActiveStatus & 0x01 << i){"true"}else{"false"} || (channelActiveStatus & 0x01 << i) ? "true" : "false";
        channel["currentValue"] = allChannels0[i][2];                  // getChannelValue(i);
        channel["unit"] = (allChannels0[i][3]==0) ? "V" : "I";             // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                      // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel["minVoltValue"] = allChannels0[i][4];                  // channelMinVoltage[i];
        channel["maxVoltValue"] = allChannels0[i][5];                  // channelMaxVoltage[i];
        if(allChannels0[i][6]==0){channel["scale"] = "_";}
        else if(allChannels0[i][6]==1){channel["scale"] = "m";}
        else if(allChannels0[i][6]==2){channel["scale"] = "u";}
        data.add(channel);
      }
      serializeJson(store, Serial);
      Serial.println("");
      serializeJson(store, hc06);
    }
    break;
    case 1:
    {
      const size_t capacity = JSON_ARRAY_SIZE(5) + JSON_OBJECT_SIZE(5*7);
      DynamicJsonDocument store(capacity);
      JsonArray data = store.createNestedArray("store_get_1");
      for(int i = 0; i < 5; i++){
        // check if channel i exists or is empty
        channel["id"] = allChannels1[i][0];
        //channel["isActive"] = (allChannels1[i][1]==0) ? "false" : "true";  // if (channelActiveStatus & 0x01 << i){"true"}else{"false"} || (channelActiveStatus & 0x01 << i) ? "true" : "false";
        channel["currentValue"] = allChannels1[i][2];                  // getChannelValue(i);
        channel["unit"] = (allChannels1[i][3]==0) ? "V" : "I";             // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                      // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel["minVoltValue"] = allChannels1[i][4];                  // channelMinVoltage[i];
        channel["maxVoltValue"] = allChannels1[i][5];                  // channelMaxVoltage[i];
        if(allChannels1[i][6]==0){channel["scale"] = "_";}
        else if(allChannels1[i][6]==1){channel["scale"] = "m";}
        else if(allChannels1[i][6]==2){channel["scale"] = "u";}
        data.add(channel);
      }
      serializeJson(store, Serial);
      Serial.println("");
      serializeJson(store, hc06);
    }
    break;
    case 5:
    {
      const size_t capacity = JSON_ARRAY_SIZE(4) + JSON_OBJECT_SIZE(4*7);
      DynamicJsonDocument store(capacity);
      JsonArray data = store.createNestedArray("store_get_5");
      for(int i = 0; i < 4; i++){
        // check if channel i exists or is empty
        channel["id"] = allChannels5[i][0];
        //channel["isActive"] = (allChannels5[i][1]==0) ? "false" : "true";  // if (channelActiveStatus & 0x01 << i){"true"}else{"false"} || (channelActiveStatus & 0x01 << i) ? "true" : "false";
        channel["currentValue"] = allChannels5[i][2];                  // getChannelValue(i);
        channel["unit"] = (allChannels5[i][3]==0) ? "V" : "I";             // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                      // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel["minVoltValue"] = allChannels5[i][4];                  // channelMinVoltage[i];
        channel["maxVoltValue"] = allChannels5[i][5];                  // channelMaxVoltage[i];
        if(allChannels5[i][6]==0){channel["scale"] = "_";}
        else if(allChannels5[i][6]==1){channel["scale"] = "m";}
        else if(allChannels5[i][6]==2){channel["scale"] = "u";}
        data.add(channel);
      }
      serializeJson(store, Serial);
      Serial.println("");
      serializeJson(store, hc06);
    }
    break;
  }
}
