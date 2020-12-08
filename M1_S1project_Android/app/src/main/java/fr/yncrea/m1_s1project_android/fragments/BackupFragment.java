package fr.yncrea.m1_s1project_android.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.recyclers.BackupConfigAdapter;
import fr.yncrea.m1_s1project_android.recyclers.BackupSlotAdapter;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
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

        Generator tmp = ((BluetoothParent) getActivity()).getGenerator();
        ArrayList<Generator> tmpList = new ArrayList<>(Arrays.asList(tmp, new Generator()));

        RecyclerView oneConfigRecycler = view.findViewById(R.id.frag_back_recycler_config);
        BackupConfigAdapter oneConfigAdapter = new BackupConfigAdapter(tmp.getChannelList());
        oneConfigRecycler.setAdapter(oneConfigAdapter);

        RecyclerView slotRecycler = view.findViewById(R.id.frag_back_recycler_slots);
        BackupSlotAdapter slotsAdapter = new BackupSlotAdapter(oneConfigAdapter, tmpList);
        slotRecycler.setAdapter(slotsAdapter);
        slotRecycler.addItemDecoration(new DividerItemDecoration(slotRecycler.getContext(), DividerItemDecoration.VERTICAL));
        slotRecycler.addItemDecoration(new DividerItemDecoration(slotRecycler.getContext(), DividerItemDecoration.HORIZONTAL));

        view.findViewById(R.id.frag_back_button_save).setOnClickListener(v -> {
            String text = "enregistrer nouvelle config à l'emplacement "+slotsAdapter.getFocusedIndex();
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
            //avec tous les canaux désactivés
        });

        view.findViewById(R.id.frag_back_button_load).setOnClickListener(v -> {
            String text = "changer config de l'emplacement "+slotsAdapter.getFocusedIndex();
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
            //avec tous les canaux désactivés
        });

        view.findViewById(R.id.frag_back_button_delete).setOnClickListener(v -> {
            String text = "supprimer config de l'emplacement "+slotsAdapter.getFocusedIndex();
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        });

        return view;
    }
}