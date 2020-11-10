package fr.yncrea.m1_s1project_android.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.Set;

import fr.yncrea.m1_s1project_android.NavigationHost;
import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothMethods;

public class ConnectFragment extends Fragment {
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /*
     * Newly discovered devices
     *
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    private View mView;


    /*
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     *
    private BroadcastReceiver mReceiver;

     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //The on-click listener for all devices in the ListViews
        AdapterView.OnItemClickListener deviceClickListener = (av, v, arg2, arg3) -> {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Set result and navigate to the next Fragment
            ((BluetoothMethods) Objects.requireNonNull(getActivity())).connectDevice(address);
            ((NavigationHost) getActivity()).navigateTo(new MainBoardFragment(), false);
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

        /*
        // Initialize array adapter for newly discovered devices
        mNewDevicesArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.device_name);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = view.findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(deviceClickListener);

        // Initialize the button to perform device discovery with the BluetoothAdapter
        ((Button) view.findViewById(R.id.button_scan)).setOnClickListener(v -> {
            if (mBluetoothAdapter.isDiscovering()) {
                // If we're already discovering, stop it
                mBluetoothAdapter.cancelDiscovery();

                ((TextView) mView.findViewById(R.id.bluetoothStatus)).setText(R.string.select_device);
            }
            else {
                // Request discover from BluetoothAdapter
                mBluetoothAdapter.startDiscovery();

                // Turn on sub-title for new devices
                view.findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
                ((TextView) mView.findViewById(R.id.bluetoothStatus)).setText(R.string.scanning);
            }
        });

        mView = view;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // If it's already paired, skip it, because it's been listed already
                    if (device != null && device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    ((TextView) mView.findViewById(R.id.bluetoothStatus)).setText(R.string.select_device);
                    if (mNewDevicesArrayAdapter.getCount() == 0) {
                        String noDevices = getResources().getText(R.string.none_found).toString();
                        mNewDevicesArrayAdapter.add(noDevices);
                    }
                }
            }
        };

        // Register for broadcasts when a device is discovered
        Objects.requireNonNull(getActivity()).registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        // Register for broadcasts when discovery has finished
        Objects.requireNonNull(getActivity()).registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        */


        //setHasOptionsMenu(true);//active le onPrepareOptionsMenu

        /*
        ((Button) view.findViewById(R.id.fra_con_connectButton)).setOnClickListener(v -> {
            ((NavigationHost) getActivity()).navigateTo(new MainBoardFragment(), false); // Navigate to the next Fragment
        });
         */

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        //Objects.requireNonNull(getActivity()).unregisterReceiver(mReceiver);
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

    /*
     * Section Bluetooth
     */


}
