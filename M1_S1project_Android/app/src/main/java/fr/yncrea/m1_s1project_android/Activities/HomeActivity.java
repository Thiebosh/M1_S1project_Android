package fr.yncrea.m1_s1project_android.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import fr.yncrea.m1_s1project_android.MIVS_RC_Application;
import fr.yncrea.m1_s1project_android.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(MIVS_RC_Application.getContext(), "écran settings", Toast.LENGTH_SHORT).show();
            //PreferenceUtils.setLogin(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}