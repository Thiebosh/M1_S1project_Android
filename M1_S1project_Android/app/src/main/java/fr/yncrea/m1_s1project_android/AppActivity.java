package fr.yncrea.m1_s1project_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;
import java.util.Stack;

import fr.yncrea.m1_s1project_android.bluetooth.BluetoothConstants;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothService;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothMethods;
import fr.yncrea.m1_s1project_android.fragments.BackupFragment;
import fr.yncrea.m1_s1project_android.fragments.ConnectFragment;
import fr.yncrea.m1_s1project_android.fragments.FragmentSwitcher;
import fr.yncrea.m1_s1project_android.fragments.MainBoardFragment;

/**
 * Activité principale : gestion des channels du MIVS
 */
public class AppActivity extends AppCompatActivity implements FragmentSwitcher, BluetoothMethods {

    /*
     * Section FragmentSwitcher
     */

    @Override
    public void loadFragment(Fragment fragment, boolean addToBackstack) {
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

    /*
     * Section BluetoothMethods
     */

    /**
     * Establish connection with other device
     */
    @Override
    public void connectDevice(final String deviceMacAddress) {
        if (mBluetoothService != null && mBluetoothAdapter != null) {
            mBluetoothService.connect(mBluetoothAdapter.getRemoteDevice(deviceMacAddress));
        }
    }

    @Override
    public void disconnectDevice() {
        if (mBluetoothService != null) mBluetoothService.stop();
    }

    @Override
    public void sendData(final String str) {
        if (mBluetoothService != null) mBluetoothService.write(str);
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

    /*
     * String buffer for outgoing messages
     */
    //private StringBuffer mOutStringBuffer;


    /**
     * Set up the UI and background operations for chat.
     */
    private void setupBluetooth() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        //getApplicationContext().getString(R.string.app_name);
        //getResources().getString(R.string.app_name);
        mBluetoothService = new BluetoothService(getResources(), new Handler(Looper.myLooper()) {
            // The Handler that gets information back from the BluetoothChatService
            @Override
            public void handleMessage(Message msg) {
                String str;
                switch (msg.what) {
                    case BluetoothConstants.MESSAGE_STATE_CHANGE:
                        // Updates the status on the action bar.
                        switch (msg.arg1) {
                            case BluetoothService.STATE_CONNECTED:
                                str = getString(R.string.blt_connected, mConnectedDeviceName);
                                //mConversationArrayAdapter.clear();
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                str = getString(R.string.blt_connecting);
                                break;
                            case BluetoothService.STATE_NONE:
                                str = getString(R.string.blt_not_connected);
                                break;
                            default:
                                str = null;
                        }
                        if (str != null) {
                            Objects.requireNonNull(getSupportActionBar()).setSubtitle(str);
                        }
                        break;

                    case BluetoothConstants.MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);
                        str = getString(R.string.blt_connected, mConnectedDeviceName);
                        Toast.makeText(AppActivity.this, str, Toast.LENGTH_SHORT).show();
                        //autorise connexion
                        loadFragment(new MainBoardFragment(), true);//peut revenir à l'écran de connexion
                        break;

                    case BluetoothConstants.MESSAGE_TOAST:
                        str = msg.getData().getString(BluetoothConstants.TOAST);
                        Toast.makeText(AppActivity.this, str, Toast.LENGTH_SHORT).show();
                        break;
                        //si perd connexion, déconnecter

                    /*
                    case BluetoothConstants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        str = new String(writeBuf);
                        Toast.makeText(AppActivity.this, str, Toast.LENGTH_SHORT).show();
                        break;
                    */
                    case BluetoothConstants.MESSAGE_READ:
                        str = msg.getData().getString(BluetoothConstants.READ);
                        Toast.makeText(AppActivity.this, str, Toast.LENGTH_SHORT).show();
                        //byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        //str = new String(readBuf, 0, msg.arg1);
                        //Log.d("testy"+msg.arg1, msg.toString());
                        //Log.d("testy reception", str);
                        //Log.d("testy", ".");
                        //Toast.makeText(AppActivity.this, str, Toast.LENGTH_SHORT).show();
                        //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + str);
                        break;
                }
            }
        });

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer();
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
     * Section Cycle de vie
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity);
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

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBluetoothService.start();
            }
        }

        loadFragment(new ConnectFragment(), true);//charge premier fragment
    }


    @Override
    public void onPause() {
        super.onPause();
        disconnectDevice();
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

        //avoid NonConstantResourceId warning / error
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

}