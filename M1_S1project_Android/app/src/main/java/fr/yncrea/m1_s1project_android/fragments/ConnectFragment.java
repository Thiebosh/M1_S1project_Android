package fr.yncrea.m1_s1project_android.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
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

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothMethods;

public class ConnectFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        //si était connecté, déconnecte
        ((BluetoothMethods) Objects.requireNonNull(getActivity())).disconnectDevice();

        // Find and set up the ListView for paired devices
        ListView pairedListView = view.findViewById(R.id.frag_conn_listView_paired_devices);

        // Initialize array adapter for already paired devices
        ArrayAdapter<String> devicesAdapter = new ArrayAdapter<>(getContext(), R.layout.device_name);
        pairedListView.setAdapter(devicesAdapter);

        //The on-click listener for all devices in the ListViews
        AdapterView.OnItemClickListener deviceClickListener = (av, v, arg2, arg3) -> {
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            String deviceName = info.substring(0, info.length() - 18);//mac adress + space

            // Notify user during connexion test
            String str = getString(R.string.frag_conn_connecting_device, deviceName);
            ((TextView) view.findViewById(R.id.frag_conn_textView_title)).setText(str);

            // Connexion test : if success, handler will change fragment
            ((BluetoothMethods) Objects.requireNonNull(getActivity())).connectDevice(address);
        };
        pairedListView.setOnItemClickListener(deviceClickListener);

        // Get a set of currently paired devices from a local Bluetooth adapter
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            view.findViewById(R.id.frag_conn_textView_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                devicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        else devicesAdapter.add(getString(R.string.frag_conn_none_paired));

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //définit éléments visibles du menu pour ce fragment
        menu.findItem(R.id.menu_disconnect).setVisible(false);
        menu.findItem(R.id.menu_toMainBoard).setVisible(false);
        menu.findItem(R.id.menu_toBackup).setVisible(false);
    }
}
