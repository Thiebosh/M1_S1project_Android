package fr.yncrea.m1_s1project_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import fr.yncrea.m1_s1project_android.MIVS_RC_Application;
import fr.yncrea.m1_s1project_android.R;

/**
 * Activité de connexion bluetooth à un appareil. Une fois connecté, peut accéder à l'activité principale
 */
public class ConnectActivity extends AppCompatActivity {

    private Button mConfirmBluetoothButton;

    /*
     * Section Cycle de vie
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);



        // Initialize the button to jump on the next activity
        mConfirmBluetoothButton = (Button) findViewById(R.id.confirmBluetoothButton);
        mConfirmBluetoothButton.setOnClickListener(v -> {
            startActivity((new Intent(ConnectActivity.this, MainBoardActivity.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
        mConfirmBluetoothButton.setEnabled(false);//activera quand communication bluetooth etablie


        //1. prépare bouton scan et son action

        //2. prépare containers listview
    }

    @Override
    public void onResume() {
        super.onResume();
        //après lancement ou mise en pause :
        //1. déconnexion bluetooth

        //2. vérifie activation bluetooth

        //3. charge liste appareils apairés et les mets dans le listview
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //déconnexion bluetooth
    }

    /*
     * Section Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_bluetooth_scan) {
            Toast.makeText(MIVS_RC_Application.getContext(), "écran connexion bluetooth", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}