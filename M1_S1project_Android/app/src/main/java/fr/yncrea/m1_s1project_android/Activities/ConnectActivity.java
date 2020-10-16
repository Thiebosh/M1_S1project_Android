package fr.yncrea.m1_s1project_android.activities;

import android.bluetooth.BluetoothAdapter;
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
import fr.yncrea.m1_s1project_android.bluetooth.BluetoothService;

/**
 * Activité de connexion bluetooth à un appareil. Une fois connecté, peut accéder à l'activité principale
 */
public class ConnectActivity extends AppCompatActivity {
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    /**
     * Member object for the chat services
     */
    private BluetoothService mChatService = null;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;


    private Button mConfirmBluetoothButton;

    /*
     * Section Cycle de vie
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);


        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



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
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            //setupChat();
        }
        else {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }

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