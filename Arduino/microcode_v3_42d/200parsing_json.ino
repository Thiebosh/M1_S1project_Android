void parsingJson(){
  
  //Deserialisation
  
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
  
  // alternative à serialisation : 
  // Convert the document to an object
  //JsonObject obj = doc.to<JsonObject>();
}
