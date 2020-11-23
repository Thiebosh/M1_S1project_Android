package fr.yncrea.m1_s1project_android.interfaces;

public interface BluetoothMethodsParent {
    void connectDevice(final String deviceMacAddress);
    void disconnectDevice();
    void sendData(final String str);
}
