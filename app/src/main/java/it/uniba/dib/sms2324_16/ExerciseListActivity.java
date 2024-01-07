package it.uniba.dib.sms2324_16;

// ExerciseListActivity.java

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseListAdapter(getExerciseList(), new ExerciseListAdapter.OnItemClickListener() {
            @Override
            public void onDetailsClick(Exercise exercise) {
                // Implementa l'azione quando il pulsante "Dettagli" viene premuto
                Toast.makeText(ExerciseListActivity.this, "Dettagli di " + exercise.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAssignClick(Exercise exercise) {
                // Implementa l'azione quando il pulsante "Assegna" viene premuto
                Toast.makeText(ExerciseListActivity.this, "Assegna " + exercise.getName() + " a un paziente", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private List<Exercise> getExerciseList() {
        // Simulazione di una lista di esercizi (puoi ottenere dati da un server o altro)
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Esercizio 1"));
        exercises.add(new Exercise("Esercizio 2"));
        exercises.add(new Exercise("Esercizio 3"));
        // Aggiungi altri esercizi secondo le tue esigenze
        return exercises;
    }
}

