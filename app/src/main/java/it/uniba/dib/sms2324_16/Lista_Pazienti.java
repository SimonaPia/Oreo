package it.uniba.dib.sms2324_16;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.function.Consumer;
import androidx.appcompat.widget.Toolbar;

public class Lista_Pazienti extends AppCompatActivity implements Adapter_Pazienti.OnItemClickListener{
    private static final String TAG = "Lista_Pazienti";
    private RecyclerView recyclerView;
    private Adapter_Pazienti adapterPazienti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_pazienti);

        recyclerView = findViewById(R.id.recyclerViewPazienti);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Passa un'istanza di questa classe come listener per gestire i clic degli elementi della lista
        adapterPazienti = new Adapter_Pazienti(new ArrayList<>(), this);
        recyclerView.setAdapter(adapterPazienti);

        getPazientiFromFirestore();
    }

    @SuppressLint("RestrictedApi")
    private void getPazientiFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference pazientiRef = db.collection("Pazienti");

        pazientiRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Paziente> pazientiList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String nome = document.getString("nome");
                    String cognome = document.getString("cognome");

                    Paziente paziente = new Paziente(nome, cognome);
                    pazientiList.add(paziente);
                }

                adapterPazienti.setPazientiList(pazientiList);
            } else {
                Log.w(TAG, "Errore durante il recupero dei documenti", task.getException());
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