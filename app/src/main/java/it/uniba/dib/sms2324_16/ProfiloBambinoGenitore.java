package it.uniba.dib.sms2324_16;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfiloBambinoGenitore extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilo_bambino_genitore);
        Button bottoneAssegna = findViewById(R.id.assegnaButton);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String genitoreId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Ottieni i dati del paziente e i progressi dalla collezione "ProfiloUtente" in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (genitoreId != null) {
            Log.d(TAG, "ID Genitore: " + genitoreId);

            db.collection("ProfiloGenitore")
                    .document(genitoreId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "Documento trovato.");

                                    // Recupera i dati del profilo utente dal documento
                                    String cognome = document.getString("cognome");
                                    int giorniGiochi = document.getLong("giornigiochi").intValue();
                                    int monete = document.getLong("monete").intValue();
                                    String nome = document.getString("nome");
                                    int percentualeErrori = document.getLong("percentualeerrori").intValue();
                                    int posizioneClassifica = document.getLong("posizioneclassifica").intValue();

                                    // Popola la vista del profilo utente con i dati ottenuti
                                    TextView profiloTitle = findViewById(R.id.textTitle);
                                    profiloTitle.setText(nome + " " + cognome);

                                    RatingBar ratingBar = findViewById(R.id.ratingBarGiornigiochi);
                                    ratingBar.setRating(giorniGiochi);

                                    LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                                    stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

                                    TextView textView = findViewById(R.id.textViewpercentualeerrori);
                                    textView.setText(String.valueOf(percentualeErrori));

                                    TextView posizioneText = findViewById(R.id.textViewClassifica);
                                    posizioneText.setText("Posizione: " + posizioneClassifica);

                                } else {
                                    Log.d(TAG, "Documento non trovato.");
                                }
                            } else {
                                Log.e(TAG, "Errore durante il recupero dei dati da Firestore.", task.getException());
                                Toast.makeText(ProfiloBambinoGenitore.this, "Errore: Impossibile recuperare i dati", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Log.e(TAG, "ID del genitore nullo.");
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente o ritornando indietro.
        }

        ImageView imageView = findViewById(R.id.topLeftIcon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'intent per navigare alla HomePageBambinoFragment
                Intent intent = new Intent(ProfiloBambinoGenitore.this, HomePageGenitore.class);
                startActivity(intent);
            }
        });
    }
}
