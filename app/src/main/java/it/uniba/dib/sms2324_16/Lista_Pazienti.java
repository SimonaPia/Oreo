package it.uniba.dib.sms2324_16;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Lista_Pazienti extends AppCompatActivity implements Adapter_Pazienti.OnItemClickListener{
    private static final String TAG = "Lista_Pazienti";
    private RecyclerView recyclerView;
    private Adapter_Pazienti adapterPazienti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_pazienti);

        ImageView indietroButton = findViewById(R.id.topLeftIcon);
        recyclerView = findViewById(R.id.recyclerViewPazienti);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Passa un'istanza di questa classe come listener per gestire i clic degli elementi della lista
        adapterPazienti = new Adapter_Pazienti(new ArrayList<>(), this);


        getPazientiPerLogopedista();
        indietroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getPazientiPerLogopedista() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("Bambino - Logopedista");
            List<String> idPaziente = new ArrayList<>();

            collectionReference.whereEqualTo("id_logopedista", userId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                Log.d("TAG", "Dimensione query: " + task.getResult().size());
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    idPaziente.add(document.getString("id_bambino"));
                                    getPazientiFromFirestore(idPaziente);
                                }
                            }
                            else
                                Log.d("TAG", "Errore!");
                        }
                    });
        }
    }

    @SuppressLint("RestrictedApi")
    private void getPazientiFromFirestore(List<String> idPaziente) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference pazientiRef = db.collection("Pazienti");

        pazientiRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Paziente> pazientiList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    for (int i = 0; i < idPaziente.size(); i++)
                    {
                        if (document.getId().equals(idPaziente.get(i)))
                        {
                            String nome = document.getString("nome");
                            String cognome = document.getString("cognome");

                            Paziente paziente = new Paziente(nome, cognome);
                            pazientiList.add(paziente);
                        }
                    }
                }

                adapterPazienti.setPazientiList(pazientiList);
                recyclerView.setAdapter(adapterPazienti);
            } else {
                Log.w(TAG, "Errore durante il recupero dei documenti", task.getException());
            }
        });
        ImageView imageView = findViewById(R.id.topLeftIcon);

        // Aggiungi un listener per il click sull'ImageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'intent per navigare alla HomePageBambinoFragment
                Intent intent = new Intent(Lista_Pazienti.this, HomePageLogopedista.class);
                startActivity(intent);
            }
        });
    }

    // Implementa il metodo onItemClick per gestire il clic sugli elementi della lista
    @Override
    public void onItemClick(Paziente paziente) {
        Intent intent = new Intent(this, profilo_utente.class);
        intent.putExtra("nomePaziente", paziente.getNome());
        intent.putExtra("cognomePaziente", paziente.getCognome());
        startActivity(intent);
    }
}