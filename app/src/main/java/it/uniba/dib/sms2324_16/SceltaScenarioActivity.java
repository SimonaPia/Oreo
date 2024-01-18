package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class SceltaScenarioActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scelta_scenario);

        // Inizializza il NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Collega ciascun bottone al suo drawable di riferimento
        setButtonBackgroundResource(R.id.scelta1, R.drawable.fiabesco_immagine);
        setButtonBackgroundResource(R.id.scelta2, R.drawable.savana_immagine);
        setButtonBackgroundResource(R.id.scelta3, R.drawable.pirata__immagine);
        setButtonBackgroundResource(R.id.scelta4, R.drawable.mondo_incantato_immagine);

        // Aggiungi listener ai bottoni per la navigazione
        setButtonClickListener(R.id.scelta1, R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_fiabesco);
        setButtonClickListener(R.id.scelta2, R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_savana);
        setButtonClickListener(R.id.scelta3, R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_pirata);
        setButtonClickListener(R.id.scelta4, R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_mondo_incantato);
    }

    private void setButtonBackgroundResource(int buttonId, int drawableId) {
        Button button = findViewById(buttonId);
        button.setBackgroundResource(drawableId);
    }

    private void setButtonClickListener(int buttonId, final int actionId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviga alla destinazione associata all'azione specificata
                navController.navigate(actionId);
            }
        });
    }
}
