package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.graphics.Color;
import android.graphics.PorterDuff;
public class profilo_utente extends AppCompatActivity {

    private static final String TAG = "profilo_utente";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilo_utente);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String nome = getIntent().getStringExtra("nomePaziente");
        String cognome = getIntent().getStringExtra("cognomePaziente");




        if (nome != null && cognome != null) {
            Log.d(TAG, "Nome Paziente: " + nome);
            Log.d(TAG, "Cognome Paziente: " + cognome);

            // Ottieni i dati del paziente e i progressi dalla collezione "Pazienti" in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Pazienti")
                    .whereEqualTo("nome", nome)
                    .whereEqualTo("cognome", cognome)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    // Recupera i dati del paziente dal documento
                                    Paziente paziente = document.toObject(Paziente.class);
                                    if (document.exists()) {
                                        Log.d(TAG, "Documento trovato.");
                                       // Paziente paziente = document.toObject(Paziente.class);

                                        // Verifica che l'oggetto Paziente non sia nullo
                                        if (paziente != null) {
                                            // Popola la vista del profilo utente con i dati da "Pazienti"
                                            TextView profiloTitle = findViewById(R.id.textTitle);
                                            profiloTitle.setText(paziente.getNome() + " " + paziente.getCognome());

                                            // Imposta la RatingBar con il valore dei giorniGiochi
                                            RatingBar ratingBar = findViewById(R.id.ratingBarGiornigiochi);
                                            int giorniGioco = paziente.getGiornigiochi();
                                            ratingBar.setRating(giorniGioco);

                                            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                                            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP); // Imposta il colore delle stelle

                                            TextView textView = findViewById(R.id.textViewpercentualeerrori);
                                            textView.setText(String.valueOf(paziente.getPercentualeerrori()));
                                            Log.d(TAG, "Percentuale Errori: " + paziente.getPercentualeerrori());

                                            TextView posizioneText = findViewById(R.id.textViewClassifica);
                                            posizioneText.setText("Posizione: " + paziente.getPosizioneclassifica());
                                        } else {
                                            Log.e(TAG, "Oggetto Paziente nullo.");
                                            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente o ritornando indietro.
                                        }
                                    } else {
                                        Log.d(TAG, "Documento non trovato.");
                                    }

                                }
                            } else {
                                Log.e(TAG, "Errore durante il recupero dei dati da Firestore.", task.getException());
                                Toast.makeText(profilo_utente.this, "Errore: Impossibile recuperare i dati", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Log.e(TAG, "Nome o cognome del paziente nullo.");
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente o ritornando indietro.
        }

    }
}
