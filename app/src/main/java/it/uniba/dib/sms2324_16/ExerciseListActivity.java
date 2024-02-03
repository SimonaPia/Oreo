package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;
    private List<String> nomePaziente = new ArrayList<>();

    private void creazioneAdapter(List<Patient> pazienti) {
        adapter = new ExerciseListAdapter(this, getExerciseList(), pazienti, new ExerciseListAdapter.OnItemClickListener() {
            @Override
            public void onDetailsClick(Exercise exercise, String details, String exerciseDetails) {
                // Implementa l'azione quando il pulsante "Dettagli" viene premuto
                showDetailsPopup(details, exerciseDetails);
            }

            @Override
            public void onAssignClick(Exercise exercise, Patient selectedPatient, int position) {
                // Assegna l'esercizio al paziente
                selectedPatient.addAssignedExercise(exercise);
                // Notifica l'adapter dei pazienti che i dati sono cambiati
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAssignClick(Exercise exercise) {
                // Implementa l'azione quando il pulsante "Assegna" viene premuto senza un paziente selezionato
                Toast.makeText(ExerciseListActivity.this, "Seleziona un paziente prima di assegnare l'esercizio", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPatientSelected(Patient patient) {
                // Implementa l'azione quando un paziente viene selezionato
                // Puoi anche memorizzare il paziente selezionato per un utilizzo successivo, se necessario
                Toast.makeText(ExerciseListActivity.this, "Paziente selezionato: " + patient.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);
        ImageView indietroButton = findViewById(R.id.topLeftIcon);

        Toolbar toolbar = findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);
        indietroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Rimuovi il titolo predefinito
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Imposta il titolo e il sottotitolo personalizzati
        toolbar.setTitle("Esercizi");
        toolbar.setSubtitle("Logopedista");

        recyclerView = findViewById(R.id.recyclerView);


        getIdBambino();
    }

    private void getNomePaziente(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference pazientiRef = db.collection("Pazienti").document(id);

        pazientiRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    Log.d("TAG", task.getResult().getId());
                    if (document.exists())
                    {
                        nomePaziente.add(document.getString("nome"));
                        loadExercisesAndPatients(nomePaziente);
                    }
                }
                else
                    Log.d("TAG", "Errore!");
            }
        });
    }

    private void loadExercisesAndPatients(List<String> nomePaziente) {
        // Carica la lista di esercizi e pazienti da Firestore
        creazioneAdapter(loadPatientsFromFirestore(nomePaziente));
    }

    private void getIdBambino() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Ottenere un'istanza del database Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Ottieni l'ID dell'utente corrente
            String userId = currentUser.getUid();
            // Ottenere un riferimento alla raccolta "Classifica" nel database Firestore
            CollectionReference patientsRef = db.collection("Bambino - Logopedista");

            patientsRef.whereEqualTo("id_logopedista", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Leggi il nome del paziente dal documento e aggiungilo alla lista
                            String patientID = document.getString("id_bambino");
                            getNomePaziente(patientID);
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private List<Patient> loadPatientsFromFirestore(List<String> nomePaziente) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        List<Patient> patients = new ArrayList<>();

        if (currentUser != null) {

            // Ottenere un'istanza del database Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Ottieni l'ID dell'utente corrente
            String userId = currentUser.getUid();
            // Ottenere un riferimento alla raccolta "Classifica" nel database Firestore
            CollectionReference patientsRef = db.collection("Bambino - Logopedista");
            final boolean[] controllo = {true};
            final int[] cont = {0};
            patientsRef.whereEqualTo("id_logopedista", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("TAG", "numero documenti: " + document.getString("id_bambino") + " numero risultati " + document.getId());
                            String idBambino = document.getString("id_bambino");
                            Log.d("TAG", "idBambino pre if: " + idBambino);
                            // Leggi il nome del paziente dal documento e aggiungilo alla lista
                            if (nomePaziente != null) {
                                int siza = nomePaziente.size();
                                Patient paziente = new Patient();
                                if (cont[0] <= siza)
                                {
                                    paziente = new Patient(nomePaziente.get(cont[0]), idBambino);
                                    Log.d("TAG", "idBambino: " + idBambino);
                                    if (!patients.equals(paziente))
                                        patients.add(paziente);
                                    cont[0]++;
                                }
                                //controllo[0] = false;
                            }
                            //controllo[0] = true;
                        }
                        // Aggiorna l'adapter con i pazienti caricati da Firestore
                        adapter.setPatientList(patients);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

        return patients;

    }

    private List<Exercise> getExerciseList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference gamesRef = db.collection("Giochi");

        final List<Exercise> exercises = new ArrayList<>(); // Inizializza la lista di esercizi

        gamesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Leggi il nome e la categoria dell'esercizio dal documento
                        String exerciseName = document.getString("nome");
                        String exerciseCategory = document.getString("categoria");
                        // Crea un oggetto Exercise con nome e categoria
                        Exercise exercise = new Exercise(exerciseName, exerciseCategory);
                        // Aggiungi l'esercizio alla lista
                        exercises.add(exercise);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return exercises; // Restituisci la lista di esercizi
    }



    private void showDetailsPopup(String details, String exerciseDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dettagli");
        builder.setMessage(details);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chiudi il popup se necessario
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}