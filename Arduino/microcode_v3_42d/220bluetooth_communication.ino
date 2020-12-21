void bluetoothCommunication(){
  //Serial.println("exec bluetoothCommunication in 220");
  if (hc06.available() > 0) {
    strCmd = hc06.readString();
    Serial.print("I received : ");
    Serial.println(strCmd);
    
    if (strCmd.equals("init_main")) {
      //json : accès direct aux attributs, sans passer par méthodes
      initPlz();
    }
    else if (strCmd.equals("init_stores")) {
      getAllStores();
    }
    else if (strCmd.startsWith("store_get_")){
      uint8_t store_number = (uint8_t)strCmd.substring(10).toInt();
      getStore(store_number);
    }
    else if (strCmd.startsWith("store_save_")){
      uint8_t store_number = (uint8_t)strCmd.substring(10).toInt();
      saveStore(store_number);
    }
    else if (strCmd.startsWith("store_del_")){
      uint8_t store_number = (uint8_t)strCmd.substring(10).toInt();
      deleteStore(store_number);
    }
    else if(strCmd.startsWith("{")){
      jsonDeserialize(strCmd);
    }
  }
}
