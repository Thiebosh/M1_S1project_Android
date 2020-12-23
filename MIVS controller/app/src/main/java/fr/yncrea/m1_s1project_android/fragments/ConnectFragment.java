package fr.yncrea.m1_s1project_android.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.Set;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothConnect;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothConstants;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;

import static android.content.Context.MODE_PRIVATE;

public class ConnectFragment extends Fragment implements BluetoothConnect {

    private TextView mTitle;

    /*
     * Section BluetoothConnect
     */

    @Override
    public void updateTitle(String title) {
        mTitle.setText(title);
    }

    /*
     * Section Menu
     */

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //définit éléments visibles du menu pour ce fragment
        menu.findItem(R.id.menu_disconnect).setVisible(false);
        menu.findItem(R.id.menu_toMainBoard).setVisible(false);
        menu.findItem(R.id.menu_toBackup).setVisible(false);
    }


    /*
     * Section Cycle de vie
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        //si était connecté, déconnecte
        ((BluetoothParent) Objects.requireNonNull(getActivity())).disconnectDevice();

        //si premier lancement et adresse en mémoire, autoconnect
        SharedPreferences loginPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(BluetoothConstants.PREF_SLOT_ACCESS, MODE_PRIVATE);
        SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
        if (((BluetoothParent) getActivity()).getAutoConnect() && loginPreferences.getBoolean(BluetoothConstants.PREF_IS_SAVED, false)) {
            ((BluetoothParent) getActivity()).connectDevice(loginPreferences.getString(BluetoothConstants.PREF_ACCESS_NAME, ""),
                                                            loginPreferences.getString(BluetoothConstants.PREF_ACCESS_ADDRESS, ""));
            ((BluetoothParent) getActivity()).setAutoConnect(false);
        }

        mTitle = view.findViewById(R.id.frag_conn_textView_title);

        // Find and set up the ListView for paired devices
        ListView pairedListView = view.findViewById(R.id.frag_conn_listView_paired_devices);

        // Initialize array adapter for already paired devices
        ArrayAdapter<String> devicesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        pairedListView.setAdapter(devicesAdapter);

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

        //The on-click listener for all devices in the ListViews
        pairedListView.setOnItemClickListener((av, v, arg2, arg3) -> {
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            String deviceName = info.substring(0, info.length() - 18);//mac adress + space

            // Notify user during connexion test
            mTitle.setText(getString(R.string.frag_conn_connecting_device, deviceName));

            // Connexion test : if success, handler will change fragment
            ((BluetoothParent) getActivity()).connectDevice(deviceName, address);
            ((BluetoothParent) getActivity()).setAutoConnect(false);

            //remember mac adress
            if (((CheckBox) view.findViewById(R.id.frag_conn_checkbox_remember)).isChecked()) {
                loginPrefsEditor.putBoolean(BluetoothConstants.PREF_IS_SAVED, true);
                loginPrefsEditor.putString(BluetoothConstants.PREF_ACCESS_ADDRESS, address);
                loginPrefsEditor.putString(BluetoothConstants.PREF_ACCESS_NAME, deviceName);
            }
            else loginPrefsEditor.clear();
            loginPrefsEditor.apply();
        });

        return view;
    }
}
