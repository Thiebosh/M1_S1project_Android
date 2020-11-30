package fr.yncrea.m1_s1project_android.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
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

        return view;
    }
}