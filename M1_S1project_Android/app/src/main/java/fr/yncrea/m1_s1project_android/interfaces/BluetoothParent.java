package fr.yncrea.m1_s1project_android.interfaces;

public interface BluetoothParent {
    void connectDevice(final String deviceMacAddress);
    void disconnectDevice();
    void sendData(final Object data);
    Object getGenerator();
}
