package fr.yncrea.m1_s1project_android.interfaces;

public interface BluetoothConstants {
    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_DEVICE_NAME = 2;
    int MESSAGE_TOAST = 3;
    int MESSAGE_RECEIVE = 4;
    //int MESSAGE_SEND = 5;//permet d'avoir un feedback

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";
    String RECEIVE = "receive";
}
