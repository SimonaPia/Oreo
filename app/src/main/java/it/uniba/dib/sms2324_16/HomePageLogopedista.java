package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageLogopedista extends AppCompatActivity {
    private GridView gridview;
    private int[] icons = {
            R.drawable.ic_pazienti_foreground,
            //R.drawable.icon2,
            //R.drawable.icon3,
            //R.drawable.icon4
    };
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
                    .replace(R.id.homepage_logopedista_fragment, new HomePageLogopedistaFragment())
                    .commit();
        }

    }

}
