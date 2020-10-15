package fr.yncrea.m1_s1project_android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import fr.yncrea.m1_s1project_android.R;

/**
 * Activité secondaire : gestion du module de sauvegarde du MIVS
 */
public class BackupActivity extends AppCompatActivity {

    /*
     * Section Cycle de vie
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
    }


    @Override
    public void onRestart() {//retour à l'écran principal avant déconnexion
        super.onRestart();
        startActivity((new Intent(BackupActivity.this, ConnectActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    /*
     * Section Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global_connected, menu);
        getMenuInflater().inflate(R.menu.backup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toMainBoardActivity:
                startActivity((new Intent(BackupActivity.this, MainBoardActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            case R.id.menu_toConnectActivity:
                startActivity((new Intent(BackupActivity.this, ConnectActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}