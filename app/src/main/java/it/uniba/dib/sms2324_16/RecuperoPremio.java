package it.uniba.dib.sms2324_16;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecuperoPremio extends AppCompatActivity {
    private ImageView imageViewPremio;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recupero_premi);

        // Inizializza Firebase Firestore e Authentication
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inizializza l'ImageView
        imageViewPremio = findViewById(R.id.imageViewPremio);

        // Ottieni l'ID dell'utente corrente
        String userId = mAuth.getCurrentUser().getUid();

        // Ottieni il documento del bambino corrispondente all'ID utente
        DocumentReference bambinoRef = db.collection("Pazienti").document(userId);
        bambinoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Se il documento esiste, leggi il campo "premio"
                    String premio = documentSnapshot.getString("premio");

                    // Imposta l'immagine in base al premio
                    if (premio != null) {
                        switch (premio) {
                            case "dolce":
                                imageViewPremio.setImageResource(R.drawable.cookie);
                                break;
                            case "medaglia":
                                imageViewPremio.setImageResource(R.drawable.medaglia);
                                break;
                            case "giornalino":
                                imageViewPremio.setImageResource(R.drawable.giornalino);
                                break;
                            case "figurine":
                                imageViewPremio.setImageResource(R.drawable.pokeball);
                                break;
                            default:
                                // Se il premio non corrisponde a nessuna delle opzioni, puoi impostare un'immagine di default
                                imageViewPremio.setImageResource(R.drawable.scenario_piratesco);
                                break;
                        }
                    }
                } else {
                    // Se il documento non esiste, gestisci di conseguenza (ad esempio, mostra un messaggio di errore o un'immagine di default)
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Gestisci eventuali errori di lettura dal database
            }
        });
        Button buttonRitira = findViewById(R.id.buttonRitira);
        buttonRitira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostraDialog();
            }
        });
        ImageView imageView = findViewById(R.id.arrow_back);

        // Aggiungi un listener per il click sull'ImageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'intent per navigare alla HomePageBambinoFragment
                Intent intent = new Intent(RecuperoPremio.this, HomePageBambino.class);
                startActivity(intent);
            }
        });
    }
    private void mostraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title)); // Ottiene il titolo dalla risorsa stringa localizzata
        builder.setMessage(getString(R.string.dialog_message)); // Ottiene il messaggio dalla risorsa stringa localizzata

        // Aggiungi un pulsante "OK" al dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Chiusura del dialog
                dialogInterface.dismiss();
            }
        });

        // Mostra il dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
