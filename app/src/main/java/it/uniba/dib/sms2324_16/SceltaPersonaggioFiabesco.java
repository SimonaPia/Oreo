package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SceltaPersonaggioFiabesco extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scelta_personaggio_fiabesco);
        ImageView indietroButton = findViewById(R.id.topLeftIcon);
        indietroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Inizializza Firestore
        db = FirebaseFirestore.getInstance();

        // Inizializza Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Ottieni l'utente corrente
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userId = user.getUid(); // Ottieni l'ID dell'utente corrente

            // Imposta il click listener per le ImageView dei personaggi
            ImageView button1 = findViewById(R.id.button);
            ImageView button2 = findViewById(R.id.button1);
            ImageView button3 = findViewById(R.id.button2);
            ImageView button4 = findViewById(R.id.button3);

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvaPersonaggio("Cavaliere Lancillotto", "Fiabesco");
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvaPersonaggio("Principessa", "Fiabesco");
                }
            });

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvaPersonaggio("Cavaliere Artù", "Fiabesco");
                }
            });

            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvaPersonaggio("Fatina", "Fiabesco");
                }
            });
        }
    }


    private void salvaPersonaggio(String nomePersonaggio, String nomeScenario) {
        // Crea un nuovo oggetto da inserire nel database
        Map<String, Object> sceltaPersonaggio = new HashMap<>();
        sceltaPersonaggio.put("nome_personaggio", nomePersonaggio);
        sceltaPersonaggio.put("nome_scenario", nomeScenario);

        // Scrivi nel database sotto la collezione "SceltaPersonaggio" e il documento con ID dell'utente
        db.collection("SceltaPersonaggio").document(userId)
                .set(sceltaPersonaggio)
                .addOnSuccessListener(aVoid -> {
                    // Operazione di scrittura completata con successo
                })
                .addOnFailureListener(e -> {
                    // Gestione degli errori
                });
    }
}
