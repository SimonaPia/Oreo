package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceltaScenarioActivity extends AppCompatActivity {

    private SceltaScenarioAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scelta_scenario);


        Button btnFiabesco = findViewById(R.id.btnFiabesco);
        Button btnSavana = findViewById(R.id.btnSavana);
        Button btnPirata = findViewById(R.id.btnPirata);
        Button btnMondoIncantato = findViewById(R.id.btnMondoIncantato);
        ImageButton backButton = findViewById(R.id.topLeftIcon);

        // Aggiungi un listener per gestire il clic sul pulsante indietro
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        // Inizializza la lista degli elementi di scenario
        List<SceltaScenarioAdapter.ScenarioItem> scenarioItems = createScenarioItems();

        adapter = new SceltaScenarioAdapter(this, scenarioItems, new SceltaScenarioAdapter.OnItemClickListener() {
            @Override
            public void onScenarioClick(int actionId) {
                // Avvia l'attività associata all'azione specificata
                navigateToScenario(actionId);
            }
        });

        // Aggiungi un listener per gestire il clic sul pulsante Fiabesco
        btnFiabesco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent per avviare l'attività Fiabesco
                Intent intent = new Intent(SceltaScenarioActivity.this, ScenarioFiabescoActivity.class);
                // Salva la scelta dello scenario nel database Firebase
                saveScenarioChoice("id_genitore", "id_bambino", "Fiabesco");
                // Avvia l'attività Fiabesco
                startActivity(intent);
            }
        });

        btnSavana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent per avviare l'attività Savana
                Intent intent = new Intent(SceltaScenarioActivity.this, ScenarioSavanaActivity.class);
                // Salva la scelta dello scenario nel database Firebase
                saveScenarioChoice("id_genitore", "id_bambino", "Fiabesco");
                // Avvia l'attività Savana
                startActivity(intent);
            }
        });

        btnPirata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent per avviare l'attività Pirata
                Intent intent = new Intent(SceltaScenarioActivity.this, ScenarioPirataActivity.class);
                // Salva la scelta dello scenario nel database Firebase
                saveScenarioChoice("id_genitore", "id_bambino", "Fiabesco");
                // Avvia l'attività Pirata
                startActivity(intent);
            }
        });

        btnMondoIncantato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent per avviare l'attività Mondo Incantato
                Intent intent = new Intent(SceltaScenarioActivity.this, ScenarioMondoIncantatoActivity.class);
                // Salva la scelta dello scenario nel database Firebase
                saveScenarioChoice("id_genitore", "id_bambino", "Fiabesco");
                // Avvia l'attività Mondo incantato
                startActivity(intent);
            }
        });
    }

    private void saveScenarioChoice(String idGenitore, String idBambino, String sceltaScenario) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String tableName = "SceltaScenario";

        // Crea un oggetto da salvare nel database
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("id_genitore", idGenitore);
        dataToSave.put("id_bambino", idBambino);
        dataToSave.put("SceltaScenario", sceltaScenario);

        // Salva i dati nel database
        databaseReference.child(tableName).push().setValue(dataToSave);
    }

    private void goBack() {
        // Simula il comportamento del pulsante indietro
        onBackPressed();
    }

    private List<SceltaScenarioAdapter.ScenarioItem> createScenarioItems() {
        List<SceltaScenarioAdapter.ScenarioItem> scenarioItems = new ArrayList<>();
        // Aggiungi gli elementi di scenario con i rispettivi ID di azione
        scenarioItems.add(new SceltaScenarioAdapter.ScenarioItem(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_fiabesco));
        scenarioItems.add(new SceltaScenarioAdapter.ScenarioItem(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_savana));
        scenarioItems.add(new SceltaScenarioAdapter.ScenarioItem(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_pirata));
        scenarioItems.add(new SceltaScenarioAdapter.ScenarioItem(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_mondo_incantato));
        return scenarioItems;
    }

    private void navigateToScenario(int actionId) {
        // Crea una mappa che associa gli ID delle azioni alle attività corrispondenti
        Map<Integer, Class<?>> activityMap = new HashMap<>();
        activityMap.put(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_fiabesco, ScenarioFiabescoActivity.class);
        activityMap.put(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_savana, ScenarioSavanaActivity.class);
        activityMap.put(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_pirata, ScenarioPirataActivity.class);
        activityMap.put(R.id.action_homePageGenitoreFragment_to_activity_scelta_scenario_mondo_incantato, ScenarioMondoIncantatoActivity.class);

        // Recupera la classe dell'attività associata all'ID dell'azione
        Class<?> destinationActivity = activityMap.get(actionId);

        // Controlla se la classe dell'attività è stata trovata
        if (destinationActivity != null) {
            try {
                // Avvia l'attività
                Intent intent = new Intent(this, destinationActivity);
                startActivity(intent);

            } catch (Exception e) {
                // Gestisci eventuali eccezioni durante l'avvio dell'attività
                Log.e("SceltaScenarioActivity", "Errore durante l'avvio dell'attività", e);
            }
        } else {
            // ID dell'azione non gestito, emetti un avviso o un log
            Log.e("SceltaScenarioActivity", "ID dell'azione non gestito: " + actionId);
            // Puoi anche mostrare un messaggio all'utente se necessario
        }
    }


}

