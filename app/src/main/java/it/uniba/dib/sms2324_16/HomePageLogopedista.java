package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomePageLogopedista extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.homepage_logopedista);
        Toolbar myToolbar = findViewById(R.id.MyToolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("Fabrizio Balducci");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new HomePageLogopedistaFragment())
                    .commit();
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_graph);
        NavController navController = navHostFragment.getNavController();

        // Imposta il NavController per la vista principale (LinearLayout).
        Navigation.setViewNavController(findViewById(R.id.homepage_logopedista), navController);

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

}
