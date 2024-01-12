package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;


public class IntermediatePage extends AppCompatActivity {
    private TextView nomeCognome;

    private void nomeCognomeUtente(TextView nomeCognome) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String uID = intent.getStringExtra("ID_Utente");

        // Creazione del riferimento al documento dell'utente nel tuo database Firestore
        DocumentReference userRef = db.collection("Utente").document(uID);

        // Recupero dei dati dell'utente
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // L'utente esiste, puoi accedere ai dati
                            Users user = documentSnapshot.toObject(Users.class);

                            // Ora puoi utilizzare l'oggetto "user" per ottenere i dati dell'utente
                            String nome = user.getNome();
                            String cognome = user.getCognome();
                            nomeCognome.setText(nome + cognome);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent1 = new Intent(IntermediatePage.this, HomePageLogopedista.class);
                                    intent1.putExtra("nomeCognome", nome + cognome);
                                    startActivity(intent1);
                                }
                            }, 3000);  // Delay di 3000 millisecondi (3 secondi)
                        } else {
                            // L'utente non esiste nel database
                            Log.d(TAG, "L'utente non esiste nel database");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestione dell'errore durante il recupero dei dati dell'utente
                        Log.e(TAG, "Errore durante il recupero dei dati dell'utente", e);
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intermediate_page);

        nomeCognome = findViewById(R.id.nome_cognome_logopedista);
        nomeCognomeUtente(nomeCognome);
    }
}
