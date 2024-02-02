package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;

import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;
import androidx.navigation.fragment.NavHostFragment;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomePageLogopedista extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private NavController navController1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.homepage_logopedista);
        Toolbar myToolbar = findViewById(R.id.MyToolbar);
        setSupportActionBar(myToolbar);
        Intent intent = getIntent();
        String titolo = intent.getStringExtra("nomeCognome");

        getSupportActionBar().setTitle(titolo);
        getSupportActionBar().setSubtitle(getString(R.string.logopedist));


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new HomePageLogopedistaFragment())
                    .commit();
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        navController1 = navHostFragment.getNavController();

        // Imposta il NavController per la vista principale (LinearLayout).
        Navigation.setViewNavController(findViewById(R.id.homepage_logopedista), navController1);

        appBarConfiguration = new AppBarConfiguration.Builder(navController1.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController1, appBarConfiguration);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Gestisci il pulsante "Indietro" nella barra delle azioni
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!navController1.popBackStack()) {
            // Se non ci sono fragment nel back stack, esegui l'azione di default
            super.onBackPressed();
        }
    }

}
