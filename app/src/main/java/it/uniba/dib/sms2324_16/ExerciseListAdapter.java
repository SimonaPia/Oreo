package it.uniba.dib.sms2324_16;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        this.patientList = patientList;
        this.listener = listener;
        this.context = context;

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
                Patient selectedPatient = getPatientList().get(which);
                selectedPatient.addAssignedExercise(exercise);
                // Notifica l'activity dell'assegnazione e della necessità di aggiornare l'adapter
                listener.onAssignClick(exercise, selectedPatient, exercisePosition);
                showAssignConfirmationDialog(exercise, selectedPatient, exercisePosition);
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
}
