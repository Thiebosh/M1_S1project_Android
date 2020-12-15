package fr.yncrea.m1_s1project_android.interfaces;

public interface BluetoothConstants {
    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    //int MESSAGE_DEVICE_NAME = 2;
    int MESSAGE_RECEIVE = 3;

    // Key names received from the BluetoothChatService Handler
    //String DEVICE_NAME = "device_name";
    String RECEIVE = "receive";

    String PREF_SLOT_ACCESS = "devicePrefs";
    String PREF_IS_SAVED = "addressData";
    String PREF_ACCESS_ADDRESS = "macAddress";
    String PREF_ACCESS_NAME = "deviceName";
}
