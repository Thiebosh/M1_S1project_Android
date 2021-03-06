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

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.recyclers.BackupConfigAdapter;
import fr.yncrea.m1_s1project_android.recyclers.BackupStoreAdapter;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.models.Generator;
import fr.yncrea.m1_s1project_android.services.JsonConverterService;

/**
 * Activité secondaire : gestion du module de sauvegarde du MIVS
 */
public class BackupFragment extends Fragment implements BluetoothChildren {

    private BackupStoreAdapter mSlotsAdapter;

    /*
     * Section BluetoothChildren
     */

    @Override
    public void applyChanges(Generator generator, int index) {
        if (index == JsonConverterService.APPLYING_GLOBAL) mSlotsAdapter.notifyDataSetChanged();
        else /*if (generator != null)*/ mSlotsAdapter.updateChannelListData(generator.getChannelList(), index);
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
        if(!((BluetoothParent) Objects.requireNonNull(getContext())).getIsStoresInitialized()) {
            ((BluetoothParent) Objects.requireNonNull(getContext())).sendData(getString(R.string.blt_command_init_backup));
        }

        View view = inflater.inflate(R.layout.fragment_backup, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        RecyclerView oneConfigRecycler = view.findViewById(R.id.frag_back_recycler_config);
        oneConfigRecycler.addItemDecoration(new DividerItemDecoration(oneConfigRecycler.getContext(), DividerItemDecoration.VERTICAL));
        oneConfigRecycler.addItemDecoration(new DividerItemDecoration(oneConfigRecycler.getContext(), DividerItemDecoration.HORIZONTAL));
        BackupConfigAdapter oneConfigAdapter = new BackupConfigAdapter();
        oneConfigRecycler.setAdapter(oneConfigAdapter);

        RecyclerView storeRecycler = view.findViewById(R.id.frag_back_recycler_stores);
        storeRecycler.addItemDecoration(new DividerItemDecoration(storeRecycler.getContext(), DividerItemDecoration.VERTICAL));
        storeRecycler.addItemDecoration(new DividerItemDecoration(storeRecycler.getContext(), DividerItemDecoration.HORIZONTAL));
        mSlotsAdapter = new BackupStoreAdapter(view, oneConfigAdapter);
        storeRecycler.setAdapter(mSlotsAdapter);

        return view;
    }
}