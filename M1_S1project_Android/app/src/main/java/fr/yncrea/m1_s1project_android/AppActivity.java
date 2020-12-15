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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

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
        Log.d("testy send", ""+data);
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
        mBluetoothService = new BluetoothService(new Handler(Looper.myLooper()) {
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
                                str = getString(R.string.frag_conn_retrieve_data, mConnectedDeviceName);
                                if (mFragmentStack.peek() instanceof BluetoothConnect) {
                                    ((BluetoothConnect) mFragmentStack.peek()).updateTitle(str);
                                }
                                str = getString(R.string.blt_connected, mConnectedDeviceName);
                                sendData("initPlz");//requete pour les données
                                break;

                            case BluetoothService.STATE_DISCONNECT:
                                loadFragment(new ConnectFragment(), true);//charge premier fragment
                                str = getString(R.string.blt_not_connected);
                                BluetoothParent.mGenerator.getChannelList().clear();
                                BluetoothParent.mIsStores.clear();
                                BluetoothParent.mIsStores.addAll(Arrays.asList(true, true, false));
                                BluetoothParent.mBackupGenerator.clear();
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
                        int index;

                        if (str.startsWith("channelList", 2)) {
                            Generator storage = JsonConverterService.createJsonObject(str);
                            if (storage == null) {
                                sendData("initPlz");//requête pour les données
                                Toast.makeText(AppActivity.this, "Erreur de réception : nouvel essai", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            mGenerator.setChannelList(storage.getChannelList());
                            loadFragment(new MainBoardFragment(), true);//peut revenir à l'écran de connexion
                            break;
                        }
                        else if(str.startsWith("getStores", 2)){
                            setIsStoresInitialized(true);
                            JSONObject jsonObject = null;
                            JSONArray jArray = null;
                            try {
                                jsonObject = new JSONObject(str);
                                Log.d("testy", "etape 1");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                jArray = jsonObject.getJSONArray("getStores");
                                Log.d("testy", "etape 2");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (jArray != null) {
                                for (int i = 0; i < jArray.length(); i++){
                                    try {
                                        mIsStores.add(jArray.getInt(i)==1);
                                        Log.d("testy", "jsonObject : "+i+" = "+ mIsStores.get(i));
                                        //listdata.add(jsonObject.getString(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            loadFragment(new BackupFragment(), true);
                            break;
                            //for(int i = 0; i < )
                        }
                        else if(str.startsWith("store", 2)){
                            int store_number = Character.getNumericValue(str.charAt(7));
                            Log.d("testy", "store received : "+str);
                            str = str.replace("store"+store_number, "channelList");
                            Log.d("testy", str);
                            Generator storage = JsonConverterService.createJsonObject(str);
                            if(storage != null){
                                mIsStores.set(store_number, true);
                                mBackupGenerator.get(store_number).setChannelList(storage.getChannelList());
                                Log.d("testy", "store charged"+storage.getChannelList().size());

                            }
                            ((BluetoothChildren) mFragmentStack.peek()).applyChanges(storage, store_number);
                            break;

                        }
                        // le else if qui suit ne sert que pour l'utilisation entre 2 android
                        else if (str.equals("initPlz")) {
                            //TMP : simule envoi des données de l'arduino
                            String c1 = "{\"id\":0,\"isActive\":false,\"currentValue\":9.6,\"unit\":V,\"minVoltValue\":-2,\"maxVoltValue\":5,\"scale\":m},";
                            String c2 = "{\"id\":1,\"isActive\":true,\"currentValue\":3.8,\"unit\":I,\"minAmpereValue\":-6,\"maxAmpereValue\":5,\"scale\":u},";
                            String c3 = "{\"id\":2,\"isActive\":false,\"currentValue\":6.9,\"unit\":V,\"minVoltValue\":5,\"maxVoltValue\":10,\"scale\":m},";
                            String c4 = "{\"id\":3,\"isActive\":false,\"currentValue\":1.4,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":5,\"scale\":m},";
                            String c5 = "{\"id\":4,\"isActive\":false,\"currentValue\":6.7,\"unit\":I,\"minAmpereValue\":2,\"maxAmpereValue\":7,\"scale\":m},";
                            String c6 = "{\"id\":5,\"isActive\":true,\"currentValue\":2.587,\"unit\":V,\"minVoltValue\":-0.54,\"maxVoltValue\":5,\"scale\":_},";
                            String c7 = "{\"id\":6,\"isActive\":false,\"currentValue\":1.02,\"unit\":I,\"minAmpereValue\":0.5,\"maxAmpereValue\":1.50,\"scale\":u},";
                            String c8 = "{\"id\":7,\"isActive\":true,\"currentValue\":0.25,\"unit\":V,\"minVoltValue\":0,\"maxVoltValue\":1,\"scale\":_}";
                            String init = "{\"channelList\":["+c1+c2+c3+c4+c5+c6+c7+c8+"]}";

                            Generator storage = JsonConverterService.createJsonObject(init);
                            mGenerator.setChannelList(Objects.requireNonNull(storage).getChannelList());
                            loadFragment(new MainBoardFragment(), true);//peut revenir à l'écran de connexion
                            break;
                        }
                        else {
                            index = JsonConverterService.applyJsonData(mGenerator, str);
                            if (index == -10) break;//error
                        }

                        ((BluetoothChildren) mFragmentStack.peek()).applyChanges(mGenerator, index);
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