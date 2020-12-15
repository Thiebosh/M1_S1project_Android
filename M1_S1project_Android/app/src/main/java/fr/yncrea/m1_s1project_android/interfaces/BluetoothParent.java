package fr.yncrea.m1_s1project_android.interfaces;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public interface BluetoothParent {
    Generator mGenerator = new Generator();
    //Generator mBackupGenerator = new Generator();
    //boolean isStoresInitialized = false;
    boolean[] isStoreCharged = new boolean[]{};
    ArrayList<Boolean> isStore = new ArrayList<>();
    ArrayList<Generator> mBackupGenerator = new ArrayList<Generator>(){{
        add(new Generator());
        add(new Generator());
        add(new Generator());
    }};

    void connectDevice(final String deviceMacAddress);
    void disconnectDevice();
    void sendData(final Channel data);//emission / reception
    void sendData(final String data);//emission / reception
    Generator getGenerator();
    ArrayList<Generator> getBackupGenerator();
    boolean getStoreCharged(final int store);
    boolean getAutoConnect();
    void setAutoConnect(boolean state);
    boolean getIsStoresInitialized();
    void setIsStoresInitialized(boolean init);
}
