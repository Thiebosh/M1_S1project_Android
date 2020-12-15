package fr.yncrea.m1_s1project_android.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.recyclers.MainBoardAdapter;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public class MainBoardFragment extends Fragment implements BluetoothChildren/*, View.OnClickListener*/ {

    private MainBoardAdapter mAdapter;
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
            }
            else {
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

        mAdapter = new MainBoardAdapter(view, BluetoothParent.mGenerator.getChannelList());
        RecyclerView recycler = view.findViewById(R.id.frag_main_recycler);
        recycler.setAdapter(mAdapter);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if ((metrics.widthPixels * 160 / metrics.densityDpi) > 600) {//width screen for card item
            recycler.addItemDecoration(new DividerItemDecoration(recycler.getContext(), DividerItemDecoration.VERTICAL));
            recycler.addItemDecoration(new DividerItemDecoration(recycler.getContext(), DividerItemDecoration.HORIZONTAL));
        }

        // Listeners
        mAllOnBtn = view.findViewById(R.id.frag_main_allOn);
        mAllOffBtn = view.findViewById(R.id.frag_main_allOff);

        View.OnClickListener listener = (View v) -> {
            int id = v.getId();
            if(((ToggleButton) view.findViewById(id)).isChecked()) {
                ((ToggleButton) view.findViewById(id == R.id.frag_main_allOn ? R.id.frag_main_allOff : R.id.frag_main_allOn)).setChecked(false);
                BluetoothParent.mGenerator.setAllChannelActive(id == R.id.frag_main_allOn);
                ((BluetoothParent) getActivity()).sendData((new Channel()).setId(-1).setActive(id == R.id.frag_main_allOn));
                mAdapter.notifyDataSetChanged();//changer par un adapter.allOn ? évite plantage
            }
        };

        mAllOnBtn.setOnClickListener(listener);
        mAllOffBtn.setOnClickListener(listener);

        return view;
    }

    /*
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(((ToggleButton) Objects.requireNonNull(getView()).findViewById(id)).isChecked()) {
            ((ToggleButton) getView().findViewById(id == R.id.AllOn ? R.id.AllOff : R.id.AllOn)).setChecked(false);
            ((BluetoothParent) Objects.requireNonNull(getActivity())).getGenerator().setAllChannelActive(id == R.id.AllOn);
            ((BluetoothParent) getActivity()).sendData((new Channel()).setId(-1).setActive(id == R.id.AllOn));
            mAdapter.notifyDataSetChanged();
        }
    }*/
}
