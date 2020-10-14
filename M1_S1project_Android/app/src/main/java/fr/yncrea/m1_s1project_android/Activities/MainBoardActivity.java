package fr.yncrea.m1_s1project_android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.yncrea.m1_s1project_android.R;

/**
 * Activité principale : gestion des channels du MIVS
 */
public class MainBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board);
    }

    /*
     * Section Menu
     */

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
                startActivity((new Intent(MainBoardActivity.this, BackupActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //MainBoardActivity.this.finish(); //peut remplacer le flag mais pas même retour visuel
                return true;

            case R.id.menu_toConnectActivity:
                startActivity((new Intent(MainBoardActivity.this, ConnectActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}