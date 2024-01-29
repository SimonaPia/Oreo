package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<Bambino> bambinoList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Imposta il titolo della Toolbar
        getSupportActionBar().setTitle("Classifica");

        bambinoList = new ArrayList<>();
        leaderboardAdapter = new LeaderboardAdapter(bambinoList);
        recyclerView.setAdapter(leaderboardAdapter);

        db = FirebaseFirestore.getInstance();

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        db.collection("Classifica")
                .orderBy("valuta", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            bambinoList.clear();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                String userId = snapshot.getId();
                                String userName = snapshot.getString("nome");
                                int userValuta = snapshot.getLong("valuta").intValue();
                                bambinoList.add(new Bambino(userId, userName, userValuta));
                            }
                            leaderboardAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(LeaderboardActivity.this, "Nessun dato trovato", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LeaderboardActivity", "Errore nel recupero dei dati", e);
                        Toast.makeText(LeaderboardActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
