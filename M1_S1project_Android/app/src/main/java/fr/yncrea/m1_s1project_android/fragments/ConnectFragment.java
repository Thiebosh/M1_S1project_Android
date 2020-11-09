package fr.yncrea.m1_s1project_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.yncrea.m1_s1project_android.NavigationHost;
import fr.yncrea.m1_s1project_android.R;

public class ConnectFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        //setHasOptionsMenu(true);//active le onPrepareOptionsMenu

        ((Button) view.findViewById(R.id.fra_con_connectButton)).setOnClickListener(v -> {
            ((NavigationHost) getActivity()).navigateTo(new MainBoardFragment(), false); // Navigate to the next Fragment
        });

        return view;
    }

    /*
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_admin_logout);
        if (item!=null) item.setVisible(false);
    }
     */
}
