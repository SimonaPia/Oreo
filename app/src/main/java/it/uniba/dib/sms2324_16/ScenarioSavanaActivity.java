package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ScenarioSavanaActivity extends AppCompatActivity {
    private CollectionReference scenarioCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_savana);

        // Inizializza la raccolta principale degli scenari
        this.scenarioCollection = FirebaseFirestore.getInstance().collection("SceltaScenario");

        Button confermaButton = findViewById(R.id.bottone_conferma);

        ImageView indietroButton = findViewById(R.id.topLeftIcon);

        confermaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScenarioChoice("id_genitore", "id_bambino", "Savana");
                eseguiSalvataggioScenario("Savana");
                showConfirmationMessage();
                readScenarioChoice(); // Chiamata per leggere i dati da Firestore
            }
        });
        indietroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onConfermaClick(View view) {
        // Torna all'Activity SceltaScenarioActivity dopo la conferma
        Intent intent = new Intent(this, SceltaScenarioActivity.class);
        startActivity(intent);
        finish();
    }

    private void showConfirmationMessage() {
        Toast.makeText(this, "Ben fatto! Scenario scelto!", Toast.LENGTH_SHORT).show();
        // Torna all'Activity di scelta scenario invece di avviare un nuovo fragment o un'altra attività
        Intent intent = new Intent(this, SceltaScenarioActivity.class);
        startActivity(intent);
        finish(); // Opzionale, a seconda della tua logica di navigazione
    }

    private void saveScenarioChoice(String idGenitore, String idBambino, String sceltaScenario) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Converti l'oggetto Scenario in una mappa
            Map<String, Object> scenarioMap = new HashMap<>();
            scenarioMap.put("id_genitore", idGenitore);
            scenarioMap.put("id_bambino", idBambino);
            scenarioMap.put("SceltaScenario", sceltaScenario);

            // Aggiungi un nuovo scenario in Firestore senza specificare un identificatore
            scenarioCollection
                    .document(currentUser.getUid())
                    .set(scenarioMap)
                    .addOnSuccessListener(aVoid -> {
                        // Lo scenario è stato aggiunto con successo
                        // Puoi fare qualcosa se necessario
                    })
                    .addOnFailureListener(e -> {
                        // Gestisci eventuali errori nell'aggiunta dello scenario
                    });
        } else {
            // Gestione dell'errore o log quando l'utente non è autenticato
        }
    }

    private void eseguiSalvataggioScenario(String sceltaScenario) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String idGenitore = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Bambino - Genitore").whereEqualTo("id_genitore", idGenitore)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    String idBambino = document.getString("id_bambino1");
                                    saveScenarioChoice(idGenitore, idBambino, sceltaScenario);
                                }
                            }


                        }
                    });
            Toast.makeText(this, "Scenario salvato: " + sceltaScenario, Toast.LENGTH_SHORT).show();
        }
    }

    private void readScenarioChoice() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Recupera lo scenario da Firestore e mostra i dati
            scenarioCollection
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Lo scenario esiste, puoi ottenere i dati
                                String idGenitore = document.getString("id_genitore");
                                String idBambino = document.getString("id_bambino");
                                String sceltaScenario = document.getString("SceltaScenario");

                                // Mostra i dati letti in un Toast
                                Toast.makeText(ScenarioSavanaActivity.this,
                                        "ID Genitore: " + idGenitore +
                                                "\nID Bambino: " + idBambino +
                                                "\nScelta Scenario: " + sceltaScenario,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // Lo scenario non esiste
                                Toast.makeText(ScenarioSavanaActivity.this, "Nessuno scenario trovato", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Gestisci eventuali errori durante la lettura dello scenario da Firestore
                            Toast.makeText(ScenarioSavanaActivity.this, "Errore durante la lettura dello scenario da Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Gestione dell'errore o log quando l'utente non è autenticato
        }
    }
}

