// Tableau de valeurs pour l'exemple
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
  {0,0,9.6,0,-2,5,1},
  {1,0,3.8,1,-6,5,2},
  {2,0,6.9,0,5,10,1},
  {3,0,1.4,0,0,5,1},
  {4,0,6.7,1,2,7,1},
  {5,0,2.587,0,-0.54,5,0},
  {6,0,1.02,1,0.5,1.50,2},
  {7,0,0.25,0,0,1,0}
};

double allChannels1[5][7] = {
  {0,0,3.8,0,-2,5,1},
  {2,0,6.9,0,5,10,1},
  {3,0,1.4,0,0,5,1},
  {4,0,6.7,1,2,7,1},
  {7,0,0.25,0,0,1,0}
};

double allChannels5[4][7] = {
  {4,0,6.7,1,2,7,1},
  {5,0,2.7,0,-0.4,5,0},
  {6,0,1.2,1,0.5,1.50,2},
  {7,0,0.666,0,0,1,0}
};

String attributes[9] = { "id", "isActive", "currentValue", "unit", "minVoltValue", "minAmpereValue", "maxVoltValue", "maxAmpereValue", "scale" };

 /*
  * Deserialisation à la réception d'un document json
  */

void jsonDeserialize(String cmd){
  /*
  int brace_count = 1;
  for(int i = 1; i < cmd.length(); i++){
    Serial.print(cmd.charAt(i));
    if(cmd.charAt(i) == '{'){ brace_count++; }
    else if(cmd.charAt(i) == '}'){ brace_count--;}
    if(brace_count==0){
      //  if(i!=cmd.length()-1) // verif si c la fin de la string totale reçue
      Serial.println("");
      Serial.println("count 0");
      execJsonCommand(cmd.substring(0, i+1));
      jsonDeserialize(cmd.substring(i+1));
    }
  }
  */
  if(cmd.indexOf("}{") > 0){
    execJsonCommand(cmd.substring(0, cmd.indexOf("}{")+1));
    jsonDeserialize(cmd.substring(cmd.indexOf("}{")+1));
  }
  else{
    execJsonCommand(cmd);
  }
}


void execJsonCommand(String json){
  const size_t capacity = 1024;
  DynamicJsonDocument doc(capacity);
  
  DeserializationError error = deserializeJson(doc, json);

  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    return;
  }

  JsonObject obj = doc.as<JsonObject>();
  //int errors = 0;
  uint8_t channel = -2;
  for(int i = 0; i < 9; i++){
    if(!obj[attributes[i]].isNull() && obj["id"]){
      channel = obj["id"];
      switch(i){
        case 1:
          //channelActiveStatus & 0x01 << channel;
          if(channel==-1){
            for(int i = 0; i < 8; i++) allChannels[i][1] = obj["isActive"];
          }
          else{ 
            allChannels[channel][1] = obj["isActive"]; 
          }
          Serial.print("channelActiveStatus & 0x01 << "); Serial.println(channel);
          break;
        case 2:
          //setChannelValue(channel, obj["currentValue"]);
          allChannels[channel][2] = obj["currentValue"];
          Serial.print("setChannelValue("); Serial.print(channel); Serial.print(", "); serializeJson(obj["currentValue"], Serial); Serial.println(")");
          break;
        case 3:
          //setChannelType(channel, (obj["unit"]=="V") ? 0 : 1);
          allChannels[channel][3] = (obj["unit"]=="V") ? 0 : 1;
          Serial.print("setChannelType("); Serial.print(channel); Serial.print(", "); Serial.print((obj["unit"]=="V") ? "0" : "1"); Serial.println(")");
          break;
        case 4:
          allChannels[channel][4] = obj["minVoltValue"];
          Serial.print("channelMinVoltage["); Serial.print(channel); Serial.println("]");
          break;
        case 5:
          allChannels[channel][4] = obj["minAmpereValue"];
          Serial.print("channelMinCurrent["); Serial.print(channel); Serial.println("]");
          break;
        case 6:
          allChannels[channel][5] = obj["maxVoltValue"];
          Serial.print("channelMaxCurrent["); Serial.print(channel); Serial.println("]");
          break;
        case 7:
          allChannels[channel][5] = obj["maxAmpereValue"];
          Serial.print("channelMaxVoltage["); Serial.print(channel); Serial.println("]");
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

}


/*
 * serializeJson
 */
void initPlz(void){

  //const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(8*7);
  const size_t capacity = 1024;
  DynamicJsonDocument channelList(capacity);
  JsonArray data = channelList.createNestedArray("channelList");
  
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  for(int i = 0; i < 8; i++){
    DynamicJsonDocument channel(capacityChannel);
    // check if channel i exists or is empty
    channel["id"] = allChannels[i][0];
    channel["isActive"] = (allChannels[i][1]==0) ? "false" : "true";                                          // (channelActiveStatus & 0x01 << i) ? "true" : "false";
    channel["currentValue"] = allChannels[i][2];                                                              // getChannelValue(i);
    channel["unit"] = (allChannels[i][3]==0) ? "V" : "I";                                                     // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                                                              // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
    channel[(allChannels[i][3]==0) ? "minVoltValue" : "minAmpereValue"] = allChannels[i][4];                  // channelMinVoltage[i];
    channel[(allChannels[i][3]==0) ? "maxVoltValue" : "maxAmpereValue"] = allChannels[i][5];                  // channelMaxVoltage[i];    
    if(allChannels[i][6]==0){channel["scale"] = "_";}
    else if(allChannels[i][6]==1){channel["scale"] = "m";}
    else if(allChannels[i][6]==2){channel["scale"] = "u";}
    data.add(channel);
  }

  serializeJson(channelList, Serial);
  Serial.println("");
  serializeJson(channelList, hc06);
}


/*
 * renvoi le nombre de stores en différenciant ceux qui sont vides et ceux qui sont occupés
 */
void getAllStores(void){
  // appel de recallUserCongig()
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


/*
 * renvoi la configuration sauvegardée dans le store demandé
 */
void getStore(uint8_t store_number){
  const size_t capacityChannel = JSON_OBJECT_SIZE(7);
  switch(store_number){
    case 0:
    {
      const size_t capacity = JSON_ARRAY_SIZE(8) + JSON_OBJECT_SIZE(8*7);
      DynamicJsonDocument store(capacity);
      JsonArray data = store.createNestedArray("store_get_0");
      for(int i = 0; i < 8; i++){
        DynamicJsonDocument channel(capacityChannel);
        // check if channel i exists or is empty
        channel["id"] = allChannels0[i][0];
        //channel["isActive"] = (allChannels0[i][1]==0) ? "false" : "true";                                         // if (channelActiveStatus & 0x01 << i){"true"}else{"false"} || (channelActiveStatus & 0x01 << i) ? "true" : "false";
        channel["currentValue"] = allChannels0[i][2];                                                               // getChannelValue(i);
        channel["unit"] = (allChannels0[i][3]==0) ? "V" : "I";                                                      // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                                                                    // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel[(allChannels0[i][3]==0) ? "minVoltValue" : "minAmpereValue"] = allChannels0[i][4];                  // channelMinVoltage[i];
        channel[(allChannels0[i][3]==0) ? "maxVoltValue" : "maxAmpereValue"] = allChannels0[i][5];                  // channelMaxVoltage[i];
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
        DynamicJsonDocument channel(capacityChannel);
        // check if channel i exists or is empty
        channel["id"] = allChannels1[i][0];
        //channel["isActive"] = (allChannels1[i][1]==0) ? "false" : "true";                                         // if (channelActiveStatus & 0x01 << i){"true"}else{"false"} || (channelActiveStatus & 0x01 << i) ? "true" : "false";
        channel["currentValue"] = allChannels1[i][2];                                                               // getChannelValue(i);
        channel["unit"] = (allChannels1[i][3]==0) ? "V" : "I";                                                      // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                                                                     // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel[(allChannels1[i][3]==0) ? "minVoltValue" : "minAmpereValue"] = allChannels1[i][4];                  // channelMinVoltage[i];
        channel[(allChannels1[i][3]==0) ? "maxVoltValue" : "maxAmpereValue"] = allChannels1[i][5];                  // channelMaxVoltage[i];
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
        DynamicJsonDocument channel(capacityChannel);
        // check if channel i exists or is empty
        channel["id"] = allChannels5[i][0];
        //channel["isActive"] = (allChannels5[i][1]==0) ? "false" : "true";                                         // if (channelActiveStatus & 0x01 << i){"true"}else{"false"} || (channelActiveStatus & 0x01 << i) ? "true" : "false";
        channel["currentValue"] = allChannels5[i][2];                                                               // getChannelValue(i);
        channel["unit"] = (allChannels5[i][3]==0) ? "V" : "I";                                                      // if(getChannelType(i)==0){channel["unit"] = "V"; channel["minVoltValue"] = channelMinVoltage[i]; channel["maxVoltValue"] = channelMaxVoltage[i];}
                                                                                                                    // else{channel["unit"] = "I"; channel["minAmpereValue"] = channelMinCurrent[i]; channel["maxAmpereValue"] = channelMaxCurrent[i];}
        channel[(allChannels5[i][3]==0) ? "minVoltValue" : "minAmpereValue"] = allChannels5[i][4];                  // channelMinVoltage[i];
        channel[(allChannels5[i][3]==0) ? "maxVoltValue" : "maxAmpereValue"] = allChannels5[i][5];                  // channelMaxVoltage[i];
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


void saveStore(uint8_t store_number){
  // remplace FRAME_MEMORY correspondant à store_number
  Serial.print("Enregistrement du mainboard dans le store ");
  Serial.println(store_number);
}


void deleteStore(uint8_t store_number){
  // vide FRAME_MEMORY correspondant à store_number
  Serial.print("Suppression du store ");
  Serial.println(store_number);
}
