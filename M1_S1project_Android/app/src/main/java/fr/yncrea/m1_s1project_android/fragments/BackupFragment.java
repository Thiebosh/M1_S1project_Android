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
import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
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

        Generator tmp = ((BluetoothParent) Objects.requireNonNull(getActivity())).getGenerator();
        ArrayList<Generator> tmpList = new ArrayList<>(Arrays.asList(tmp, new Generator()));

        RecyclerView oneConfigRecycler = view.findViewById(R.id.frag_back_recycler_config);
        oneConfigRecycler.addItemDecoration(new DividerItemDecoration(oneConfigRecycler.getContext(), DividerItemDecoration.VERTICAL));
        oneConfigRecycler.addItemDecoration(new DividerItemDecoration(oneConfigRecycler.getContext(), DividerItemDecoration.HORIZONTAL));
        BackupConfigAdapter oneConfigAdapter = new BackupConfigAdapter(tmp.getChannelList());
        oneConfigRecycler.setAdapter(oneConfigAdapter);

        RecyclerView slotRecycler = view.findViewById(R.id.frag_back_recycler_slots);
        slotRecycler.addItemDecoration(new DividerItemDecoration(slotRecycler.getContext(), DividerItemDecoration.VERTICAL));
        slotRecycler.addItemDecoration(new DividerItemDecoration(slotRecycler.getContext(), DividerItemDecoration.HORIZONTAL));
        BackupSlotAdapter slotsAdapter = new BackupSlotAdapter(view, oneConfigAdapter, tmpList);
        slotRecycler.setAdapter(slotsAdapter);


        //passer ces trois là dans backupSlotAdapter, au même titre que les éléments du bas de main board. exemple de lien / activation avec save
        view.findViewById(R.id.frag_back_button_save).setOnClickListener(v -> {
            String text = "enregistrer nouvelle config à l'emplacement "+slotsAdapter.getFocusedIndex();
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);

            //set tous les canaux à faux

            //remplace config précédente
            slotsAdapter.getConfigList().set(slotsAdapter.getFocusedIndex(), new Generator());

            //envoie données à arduino
            ((BluetoothParent) getActivity()).sendData("");
        });

        view.findViewById(R.id.frag_back_button_load).setOnClickListener(v -> {
            //récupère config
            ArrayList<Channel> savedConfig = slotsAdapter.getConfigList().get(slotsAdapter.getFocusedIndex()).getChannelList();

            //la charge
            ((BluetoothParent) getActivity()).getGenerator().setChannelList(savedConfig);

            //envoie données à arduino
            ((BluetoothParent) getActivity()).sendData("");
        });

        view.findViewById(R.id.frag_back_button_delete).setOnClickListener(v -> {
            //supprime config
            slotsAdapter.getConfigList().get(slotsAdapter.getFocusedIndex()).setChannelList(new ArrayList<>());

            //if c'est le bouton sur lequel on est, actualiser oneConfigRecycler

            //envoie données à arduino
            ((BluetoothParent) getActivity()).sendData("");
        });

        return view;
    }
}