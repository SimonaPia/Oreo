package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class VisualizzaScenario extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Recupera l'utente attualmente autenticato
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Ottieni l'ID dell'utente corrente
            String userId = currentUser.getUid();

            // Recupera lo scenario dal database Firebase utilizzando l'ID dell'utente
            db.collection("SceltaScenario")
                    .whereEqualTo("id_bambino", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String scenario = document.getString("SceltaScenario");
                                    Log.d(TAG, "Scenario recuperato: " + scenario);
                                    // Se lo scenario è piratesco, avvia l'Activity per visualizzarlo
                                    // Verifica se lo scenario è null prima di confrontarlo con una stringa
                                    if (scenario != null && scenario.equals("Pirata")) {
                                        Intent intent = new Intent(VisualizzaScenario.this, SceltaPersonaggioPiratesco.class);
                                        intent.putExtra("scenario", scenario);
                                        startActivity(intent);
                                        finish(); // Opzionale: chiudi questa Activity se non si deve tornare indietro
                                    }
                                    if (scenario != null && scenario.equals("Savana")) {
                                        Intent intent = new Intent(VisualizzaScenario.this, SceltaPersonaggioSavana.class);
                                        intent.putExtra("scenario", scenario);
                                        startActivity(intent);
                                        finish(); // Opzionale: chiudi questa Activity se non si deve tornare indietro
                                    }
                                    if (scenario != null && scenario.equals("Mondo incantato")) {
                                        Intent intent = new Intent(VisualizzaScenario.this, SceltaPersonaggioSavana.class);
                                        intent.putExtra("scenario", scenario);
                                        startActivity(intent);
                                        finish(); // Opzionale: chiudi questa Activity se non si deve tornare indietro
                                    }
                                    if (scenario != null && scenario.equals("fiabesco")) {
                                        Intent intent = new Intent(VisualizzaScenario.this, SceltaPersonaggioFiabesco.class);
                                        intent.putExtra("scenario", scenario);
                                        startActivity(intent);
                                        finish(); // Opzionale: chiudi questa Activity se non si deve tornare indietro
                                    }
                                }
                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                                Toast.makeText(VisualizzaScenario.this, "Errore nel recupero degli scenari", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // L'utente non è autenticato, gestire di conseguenza
        }
    }
}
