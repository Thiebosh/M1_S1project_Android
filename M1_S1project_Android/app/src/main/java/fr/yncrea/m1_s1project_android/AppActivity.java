package fr.yncrea.m1_s1project_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import fr.yncrea.m1_s1project_android.bluetooth.BluetoothConstants;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothService;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothMethods;
import fr.yncrea.m1_s1project_android.fragments.ConnectFragment;
import fr.yncrea.m1_s1project_android.fragments.MainBoardFragment;

/**
 * Activité principale : gestion des channels du MIVS
 */
public class AppActivity extends AppCompatActivity implements NavigationHost, BluetoothMethods {

    /*
     * Section NavigationHost
     */

    @Override
    public void loadFragment(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.act_app_fragmentContainer, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    /*
     * Section BluetoothMethods
     */

    /**
     * Establish connection with other device
     */
    @Override
    public void connectDevice(final String deviceMacAddress) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceMacAddress);
        // Attempt to connect to the device
        mBluetoothService.connect(device);
    }

    /*
    @Override
    public void updateHandler(final Handler handler) {
        mBluetoothService.switchHandler(handler);
    }

     */

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
     * Member object for the chat services
     */
    private BluetoothService mBluetoothService = null; // anciennement mChatService

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;


    /**
     * Set up the UI and background operations for chat.
     */
    private void setupBluetooth() { //anciennement setupChat
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBluetoothService = new BluetoothService(new Handler(Looper.myLooper()) {
            // The Handler that gets information back from the BluetoothChatService

            @Override
            public void handleMessage(Message msg) {
                AppCompatActivity activity = AppActivity.this;
                switch (msg.what) {
                    case BluetoothConstants.MESSAGE_STATE_CHANGE:
                        // Updates the status on the action bar.
                        switch (msg.arg1) {
                            case BluetoothService.STATE_CONNECTED:
                                getSupportActionBar().setSubtitle(getString(R.string.title_connected_to, mConnectedDeviceName));
                                //mConversationArrayAdapter.clear();
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                getSupportActionBar().setSubtitle(getString(R.string.title_connecting));
                                break;
                            /*case BluetoothService.STATE_LISTEN:
                                getSupportActionBar().setSubtitle("recherche"); //?
                                break;*/
                            case BluetoothService.STATE_NONE:
                                getSupportActionBar().setSubtitle(getString(R.string.title_not_connected));
                                break;
                        }
                        break;
                    case BluetoothConstants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        //mConversationArrayAdapter.add("Me:  " + writeMessage);
                        break;
                    case BluetoothConstants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                        break;
                    case BluetoothConstants.MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);
                        Toast.makeText(activity, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        //autorise connexion
                        loadFragment(new MainBoardFragment(), false);
                        break;
                    case BluetoothConstants.MESSAGE_TOAST:
                        Toast.makeText(activity, msg.getData().getString(BluetoothConstants.TOAST), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupBluetooth();

                loadFragment(new ConnectFragment(), false);//actualise fragment
            } else {
                // User did not enable Bluetooth or an error occurred
                AppCompatActivity activity = AppActivity.this;
                Toast.makeText(activity, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                activity.finish();
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
        getSupportActionBar().setSubtitle(getString(R.string.title_not_connected));

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (savedInstanceState == null) {
            loadFragment(new ConnectFragment(), false);//charge fragment
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBluetoothAdapter == null) {
            return;
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); //réponse dans onActivityResult()
            // Otherwise, setup the chat session
        } else if (mBluetoothService == null) {
            setupBluetooth();
        }
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
    }

    /*
    @Override
    public void onRestart() {//retour à l'écran principal avant déconnexion
        super.onRestart();
        if (!mFlagChangeActivity) {
            startActivity((new Intent(AppActivity.this, bluetooth_exemple_activity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else mFlagChangeActivity = false;
    }
*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }


    /*
     * Section Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global_connected, menu);
        getMenuInflater().inflate(R.menu.main_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*
        switch(item.getItemId()) {
            case R.id.menu_toBackupActivity:
                mFlagChangeActivity = true;
                startActivity((new Intent(AppActivity.this, BackupFragment.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //MainBoardActivity.this.finish(); //peut remplacer le flag mais pas même retour visuel
                return true;

            case R.id.menu_toConnectActivity:
                mFlagChangeActivity = true;
                startActivity((new Intent(AppActivity.this, bluetooth_exemple_activity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

         */
        return super.onOptionsItemSelected(item);
    }



}