package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder> {

    private List<Exercise> exerciseList;
    private List<Patient> patientList;
    private Patient selectedPatient;
    private Exercise selectedExercise;
    private OnItemClickListener listener;
    private Context context;
    private List<Patient> patients;

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onDetailsClick(Exercise exercise, String details, String exerciseDetails);
        void onAssignClick(Exercise exercise, Patient selectedPatient, int position);
        void onAssignClick(Exercise exercise);
        void onPatientSelected(Patient patient);
    }

    // Dichiarazione dell'oggetto dialog
    private AlertDialog dialog;

    //Costruttore
    public ExerciseListAdapter(Context context, List<Exercise> exerciseList, List<Patient> patientList, OnItemClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
        this.context = context;
        this.patientList = new ArrayList<>();

        // Inizializza l'oggetto dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seleziona un paziente");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_patient_list, null);
        RecyclerView recyclerViewPatients = view.findViewById(R.id.recyclerViewPatients);
        recyclerViewPatients.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewPatients.setAdapter(new PatientListAdapter(context, patientList, new PatientListAdapter.OnItemClickListener() {
            @Override
            public void onAssignClick(Patient patient) {
                // Assegna il paziente selezionato
                selectedPatient = patient;
                // Ottieni la posizione corrente
                int exercisePosition = exerciseList.indexOf(selectedExercise);
                // Richiama il listener per assegnare l'esercizio al paziente
                listener.onAssignClick(exerciseList.get(exercisePosition), selectedPatient, exercisePosition);
                // Chiudi il dialog dopo aver assegnato
                dialog.dismiss();
            }
        }));

        builder.setView(view);
        dialog = builder.create();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.bind(exercise, listener);

        // Ottieni riferimenti ai bottoni dal layout item_exercise.xml
        Button detailsButton = holder.itemView.findViewById(R.id.details_button_id);
        Button assignButton = holder.itemView.findViewById(R.id.assign_button_id);

        // Imposta il colore dei bottoni
        detailsButton.setBackgroundColor(ContextCompat.getColor(context, R.color.blu_oltremare));
        assignButton.setBackgroundColor(ContextCompat.getColor(context, R.color.blu_oltremare));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseNameTextView;
        private Button detailsButton;
        private Button assignButton;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exercise_name_id);
            detailsButton = itemView.findViewById(R.id.details_button_id);
            assignButton = itemView.findViewById(R.id.assign_button_id);
        }

        public void bind(final Exercise exercise, final OnItemClickListener listener) {
            exerciseNameTextView.setText(exercise.getName());

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDetailsClick(exercise, exercise.getName(), exercise.getDetails());
                }
            });

            assignButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedExercise = exercise;
                    // Mostra il dialog quando il pulsante "Assegna" viene premuto
                    showAssignExerciseDialog(selectedExercise, getBindingAdapterPosition(), context);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Richiama il listener solo quando il paziente è stato selezionato
                    if (selectedPatient != null) {
                        showAssignConfirmationDialog(exercise, selectedPatient, getBindingAdapterPosition());
                    } else {
                        // Mostriamo un messaggio se il paziente non è stato selezionato
                        Toast.makeText(context, "Seleziona un paziente prima di assegnare l'esercizio", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Mostra un popup con i dettagli
                    showDetailsPopup(exercise);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Notify the listener when a patient is selected
                    listener.onPatientSelected(selectedPatient);
                    Log.d("ExerciseListAdapter", "Patient selected: " + selectedPatient.getName());
                }
            });
        }
    }

    private void showDetailsPopup(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dettagli: " + exercise.getName());
        builder.setMessage(exercise.getDetails());
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

    private void showDateSelectionDialog(final Exercise exercise, final int exercisePosition, final Patient selectedPatient, Context activityContext) {
        // Utilizza un DatePickerDialog per permettere all'utente di selezionare una data
        DatePickerDialog datePickerDialog = new DatePickerDialog(activityContext);

        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Qui puoi gestire la data selezionata dall'utente
                // Esempio: puoi assegnare l'esercizio al paziente selezionato per la data scelta
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                // Assegna l'esercizio al paziente selezionato
                selectedPatient.addAssignedExercise(exercise);

                // Notifica l'activity dell'assegnazione e della necessità di aggiornare l'adapter
                listener.onAssignClick(exercise, selectedPatient, exercisePosition);
                showAssignConfirmationDialog(exercise, selectedPatient, exercisePosition);
                saveAssignmentToFirestore(exercise, selectedPatient, calendar.getTime());

            }
        });

        datePickerDialog.show();
    }

    private void saveAssignmentToFirestore(Exercise exercise, Patient selectedPatient, Date selectedDate) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> assignment = new HashMap<>();
            assignment.put("exercise_name", exercise.getName());
            assignment.put("patient_id", selectedPatient.getId());
            assignment.put("date", selectedDate); // Salva la data selezionata

            db.collection("assignments")
                    .add(assignment)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Assignment added with ID: " + documentReference.getId());
                        // Gestisci l'aggiunta del documento con successo
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding assignment", e);
                        // Gestisci eventuali errori
                    });
        }
    }

    private void showAssignExerciseDialog(final Exercise exercise, final int exercisePosition, Context activityContext) {
        // Verifica se ci sono pazienti disponibili
        List<Patient> patientList = getPatientList();
        if (patientList.isEmpty()) {
            Toast.makeText(activityContext, "Nessun paziente disponibile. Aggiungi pazienti prima di assegnare gli esercizi.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creazione della lista di nomi dei pazienti
        CharSequence[] patientNames = new CharSequence[patientList.size()];
        for (int i = 0; i < patientList.size(); i++) {
            patientNames[i] = patientList.get(i).getName();
        }

        // Creazione del dialog per l'assegnazione dell'esercizio
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle("Seleziona un paziente");
        builder.setItems(patientNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Assegna l'esercizio al paziente selezionato
                Patient selectedPatient = patientList.get(which);
                selectedPatient.addAssignedExercise(exercise);
                // Mostra il dialog della data dopo che l'utente ha selezionato un paziente
                showDateSelectionDialog(exercise, exercisePosition, selectedPatient, activityContext);
            }
        });

        AlertDialog dialog = builder.create();
        Log.d("ExerciseListAdapter", "Showing assign exercise dialog");
        dialog.show();
    }

    private List<Patient> getPatientList() {
        // Aggiungi qui la tua implementazione per ottenere la lista dei pazienti
        // In questo esempio, restituisco una lista vuota per dimostrazione
        return patientList;
    }

    private void showAssignConfirmationDialog(final Exercise exercise, final Patient selectedPatient, final int exercisePosition) {
        Log.d("ExerciseListAdapter", "showAssignConfirmationDialog called");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Conferma assegnazione");
        builder.setMessage("Vuoi assegnare l'esercizio a " + selectedPatient.getName() + "?");
        builder.setPositiveButton("Sì", (dialog, which) -> {
            // Assegna l'esercizio al paziente selezionato
            selectedPatient.addAssignedExercise(exercise);

            // Notifica l'activity dell'assegnazione e della necessità di aggiornare l'adapter
            if (listener != null) {
                listener.onAssignClick(exercise, selectedPatient, exercisePosition);
                Log.d("ExerciseListAdapter", "Exercise assigned to " + selectedPatient.getName());
            }

            Toast.makeText(context, "Esercizio assegnato a " + selectedPatient.getName(), Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            Log.d("ExerciseListAdapter", "Negative button clicked");
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void setPatientList(List<Patient> patients) {
        this.patientList.clear();
        this.patientList.addAll(patients);
        notifyDataSetChanged(); // Notifica l'adapter dei cambiamenti
    }
    public void setExerciseList(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
        notifyDataSetChanged();
    }

}
