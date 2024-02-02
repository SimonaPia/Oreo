package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClassificaVistaLogopedista extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ClassificaAdapterLog leaderboardAdapter;
    private List<Bambino> bambinoList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classifica_vista_logopedista);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        bambinoList = new ArrayList<>();
        leaderboardAdapter = new ClassificaAdapterLog(bambinoList);
        recyclerView.setAdapter(leaderboardAdapter);

        db = FirebaseFirestore.getInstance();

        loadLeaderboard();
        ImageView imageView = findViewById(R.id.immagineInAltoASinistra);

        // Aggiungi un listener per il click sull'ImageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'intent per navigare alla HomePageBambinoFragment
                Intent intent = new Intent(ClassificaVistaLogopedista.this, HomePageLogopedista.class);
                startActivity(intent);
            }
        });
    }

    private void loadLeaderboard() {
        db.collection("Pazienti")
                .orderBy("monete", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            bambinoList.clear();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                String userId = snapshot.getId();
                                String userName = snapshot.getString("nome");
                                int userValuta = snapshot.getLong("monete").intValue();
                                bambinoList.add(new Bambino(userId, userName, userValuta));
                            }
                            leaderboardAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ClassificaVistaLogopedista.this, "Nessun dato trovato", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LeaderboardActivity", "Errore nel recupero dei dati", e);
                        Toast.makeText(ClassificaVistaLogopedista.this, "Errore nel recupero dei dati", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
