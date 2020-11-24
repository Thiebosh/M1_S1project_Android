package fr.yncrea.m1_s1project_android.interfaces;

import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public interface BluetoothParent {
    void connectDevice(final String deviceMacAddress);
    void disconnectDevice();
    void sendData(final Channel data);//emission / reception
    Generator getGenerator();
}
