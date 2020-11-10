package fr.yncrea.m1_s1project_android.bluetooth;

import android.os.Handler;

public interface BluetoothMethods {
    //void updateHandler(final Handler handler);

    void connectDevice(final String deviceMacAddress);
}
