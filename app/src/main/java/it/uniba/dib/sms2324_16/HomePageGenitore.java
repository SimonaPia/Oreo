package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.FirebaseApp;

public class HomePageGenitore extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.homepage_genitore);
        Toolbar myToolbar = findViewById(R.id.MyToolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String titolo = intent.getStringExtra("nomeCognome");

        getSupportActionBar().setTitle(titolo);
        getSupportActionBar().setSubtitle(getString(R.string.parent));


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new HomePageGenitoreFragment())
                    .commit();
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_genitore);
        navController = navHostFragment.getNavController();

        // Imposta il NavController per la vista principale (LinearLayout).
        Navigation.setViewNavController(findViewById(R.id.homepage_genitore), navController);

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        ImageButton btnSwitchToChildArea = findViewById(R.id.btnSwitchToChildArea);

        // Aggiungi un listener per l'evento di clic
        btnSwitchToChildArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia un'attività per passare all'area bambino
                Intent intent = new Intent(HomePageGenitore.this, HomePageBambino.class);
                startActivity(intent);
            }
        });
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
        if (!navController.popBackStack()) {
            // Se non ci sono fragment nel back stack, esegui l'azione di default
            super.onBackPressed();
        }
    }
}
