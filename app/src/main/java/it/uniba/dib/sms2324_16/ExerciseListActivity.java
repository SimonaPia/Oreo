package it.uniba.dib.sms2324_16;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;
    private Patient selectedPatient;

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
        toolbar.setTitle("Mario Rossi");
        toolbar.setSubtitle("Logopedista");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseListAdapter(this, getExerciseList(), getPatientList(), new ExerciseListAdapter.OnItemClickListener() {
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
        recyclerView.setAdapter(adapter);
        Log.d("ExerciseListActivity", "Number of patients in adapter: " + adapter.getItemCount());
    }

    private List<Patient> getPatientList() {
        List<Patient> patients = new ArrayList<>();
        // Aggiungi pazienti alla lista
        patients.add(new Patient("Maria"));
        patients.add(new Patient("Pia"));
        patients.add(new Patient("Mario"));

        Log.d("ExerciseListActivity", "Number of patients: " + patients.size());
        return patients;
    }

    private List<Exercise> getExerciseList() {
        // Simulazione di una lista di esercizi (puoi ottenere dati da un server o altro)
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Esercizio 1 - Denominazione immagini", "Data un'immagine, il bambino deve riconoscere la parola associata all’immagine. Per fornire la risposta il bambino registra un audio utilizzando il microfono del dispositivo. Al bambino saranno inoltre forniti fino a 3 aiuti audio per facilitargli il riconoscimento della parola."));
        exercises.add(new Exercise("Esercizio 2 - Ripetizione di sequenze di parole", "Dato un audio che riproduce 3 parole in sequenza, il bambino dovrà registrare un audio ripetendo le parole nella sequenza corretta."));
        exercises.add(new Exercise("Esercizio 3 - Riconoscimento di coppie minime", "Date due immagini e la riproduzione audio di una parola, il bambino deve riconoscere l’immagine giusta a cui la parola si riferisce."));
        return exercises;
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


