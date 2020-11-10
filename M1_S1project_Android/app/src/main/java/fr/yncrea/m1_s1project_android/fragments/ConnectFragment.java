package fr.yncrea.m1_s1project_android.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.Set;

import fr.yncrea.m1_s1project_android.AppActivity;
import fr.yncrea.m1_s1project_android.NavigationHost;
import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothConstants;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothMethods;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothService;

public class ConnectFragment extends Fragment {
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        /*
        // Define the handler for this fragment
        ((BluetoothMethods) Objects.requireNonNull(getActivity())).updateHandler(new Handler(Looper.myLooper()) {
            // The Handler that gets information back from the BluetoothChatService

            @Override
            public void handleMessage(Message msg) {
                Activity activity = getActivity();
                switch (msg.what) {
                    case BluetoothConstants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothService.STATE_CONNECTED:
                                setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                                //mConversationArrayAdapter.clear();
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                setStatus(getString(R.string.title_connecting));
                                break;
                            case BluetoothService.STATE_LISTEN:
                            case BluetoothService.STATE_NONE:
                                setStatus(getString(R.string.title_not_connected));
                                break;
                        }
                        break;
                    case BluetoothConstants.MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        String connectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);
                        Toast.makeText(activity, "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();
                        AppActivity.setConnectedDeviceName(connectedDeviceName);
                        //autorise connexion
                        ((NavigationHost) getActivity()).loadFragment(new MainBoardFragment(), false);
                        break;
                    case BluetoothConstants.MESSAGE_TOAST:
                        Toast.makeText(activity, msg.getData().getString(BluetoothConstants.TOAST), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
         */

        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //The on-click listener for all devices in the ListViews
        AdapterView.OnItemClickListener deviceClickListener = (av, v, arg2, arg3) -> {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            String deviceName = info.substring(0, info.length() - 18);//mac adress + space

            // Notify user during connexion test
            ((TextView) view.findViewById(R.id.bluetoothStatus)).setText("Try to connect to " + deviceName);

            // Connexion test : if success, handler will change fragment
            ((BluetoothMethods) Objects.requireNonNull(getActivity())).connectDevice(address);

        };


        // Initialize array adapter for already paired devices
        ArrayAdapter<String> pairedDevicesArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = view.findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(deviceClickListener);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            view.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }

        //setHasOptionsMenu(true);//active le onPrepareOptionsMenu


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }


    /*
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_admin_logout);
        if (item!=null) item.setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.secure_connect_scan) {// Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        }
        return false;
    }

     */
}
