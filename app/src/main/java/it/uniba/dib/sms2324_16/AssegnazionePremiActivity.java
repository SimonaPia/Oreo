package it.uniba.dib.sms2324_16;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AssegnazionePremiActivity extends AppCompatActivity {
    private RadioButton radio_button1, radio_button2, radio_button3, radio_button4;
    private CardView card1, card2, card3, card4;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assegnazione_premi);


        // Recupera i dati dal Intent che ha avviato questa attività
        Intent intent = getIntent();
        String nome = intent.getStringExtra("nomePaziente");
        String cognome = intent.getStringExtra("cognomePaziente");
        db = FirebaseFirestore.getInstance();
        Log.d("AssegnazionePremiActivity", "onCreate() called");
        // Inizializza le view
        radio_button1 = findViewById(R.id.radio_button1);
        radio_button2 = findViewById(R.id.radio_button2);
        radio_button3 = findViewById(R.id.radio_button3);
        radio_button4 = findViewById(R.id.radio_button4);

        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        CardView card4 = findViewById(R.id.card4);

        // Aggiungi un listener per il click alle CardView
        radio_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confermaAssegnazionePremio("dolce");
            }
        });

        radio_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confermaAssegnazionePremio("medaglia");
            }
        });

        radio_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confermaAssegnazionePremio("giornalino");
            }
        });

        radio_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confermaAssegnazionePremio("figurine");
            }
        });
        ImageView imageView = findViewById(R.id.freccia);

        // Aggiungi un listener per il click sull'ImageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Torna all'attività profilo_utente con i dati aggiornati
                Intent intent = new Intent(AssegnazionePremiActivity.this, profilo_utente.class);
                intent.putExtra("nomePaziente", nome);
                intent.putExtra("cognomePaziente", cognome);
                startActivity(intent);

            }
        });
    }

    private void confermaAssegnazionePremio(final String nomePremio) {
        Log.d("AssegnazionePremiActivity", "confermaAssegnazionePremio() called with premio: " + nomePremio);

        AlertDialog.Builder builder = new AlertDialog.Builder(AssegnazionePremiActivity.this);
        builder.setTitle("Conferma Assegnazione Premio");
        builder.setMessage("Vuoi assegnare il premio '" + nomePremio + "'?");

        builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("AssegnazionePremiActivity", "Positive button clicked");
                // Salva il premio selezionato nel database Pazienti
                salvaPremio(nomePremio);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("AssegnazionePremiActivity", "Negative button clicked");
                // L'utente ha annullato l'operazione
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void salvaPremio(final String nomePremio) {
        // Supponendo che tu abbia già l'ID del bambino
        final String idBambino = "R9F0azhsC0YzmbWghWud2Fqr5Uv1";

        // Esegui una query per trovare il documento del bambino corrispondente all'ID
        DocumentReference bambinoRef = db.collection("Pazienti").document(idBambino);
        bambinoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Aggiungi il premio al documento del bambino
                    documentSnapshot.getReference().update("premio", nomePremio)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successo! Il premio è stato aggiornato con successo.
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Gestisci l'errore in caso di fallimento nell'aggiornamento del premio
                                }
                            });
                } else {
                    // Il documento del bambino non esiste
                }
            }
        });
    }


}
