package fr.yncrea.m1_s1project_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.yncrea.m1_s1project_android.fragments.BackupFragment;
import fr.yncrea.m1_s1project_android.fragments.ConnectFragment;

/**
 * Activité principale : gestion des channels du MIVS
 */
public class AppActivity extends AppCompatActivity implements NavigationHost {

    /*
     * Section Cycle de vie
     */

    private boolean mFlagChangeActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.act_app_fragmentContainer, new ConnectFragment())
                    .commit();
        }
    }
/*
    @Override
    public void onRestart() {//retour à l'écran principal avant déconnexion
        super.onRestart();
        if (!mFlagChangeActivity) {
            startActivity((new Intent(AppActivity.this, bluetooth_exemple_activity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else mFlagChangeActivity = false;
    }
*/
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.act_app_fragmentContainer, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    /*
     * Section Menu
     */
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global_connected, menu);
        getMenuInflater().inflate(R.menu.main_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toBackupActivity:
                mFlagChangeActivity = true;
                startActivity((new Intent(AppActivity.this, BackupFragment.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //MainBoardActivity.this.finish(); //peut remplacer le flag mais pas même retour visuel
                return true;

            case R.id.menu_toConnectActivity:
                mFlagChangeActivity = true;
                startActivity((new Intent(AppActivity.this, bluetooth_exemple_activity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

 */
}