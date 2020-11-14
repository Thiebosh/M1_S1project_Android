package fr.yncrea.m1_s1project_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.yncrea.m1_s1project_android.R;

public class MainBoardFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_board, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //définit éléments visibles du menu pour ce fragment
        menu.findItem(R.id.menu_disconnect).setVisible(true);
        menu.findItem(R.id.menu_toMainBoard).setVisible(false);
        menu.findItem(R.id.menu_toBackup).setVisible(true);
    }
}
