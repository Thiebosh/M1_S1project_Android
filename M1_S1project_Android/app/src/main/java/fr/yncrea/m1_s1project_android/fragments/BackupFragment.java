package fr.yncrea.m1_s1project_android.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.RecyclerView.BackupAdapterView;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Generator;

/**
 * Activité secondaire : gestion du module de sauvegarde du MIVS
 */
public class BackupFragment extends Fragment implements BluetoothChildren {

    /*
     * Section BluetoothChildren
     */

    @Override
    public void applyChanges(Generator generator, int index) {

    }


    /*
     * Section Menu
     */

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //définit éléments visibles du menu pour ce fragment
        menu.findItem(R.id.menu_disconnect).setVisible(true);
        menu.findItem(R.id.menu_toMainBoard).setVisible(true);
        menu.findItem(R.id.menu_toBackup).setVisible(false);
    }


    /*
     * Section Cycle de vie
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        String[] slotArray = {"Slot 0","Slot 1","Slot 2","Slot 3", "Slot 4","Slot 5","Slot 6","Slot 7", "Slot 0","Slot 1","Slot 2","Slot 3", "Slot 4","Slot 5","Slot 6","Slot 7", "Slot 0","Slot 1","Slot 2","Slot 3", "Slot 4","Slot 5","Slot 6","Slot 7"};

        ArrayAdapter bAdapter = new ArrayAdapter<>(getContext(), R.layout.slot_item, slotArray);

        ListView slotList = (ListView) view.findViewById(R.id.defaultTextview);
        slotList.setAdapter(bAdapter);
        bAdapter.notifyDataSetChanged();
        slotList.setOnItemClickListener((AdapterView<?> parent, View view1, int position, long id) -> {
            Log.d("testy", ""+id);
        });

        /*Activity activity = Objects.requireNonNull(getActivity());
        RecyclerView slotRecycler = view.findViewById(R.id.slotRecycler);
        BackupAdapterView backupAdapter = new BackupAdapterView();
        slotRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        slotRecycler.setAdapter(backupAdapter);
        backupAdapter.notifyDataSetChanged();*/

        return view;
    }
}