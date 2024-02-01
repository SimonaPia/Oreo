package it.uniba.dib.sms2324_16;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseViewHolder> {
    private List<ExerciseItem> exerciseItemList;

    public ExerciseAdapter(List<ExerciseItem> exerciseItemList) {
        this.exerciseItemList = exerciseItemList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_esercizi, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseItem item = exerciseItemList.get(position);

        holder.childNameTextView.setText("Bambino: " + item.getChildName());
        holder.exerciseTypeTextView.setText("Tipo di esercizio: " + item.getExerciseType());

        String completedStatus = item.isExerciseCompleted() ? "Fatto" : "Non fatto";
        holder.completedStatusTextView.setText("Stato: " + completedStatus);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chiamato quando un elemento della RecyclerView viene cliccato
                // Mostra un dialog qui
                showDialog(item, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseItemList.size();
    }

    private void showDialog(ExerciseItem exerciseItem, @NonNull ExerciseViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Dettagli dell'esercizio");
        builder.setMessage("Tipo di esercizio: " + exerciseItem.getExerciseType() +
                "\nRisposta: " + exerciseItem.getRisposta());

        builder.setPositiveButton("Corretto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aggiornaMonete(3, exerciseItem.getIdBambino());
                // Chiudi il dialog
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Sbagliato", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // Mostra il dialog
        builder.show();
    }

    private void aggiornaMonete(int incremento, String idBambino) {
        // Inizializza Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("TAG", idBambino);
        // Ottieni un riferimento al documento che vuoi aggiornare
        DocumentReference documentRef = db.collection("Pazienti").document(idBambino);

        // Esegui l'aggiornamento del campo specifico nel documento
        documentRef
                .update("monete", incremento)
                .addOnSuccessListener(aVoid -> {
                    // L'aggiornamento è avvenuto con successo
                    Log.d("TAG", "Campo aggiornato con successo!");
                })
                .addOnFailureListener(e -> {
                    // Si è verificato un errore durante l'aggiornamento
                    Log.w("TAG", "Errore durante l'aggiornamento del campo", e);
                });

    }
}

