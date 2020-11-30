package fr.yncrea.m1_s1project_android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.RecyclerView.MainBoardAdapterView;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public class MainBoardFragment extends Fragment implements BluetoothChildren {

    private MainBoardAdapterView mAdapter;
    private SwitchCompat mSwitchAll;

    /*
     * Section BluetoothChildren
     */

    @Override
    public void applyChanges(Generator generator, int index) {
        mAdapter.updateChannelList(generator.getChannelList(), index);
        if (index == -1) mSwitchAll.setChecked(!mSwitchAll.isChecked());
    }


    /*
     * Section Menu
     */

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //définit éléments visibles du menu pour ce fragment
        menu.findItem(R.id.menu_disconnect).setVisible(true);
        menu.findItem(R.id.menu_toMainBoard).setVisible(false);
        menu.findItem(R.id.menu_toBackup).setVisible(true);
    }


    /*
     * Section Cycle de vie
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_board, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        Activity activity = Objects.requireNonNull(getActivity());

        RecyclerView rv = view.findViewById(R.id.mainboard_recycler);
        mAdapter = new MainBoardAdapterView(getContext(),
                ((BluetoothParent) activity).getGenerator().getChannelList());
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
        rv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        mSwitchAll = view.findViewById(R.id.switch1);
        mSwitchAll.setOnClickListener(v -> {
            boolean isChecked = mSwitchAll.isChecked();

            ((BluetoothParent) activity).getGenerator().setAllChannelActive(isChecked);
            mAdapter.notifyDataSetChanged();

            ((BluetoothParent) activity).sendData((new Channel()).setId(-1).setActive(isChecked));
        });

        return view;
    }

}
