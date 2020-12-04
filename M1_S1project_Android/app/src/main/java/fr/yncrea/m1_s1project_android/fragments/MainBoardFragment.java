package fr.yncrea.m1_s1project_android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.RecyclerView.MainBoardAdapterView;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public class MainBoardFragment extends Fragment implements BluetoothChildren, View.OnClickListener {

    private MainBoardAdapterView mAdapter;
    private ToggleButton mAllOnBtn;
    private ToggleButton mAllOffBtn;

    /*
     * Section BluetoothChildren
     */

    @Override
    public void applyChanges(Generator generator, int index) {
        mAdapter.updateChannelListData(generator.getChannelList(), index);
        if (index == -1) {
            if (generator.getChannel(0).isActive()) {
                mAllOnBtn.setChecked(true);
                mAllOffBtn.setChecked(false);
            } else {
                mAllOffBtn.setChecked(true);
                mAllOnBtn.setChecked(false);
            }
        }
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
        mAdapter = new MainBoardAdapterView(getContext(), view, ((BluetoothParent) activity).getGenerator().getChannelList());
        rv.setAdapter(mAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(rv.getItemAnimator())).setSupportsChangeAnimations(false);
        mAdapter.notifyDataSetChanged();

        // Listeners
        mAllOnBtn = view.findViewById(R.id.AllOn);
        mAllOnBtn.setOnClickListener(this);

        mAllOffBtn = view.findViewById(R.id.AllOff);
        mAllOffBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(((ToggleButton) Objects.requireNonNull(getView()).findViewById(id)).isChecked()) {
            ((ToggleButton) getView().findViewById(id == R.id.AllOn ? R.id.AllOff : R.id.AllOn)).setChecked(false);
            ((BluetoothParent) Objects.requireNonNull(getActivity())).getGenerator().setAllChannelActive(id == R.id.AllOn);
            ((BluetoothParent) getActivity()).sendData((new Channel()).setId(-1).setActive(id == R.id.AllOn));
            mAdapter.notifyDataSetChanged();
        }
    }
}
