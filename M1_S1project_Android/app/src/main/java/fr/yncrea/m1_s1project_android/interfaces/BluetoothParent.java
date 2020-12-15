package fr.yncrea.m1_s1project_android.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public interface BluetoothParent {
    Generator mGenerator = new Generator();
    ArrayList<Boolean> mIsStores = new ArrayList<>(Arrays.asList(true, true, false));
    ArrayList<Generator> mBackupGenerator = new ArrayList<>();

    boolean getAutoConnect();
    void setAutoConnect(boolean state);
    void connectDevice(final String deviceName, final String deviceMacAddress);
    void disconnectDevice();

    void sendData(final Channel data);//emission / reception
    void sendData(final String data);//emission / reception

    boolean getIsStoresInitialized();
    void setIsStoresInitialized(boolean init);
}
