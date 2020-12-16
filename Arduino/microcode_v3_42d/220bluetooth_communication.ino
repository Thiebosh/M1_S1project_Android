void bluetoothCommunication(){
  //Serial.println("exec bluetoothCommunication in 220");
  if (hc06.available() > 0) {
    strCmd = hc06.readString();
    Serial.print("I received : ");
    Serial.println(strCmd);
    //hc06.print(strCmd);
    //hc06.println("\n");
    if (strCmd.equals("init_main")) {
      //json : accès direct aux attributs, sans passer par méthodes
      initPlz();
      /*
      String c1 = "{\"id\":0,\"isActive\":false,\"currentValue\":9.6,\"unit\":V,\"minVoltValue\":-2,\"maxVoltValue\":5,\"scale\":m},";
      String c2 = "{\"id\":1,\"isActive\":true,\"currentValue\":3.8,\"unit\":I,\"minAmpereValue\":-6,\"maxAmpereValue\":5,\"scale\":u},";
      String c3 = "{\"id\":2,\"isActive\":false,\"currentValue\":6.9,\"unit\":V,\"minVoltValue\":5,\"maxVoltValue\":10,\"scale\":m},";
      String c4 = "{\"id\":3,\"isActive\":false,\"currentValue\":1.4,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":5,\"scale\":m},";
      String c5 = "{\"id\":4,\"isActive\":false,\"currentValue\":6.7,\"unit\":I,\"minAmpereValue\":2,\"maxAmpereValue\":7,\"scale\":m},";
      String c6 = "{\"id\":5,\"isActive\":true,\"currentValue\":2.587,\"unit\":V,\"minVoltValue\":-0.54,\"maxVoltValue\":5,\"scale\":_},";
      String c7 = "{\"id\":6,\"isActive\":false,\"currentValue\":1.02,\"unit\":I,\"minAmpereValue\":0.5,\"maxAmpereValue\":1.50,\"scale\":u},";
      String c8 = "{\"id\":7,\"isActive\":true,\"currentValue\":0.25,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":1,\"scale\":_}";
      String init = "{\"channelList\":["+c1+c2+c3+c4+c5+c6+c7+c8+"]}";
      Serial.println(init);
      hc06.print(init);
      */
    }
    else if (strCmd.equals("init_stores")) {
      getAllStores();
      //Serial.println("{\"init_stores\":[1,1,0,0,0,1,0,0]}");
      //hc06.print("{\"init_stores\":[1,1,0,0,0,1,0,0]}");
    }
    
    else if (strCmd.startsWith("store_get_")){
      uint8_t store_number = (uint8_t)strCmd.substring(10).toInt();
      getStore(store_number);
    }
    
    else if(strCmd.startsWith("{")){
      jsonDeserialize(strCmd);
    }
    /*
    else if (strCmd.equals("store_get_0")) {
      String c1 = "{\"id\":0,\"currentValue\":4.2,\"unit\":V,\"minVoltValue\":-2,\"maxVoltValue\":5,\"scale\":m},";
      String c2 = "{\"id\":1,\"currentValue\":1.3,\"unit\":I,\"minAmpereValue\":-6,\"maxAmpereValue\":5,\"scale\":u},";
      String c3 = "{\"id\":2,\"currentValue\":9.5,\"unit\":V,\"minVoltValue\":5,\"maxVoltValue\":10,\"scale\":m},";
      String c4 = "{\"id\":3,\"currentValue\":0.07,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":5,\"scale\":m},";
      String c5 = "{\"id\":4,\"currentValue\":6.7,\"unit\":I,\"minAmpereValue\":2,\"maxAmpereValue\":7,\"scale\":m},";
      String c6 = "{\"id\":5,\"currentValue\":2.587,\"unit\":V,\"minVoltValue\":-0.54,\"maxVoltValue\":5,\"scale\":_},";
      String c7 = "{\"id\":6,\"currentValue\":1.02,\"unit\":I,\"minAmpereValue\":0.5,\"maxAmpereValue\":1.50,\"scale\":u},";
      String c8 = "{\"id\":7,\"currentValue\":0.25,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":1,\"scale\":_}";
      String store0 = "{\"store_get_0\":["+c1+c2+c3+c4+c5+c6+c7+c8+"]}";
      hc06.print(store0);
    }
    else if (strCmd.equals("store_get_1")) {
      String c1 = "{\"id\":0,\"currentValue\":0.9,\"unit\":V,\"minVoltValue\":-2,\"maxVoltValue\":5,\"scale\":m},";
      String c3 = "{\"id\":2,\"currentValue\":5.1,\"unit\":V,\"minVoltValue\":5,\"maxVoltValue\":10,\"scale\":m},";
      String c4 = "{\"id\":3,\"currentValue\":3.7,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":5,\"scale\":m},";
      String c8 = "{\"id\":7,\"currentValue\":0.666,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":1,\"scale\":_}";
      String store1 = "{\"store_get_1\":["+c1+c3+c4+c8+"]}";
      hc06.print(store1);
    }
    */
    else if (strCmd.equals("get")) {
      int reponse = getChannelValue(0);
      hc06.print(reponse);
    }
    else if (strCmd.equals("set")) {
      setChannelValue(0,(getChannelValue(0)+1)%2);
    }
  }

  /*
    while(hc06.available())
    {
    char data = hc06.read();
    Serial.write(data);
    hc06.write(data);
    }
  */
}
