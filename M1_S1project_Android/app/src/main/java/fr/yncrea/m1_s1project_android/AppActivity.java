package fr.yncrea.m1_s1project_android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.Executors;

import fr.yncrea.m1_s1project_android.fragments.BackupFragment;
import fr.yncrea.m1_s1project_android.fragments.ConnectFragment;
import fr.yncrea.m1_s1project_android.fragments.MainBoardFragment;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothConnect;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothConstants;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.interfaces.FragmentSwitcher;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;
import fr.yncrea.m1_s1project_android.models.Scale;
import fr.yncrea.m1_s1project_android.models.Unit;
import fr.yncrea.m1_s1project_android.services.BluetoothService;
import fr.yncrea.m1_s1project_android.services.JsonConverterService;

/**
 * Activité principale : gestion des channels du MIVS
 */
public class AppActivity extends AppCompatActivity implements FragmentSwitcher, BluetoothParent {

    /*
     * Section FragmentSwitcher
     */

    @Override
    public void loadFragment(final Fragment fragment, final boolean addToBackstack) {
        String searched = fragment.getClass().getSimpleName();
        Stack<Fragment> searcher = (Stack<Fragment>) mFragmentStack.clone();
        int position = searcher.size() - 1;//size -> index
        while (!searcher.empty()) {
            if (searcher.pop().getClass().getSimpleName().equals(searched)) break;
            --position;
        }
        if (position != -1) for (int i = mFragmentStack.size(); i > position; --i) mFragmentStack.pop();

        if (addToBackstack) mFragmentStack.push(fragment);//renouvellement du frament stocké

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.act_app_fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentStack.size() < 2) {//besoin d'écran actuel et précédent
            mFragmentStack.clear();
            AppActivity.this.finish();//revient à l'activité précédente
        }
        else {
            mFragmentStack.pop();//ecran actuel
            loadFragment(mFragmentStack.peek(), true);//ecran précédent / can't be a String stack
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //rotation de tous les affichages en mémoire
        //peut tenter de garder indice de rotation et de recréer le fragment que si cet indice est franchi
        //ici, n'appelle que loadfragment(mFragmentStack.peek())
        //vérifier que ces deux cas fonctionnent avec fragment temporaire ou si nécessaire de le stocker à part

        int i = 0;
        Stack<Fragment> copy = new Stack<>();

        Fragment holder;
        while (!mFragmentStack.empty()) {
            holder = mFragmentStack.pop();
            String simpleName = holder.getClass().getSimpleName();
            if (MainBoardFragment.class.getSimpleName().equals(simpleName)) {
                copy.push(new MainBoardFragment());
            }
            else if (BackupFragment.class.getSimpleName().equals(simpleName)) {
                copy.push(new BackupFragment());
            }
            else copy.push(holder);
            i++;
        }
        
        //mFragmentStack.addAll(copy);
        for (int j = 0; j < i; j++) mFragmentStack.push(copy.pop());

        loadFragment(mFragmentStack.peek(), true);
    }

    /*
     * Section BluetoothParent
     */

    boolean mAutoConnect = true;
    boolean mIsStoresInitialized = false;

    /**
     * Establish connection with other device
     */
    @Override
    public void connectDevice(final String deviceName, final String deviceMacAddress) {
        if (mBluetoothService != null && mBluetoothAdapter != null) {
            mConnectedDeviceName = deviceName;
            mBluetoothService.connect(mBluetoothAdapter.getRemoteDevice(deviceMacAddress));
        }
    }

    @Override
    public void disconnectDevice() {
        if (mBluetoothService != null) mBluetoothService.stop();
    }

    @Override
    public void sendData(final Channel data) {
        sendData(JsonConverterService.extractJsonData(data));
    }

    @Override
    public void sendData(final String data) {
        Log.d("testy", "send : "+data);
        if (mBluetoothService != null) mBluetoothService.send(data);
    }

    @Override
    public boolean getAutoConnect() {
        return mAutoConnect;
    }

    @Override
    public void setAutoConnect(boolean state) {
        mAutoConnect = state;
    }

    @Override
    public boolean getIsStoresInitialized() {
        return mIsStoresInitialized;
    }

    @Override
    public void setIsStoresInitialized(boolean init) {
        mIsStoresInitialized = init;
    }

    /*
     * Section Bluetooth
     */

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the bluetooth services
     */
    private BluetoothService mBluetoothService = null;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;


    /**
     * Set up the UI and background operations for chat.
     */
    private void setupBluetooth() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBluetoothService = new BluetoothService(
                getResources().getStringArray(R.array.blt_commands_array),
                new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // The Handler that gets information back from the BluetoothChatService
                String str;
                switch (msg.what) {
                    case BluetoothConstants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothService.STATE_NONE:
                                str = getString(R.string.blt_not_connected);
                                break;

                            case BluetoothService.STATE_CONNECTING:
                                str = getString(R.string.frag_conn_connecting_device, mConnectedDeviceName);
                                if (mFragmentStack.peek() instanceof BluetoothConnect) {
                                    ((BluetoothConnect) mFragmentStack.peek()).updateTitle(str);
                                }
                                str = getString(R.string.blt_connecting);
                                break;

                            case BluetoothService.STATE_FAILED:
                                str = getString(R.string.frag_conn_connexion_failed, mConnectedDeviceName);
                                if (mFragmentStack.peek() instanceof BluetoothConnect) {
                                    ((BluetoothConnect) mFragmentStack.peek()).updateTitle(str);
                                }
                                str = getString(R.string.blt_not_connected);
                                break;

                            case BluetoothService.STATE_CONNECTED:
                                str = getString(R.string.frag_conn_loading_data, mConnectedDeviceName);
                                if (mFragmentStack.peek() instanceof BluetoothConnect) {
                                    ((BluetoothConnect) mFragmentStack.peek()).updateTitle(str);
                                }
                                str = getString(R.string.blt_connected, mConnectedDeviceName);
                                sendData(getString(R.string.blt_command_init_main));//requete pour les données
                                break;

                            case BluetoothService.STATE_DISCONNECT:
                                loadFragment(new ConnectFragment(), true);//charge premier fragment
                                str = getString(R.string.blt_not_connected);
                                BluetoothParent.mGenerator.getChannelList().clear();
                                BluetoothParent.mIsStores.clear();
                                BluetoothParent.mBackupGenerators.clear();
                                break;

                            default:
                                str = null;
                        }
                        if (str != null) {
                            Objects.requireNonNull(getSupportActionBar()).setSubtitle(str);
                        }
                        break;

                    case BluetoothConstants.MESSAGE_RECEIVE:
                        str = msg.getData().getString(BluetoothConstants.RECEIVE);
                        Log.d("testy", "recieve : "+str);

                        if (str.startsWith(Generator.ATTRIBUTE_CHANNEL_LIST, 2)) {//init_main
                            Generator storage = JsonConverterService.createJsonObject(str);
                            if (storage == null) {
                                sendData(getString(R.string.blt_command_init_main));//requête pour les données
                                Toast.makeText(AppActivity.this, getString(R.string.blt_reception_failed), Toast.LENGTH_SHORT).show();
                                break;
                            }
                            mGenerator.setChannelList(storage.getChannelList());
                            loadFragment(new MainBoardFragment(), true);//peut revenir à l'écran de connexion
                            break;
                        }
                        else if (str.startsWith(getString(R.string.blt_command_init_backup), 2)){
                            ArrayList<Integer> array = JsonConverterService.extractIntegerArray(str, getString(R.string.blt_command_init_backup));
                            if (array != null) {
                                setIsStoresInitialized(true);
                                for (int value : array) {
                                    BluetoothParent.mIsStores.add(value == 1);
                                    BluetoothParent.mBackupGenerators.add(new Generator());
                                }
                                if (mFragmentStack.peek() instanceof BackupFragment) {
                                    ((BluetoothChildren) mFragmentStack.peek()).applyChanges(null, JsonConverterService.APPLYING_GLOBAL);
                                }
                            }
                            break;
                        }
                        else if (str.startsWith(getString(R.string.blt_command_backup_get), 2)){
                            int store_number;
                            try {
                                store_number = Integer.parseInt(str.substring(2 + getString(R.string.blt_command_backup_get).length(),
                                        str.indexOf(JsonConverterService.OBJECT_SEPARATOR) - 1));
                            }
                            catch (Exception ignore) {
                                break;
                            }

                            str = str.replace(getString(R.string.blt_command_backup_get)+store_number, Generator.ATTRIBUTE_CHANNEL_LIST);
                            Generator storage = JsonConverterService.createJsonObject(str);
                            if(storage != null){
                                mIsStores.set(store_number, true);
                                mBackupGenerators.get(store_number).setChannelList(storage.getChannelList());
                            }
                            ((BluetoothChildren) mFragmentStack.peek()).applyChanges(storage, store_number);
                            break;
                        }
                        else if (str.startsWith(getString(R.string.blt_command_backup_save))) {
                            int store_number;
                            try {
                                store_number = Integer.parseInt(str.substring(getString(R.string.blt_command_backup_save).length()));
                            }
                            catch (Exception ignore) {
                                break;
                            }
                            if (!BluetoothParent.mBackupGenerators.isEmpty()) {
                                BluetoothParent.mBackupGenerators.set(store_number, (new Generator()).setChannelList(BluetoothParent.mGenerator.getChannelList()).setAllChannelActive(false));
                                BluetoothParent.mIsStores.set(store_number, true);
                                if (mFragmentStack.peek() instanceof BackupFragment) {
                                    ((BluetoothChildren) mFragmentStack.peek()).applyChanges(null, JsonConverterService.APPLYING_GLOBAL);
                                }
                            }
                            else {
                                sendData(getString(R.string.blt_command_init_backup));

                                Executors.newSingleThreadExecutor().execute(() -> {
                                    while (BluetoothParent.mBackupGenerators.isEmpty()) {
                                        try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    BluetoothParent.mBackupGenerators.set(store_number, (new Generator()).setChannelList(BluetoothParent.mGenerator.getChannelList()).setAllChannelActive(false));
                                    BluetoothParent.mIsStores.set(store_number, true);
                                    if (mFragmentStack.peek() instanceof BackupFragment) {
                                        ((BluetoothChildren) mFragmentStack.peek()).applyChanges(null, JsonConverterService.APPLYING_GLOBAL);
                                    }
                                });
                            }
                        }
                        else if (str.startsWith(getString(R.string.blt_command_backup_load))) {
                            int store_number;
                            try {
                                store_number = Integer.parseInt(str.substring(getString(R.string.blt_command_backup_load).length()));
                            }
                            catch (Exception ignore) {
                                break;
                            }
                            if (!BluetoothParent.mBackupGenerators.isEmpty()) {
                                if (!BluetoothParent.mBackupGenerators.get(store_number).getChannelList().isEmpty()) {
                                    BluetoothParent.mGenerator.setChannelList(BluetoothParent.mBackupGenerators.get(store_number).getChannelList());
                                    if (mFragmentStack.peek() instanceof MainBoardFragment) {
                                        loadFragment(new MainBoardFragment(), true);
                                    }
                                } else {
                                    sendData(getString(R.string.blt_command_backup_get) + store_number);

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        while (BluetoothParent.mBackupGenerators.get(store_number).getChannelList().isEmpty()) {
                                            try {
                                                Thread.sleep(10);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        BluetoothParent.mGenerator.setChannelList(BluetoothParent.mBackupGenerators.get(store_number).getChannelList());
                                        if (mFragmentStack.peek() instanceof MainBoardFragment) {
                                            loadFragment(new MainBoardFragment(), true);
                                        }
                                    });
                                }
                            }
                            else {
                                sendData(getString(R.string.blt_command_init_backup));

                                Executors.newSingleThreadExecutor().execute(() -> {
                                    while (BluetoothParent.mBackupGenerators.isEmpty()) {
                                        try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!BluetoothParent.mBackupGenerators.get(store_number).getChannelList().isEmpty()) {
                                        BluetoothParent.mGenerator.setChannelList(BluetoothParent.mBackupGenerators.get(store_number).getChannelList());
                                        if (mFragmentStack.peek() instanceof MainBoardFragment) {
                                            loadFragment(new MainBoardFragment(), true);
                                        }
                                    } else {
                                        sendData(getString(R.string.blt_command_backup_get) + store_number);

                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            while (BluetoothParent.mBackupGenerators.get(store_number).getChannelList().isEmpty()) {
                                                try {
                                                    Thread.sleep(10);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            BluetoothParent.mGenerator.setChannelList(BluetoothParent.mBackupGenerators.get(store_number).getChannelList());
                                            if (mFragmentStack.peek() instanceof MainBoardFragment) {
                                                loadFragment(new MainBoardFragment(), true);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                        else if (str.startsWith(getString(R.string.blt_command_backup_delete))) {
                            int store_number;
                            try {
                                store_number = Integer.parseInt(str.substring(getString(R.string.blt_command_backup_delete).length()));
                            }
                            catch (Exception ignore) {
                                break;
                            }
                            if (!BluetoothParent.mBackupGenerators.isEmpty()) {
                                BluetoothParent.mBackupGenerators.get(store_number).setChannelList(new ArrayList<>());
                                BluetoothParent.mIsStores.set(store_number, false);
                                if (mFragmentStack.peek() instanceof BackupFragment) {
                                    loadFragment(new BackupFragment(), true);
                                }
                            }
                            else {
                                sendData(getString(R.string.blt_command_init_backup));

                                Executors.newSingleThreadExecutor().execute(() -> {
                                    while (BluetoothParent.mBackupGenerators.isEmpty()) {
                                        try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    BluetoothParent.mBackupGenerators.get(store_number).setChannelList(new ArrayList<>());
                                    BluetoothParent.mIsStores.set(store_number, false);
                                    if (mFragmentStack.peek() instanceof BackupFragment) {
                                        loadFragment(new BackupFragment(), true);
                                    }
                                });
                            }
                        }

                        // 3 if suivants servent à communication entre 2 android
                        else if (str.equals(getString(R.string.blt_command_init_main))) {
                            Generator init = new Generator().setChannelList(new ArrayList<>(Arrays.asList(
                                    (new Channel()).setId(0).setCurrentValue(2.4),
                                    (new Channel()).setId(2).setUnit(Unit.I).setCurrentValue(-0.003),
                                    (new Channel()).setId(3).setActive(true),
                                    (new Channel()).setId(4).setCurrentValue(0.14),
                                    (new Channel()).setId(12).setCurrentValue(-1))));
                            sendData(JsonConverterService.createJsonString(init));
                            break;
                        }
                        else if (str.equals(getString(R.string.blt_command_init_backup))) {
                            String init = "{\""+getString(R.string.blt_command_init_backup)+"\":[0,0,1,0]}";
                            sendData(init);
                        }
                        else if (str.startsWith(getString(R.string.blt_command_backup_get))) {
                            int store_number;
                            try {
                                store_number = Integer.parseInt(str.substring(getString(R.string.blt_command_backup_get).length()));
                            }
                            catch (Exception ignore) {
                                break;
                            }

                            if (store_number == 2) {
                                String c1 = JsonConverterService.createJsonString((new Channel()).setId(0).setCurrentValue(0.9).setMinValue(-2).setMaxValue(5).setScale(Scale.m));
                                String c3 = JsonConverterService.createJsonString((new Channel()).setId(2).setCurrentValue(-5.1).setMinValue(-6).setMaxValue(5).setScale(Scale.m));
                                String c4 = JsonConverterService.createJsonString((new Channel()).setId(3).setUnit(Unit.I).setCurrentValue(3.7).setMinValue(0).setMaxValue(500).setScale(Scale.u));
                                sendData("{\"" + getString(R.string.blt_command_backup_get) + "0\":["+c1+","+c3+","+c4+"]}");
                            }
                        }

                        else {//réception d'objet partiel
                            int index = JsonConverterService.applyJsonData(mGenerator, str);
                            if (index == JsonConverterService.ERROR_CONVERSION) break;//error
                            ((BluetoothChildren) mFragmentStack.peek()).applyChanges(mGenerator, index);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) setupBluetooth();
            else {
                // User did not enable Bluetooth or an error occurred
                int str = R.string.blt_disabled_exit_app;
                Toast.makeText(AppActivity.this, str, Toast.LENGTH_SHORT).show();
                AppActivity.this.finish();
            }
        }
    }


    /*
     * Section Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // define all actions once and show / hide depending of the fragment

        //avoid NonConstantResourceId warning
        final int mainBoard = R.id.menu_toMainBoard;
        final int backup = R.id.menu_toBackup;
        final int disconnect = R.id.menu_disconnect;

        switch(item.getItemId()) {
            case mainBoard:
                loadFragment(new MainBoardFragment(), true);
                return true;

            case backup:
                loadFragment(new BackupFragment(), true);
                return true;

            case disconnect:
                loadFragment(new ConnectFragment(), true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * Section Cycle de vie
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app);

        String str = getString(R.string.blt_not_connected);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(str);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) return;

        // If BT is not on, request that it be enabled.
        // setupBluetooth() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); //réponse dans onActivityResult()
            // Otherwise, setup the bluetooth session
        }
        else if (mBluetoothService == null) setupBluetooth();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBluetoothService != null && mBluetoothService.getState() == BluetoothService.STATE_NONE) {
            mBluetoothService.start();
        }

        loadFragment(new ConnectFragment(), true);//charge premier fragment
        setAutoConnect(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        disconnectDevice();
    }
}